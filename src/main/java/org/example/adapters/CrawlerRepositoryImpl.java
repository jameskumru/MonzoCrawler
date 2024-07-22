package org.example.adapters;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.CrawlRequest;
import org.example.ports.CrawlerRepository;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class CrawlerRepositoryImpl implements CrawlerRepository {
    @Override
    public CrawlRequest findByDomain(String domain) {
        log.info("No previous request found.");
        return null;
    }

    @Override
    public synchronized void persist(CrawlRequest crawlRequest) {
        //Would use Hikari connection pooling and persist to the DB ensuring transaction safety
    }
}
