package org.example.ports;

import org.jsoup.select.Elements;

public interface WebClient {
    Elements getLinksOnPage(String target);
}
