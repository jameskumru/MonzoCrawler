package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.service.Crawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class CrawlerConsoleApplication implements CommandLineRunner {
    private static final String USAGE_ERROR_MESSAGE = "Usage: java crawler <start-url>";

    @Autowired
    private Crawler crawler;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println(USAGE_ERROR_MESSAGE);
        }

        SpringApplication.run(CrawlerConsoleApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        crawler.crawl(args);
    }
}