package org.example.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.adapters.FileUtils;
import org.example.domain.CrawlRequest;
import org.example.ports.CrawlerRepository;
import org.example.ports.WebClient;
import org.example.validators.UrlValidator;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
@Getter
public class Crawler {

    @Autowired
    private CrawlerRepository crawlerRepository;

    @Autowired
    private WebClient webClient;

    private final UrlValidator validator = new UrlValidator();
    private final ConcurrentLinkedQueue<String> linksToVisit = new ConcurrentLinkedQueue<>();
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    public void crawl(String[] args) {
        // Validate input
        var baseDomainUrl = validator.getValidBaseUrl(args);
        log.info("Base Domain URL: {}", baseDomainUrl);

        // Check if domain exists in DB and load progress if present
        var crawlerRequest = Optional.ofNullable(crawlerRepository.findByDomain(baseDomainUrl))
                .orElseGet(() -> new CrawlRequest(baseDomainUrl));

        log.info("Beginning to crawl");

        //initialises the list
        if (linksToVisit.isEmpty()) {
            linksToVisit.add(crawlerRequest.getBaseDomain());
        }

        // CountDownLatch to wait for all tasks to complete
        // Without this, we can't log the output at the end
        CountDownLatch latch = new CountDownLatch(linksToVisit.size() + 1); // +1 for the initial task

        // Schedule a task to stop crawling after 10 seconds - allows us to add rate limiting
        scheduleStopCommand(latch, crawlerRequest, args[1]);

        // Process the initial URL
        String initialTarget = linksToVisit.poll();
        if (initialTarget != null) {
            executor.submit(() -> {
                try {
                    findAndPersistAllLinks(initialTarget, crawlerRequest);
                } finally {
                    latch.countDown(); // Ensure latch count is decremented even if an exception occurs
                }
                log.info("Processed initial link: {}", initialTarget);
            });
        }

        // Process remaining links
        while (isRunning.get()) {
            String target = linksToVisit.poll();

            if (target == null) {
                // If the queue is empty, pause and retry
                try {
                    Thread.sleep(100); // sleep for a short while to prevent tight loop
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupt status
                }
                continue;
            }

            // Submit a new task to the thread pool
            executor.submit(() -> {
                try {
                    findAndPersistAllLinks(target, crawlerRequest);
                } finally {
                    latch.countDown(); // Ensure latch count is decremented even if an exception occurs
                }
                log.info("Processed link: {}", target);
            });
        }

        log.info("No more links to follow...");
    }

    private void scheduleStopCommand(CountDownLatch latch, CrawlRequest crawlerRequest, String arg) {
        executor.schedule(() -> {
            isRunning.set(false);
            executor.shutdown();
            try {
                if (!executor.awaitTermination(Integer.parseInt(arg), TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt(); // Restore interrupt status
            }

            // Wait for all tasks to finish
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupt status
            }

            // Log the contents of the ledger to a temporary file
            FileUtils.logToFile(crawlerRequest);
        }, 10, TimeUnit.SECONDS);
    }

    void findAndPersistAllLinks(String target, CrawlRequest request) {
        var links = webClient.getLinksOnPage(target);

        var filteredLinks = links.stream()
                .map(element -> getIfValidUrl(element, request))
                .filter(Objects::nonNull)
                .toList();

        filteredLinks.forEach(link -> {
            if (!request.getLedger().containsKey(link)) {
                request.getLedger().put(link, false);
                linksToVisit.add(link);
                log.info("Added new link to queue and map: {}", link);
            }
        });

        request.setLinkAsVisited(target);
        log.info("Visited: {}", target);

        synchronized (this) {
            crawlerRepository.persist(request);
        }
    }

    //This is just some URL manipulation and validation. Should be moved to other classes
    private String getIfValidUrl(Element element, CrawlRequest request) {
        var baseUri = element.baseUri();
        var extension = element.attr("href");
        var proposedUrl = baseUri + stripLeadingSlash(extension);

        return (isSameDomain(baseUri, request) && !request.hasLinkBeenVisited(proposedUrl) && extension.matches("^/.*")) ? proposedUrl : null;
    }

    private String stripLeadingSlash(String extension) {
        return extension.replaceFirst("^/", "");
    }

    private boolean isSameDomain(String target, CrawlRequest crawlerRequest) {
        return target.contains(crawlerRequest.getBaseDomain().replace("www.", ""));
    }
}
