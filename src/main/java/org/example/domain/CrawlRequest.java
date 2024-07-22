package org.example.domain;

import lombok.Data;
import org.jsoup.nodes.Element;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

//This would be an entity managed by spring; I haven't added Hibernate to the classpath
@Data
public class CrawlRequest {
    private ConcurrentHashMap<String, Boolean> ledger;
    private String baseDomain;

    public CrawlRequest(String baseDomainUrl) {
        this.baseDomain = baseDomainUrl;
        this.ledger = new ConcurrentHashMap<>();
        //initialise the map as we know it's new
        ledger.putIfAbsent(baseDomainUrl, true);
    }

    public boolean hasLinkBeenVisited(String key) {
        return ledger.getOrDefault(key, false);
    }

    public Consumer<? super Element> safelyAddToLedger(String link) {
        return null;
    }

    public void setLinkAsVisited(String link) {
        //Safely sets the state of the link in the map.
        //If the value is present and is not already visited, set it to true/visited
        ledger.computeIfPresent(link, (key, value) -> !value);
    }
}
