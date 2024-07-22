package org.example.adapters;

import lombok.extern.slf4j.Slf4j;
import org.example.ports.WebClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@Slf4j
public class WebClientImpl implements WebClient {
    @Override
    public Elements getLinksOnPage(String target) {
        Document document = null;
        try {
            document = Jsoup.connect(target).get();
        } catch (IOException e) {
            //404s are loud
        }
        log.info("Connected to {}", target);
       return document.select("a[href]");
    }
}
