package org.example.ports;

import org.example.domain.CrawlRequest;

public interface CrawlerRepository {
    //Map<url, parsed> stores the url to follow and if it's been searched or not
    CrawlRequest findByDomain(String domain);

    void persist(CrawlRequest crawlRequest);
}
