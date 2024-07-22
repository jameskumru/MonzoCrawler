package org.example.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.example.domain.CrawlRequest;
import org.example.ports.CrawlerRepository;
import org.example.ports.WebClient;
import org.example.validators.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrawlerTest {
    private ListAppender<ILoggingEvent> logWatcher;

    @Mock
    private CrawlerRepository crawlerRepository;

    @Mock
    private UrlValidator urlValidator;

    @Mock
    private WebClient webClient;

    @Mock
    private Jsoup jsoup;

    @InjectMocks
    private Crawler crawler;

    @BeforeEach
    void setUp() {
        logWatcher = new ListAppender<>();
        logWatcher.start();

        ((Logger) LoggerFactory.getLogger(Crawler.class)).addAppender(logWatcher);

        when(crawlerRepository.findByDomain(any())).thenReturn(null);
    }

    @Test
    void crawlShouldLogOnStartup() {
        var args = (new String[]{"https://www.monzo.com", "2"});
        crawler.crawl(args);

        assertThat(logWatcher.list.get(1).getFormattedMessage()).isEqualTo("Beginning to crawl");
    }

    @Test
    void crawlShouldMapAllSubLinks() throws InterruptedException, IOException {
        // Setup
        String baseDomainUri = "https://example.com/";
        String baseDomainUrl = "https://www.example.com/";
        String newUri = "https://example.com/new";

        CrawlRequest crawlRequest = new CrawlRequest(baseDomainUrl);

        when(crawlerRepository.findByDomain(baseDomainUrl)).thenReturn(crawlRequest);

        // Mock Jsoup connection
        Elements elements = TextFixtures.buildElements(baseDomainUri, "/new");
        when(webClient.getLinksOnPage(anyString())).thenReturn(elements);

        // Mock ledger behavior
        doNothing().when(crawlerRepository).persist(any(CrawlRequest.class));

        // Call the method under test
        crawler.crawl(new String[]{baseDomainUrl, "2"});

        // Wait for some time to ensure all tasks complete
        Thread.sleep(2000);

        // Validate the behavior and assert results
        assertThat(crawlRequest.getLedger()).containsEntry(newUri, true);

        // Verify interactions
        verify(crawlerRepository, times(2)).persist(any(CrawlRequest.class));
    }
}