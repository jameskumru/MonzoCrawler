package org.example.adapters;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.CrawlRequest;
import org.example.service.Crawler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class FileUtils {
    public static void logToFile(CrawlRequest crawlerRequest) {
        Path tempFile;
        try {
            tempFile = Files.createTempFile("ledger", ".txt");
            try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
                log.info("Writing ledger contents to temporary file: {}", tempFile.toAbsolutePath());
                crawlerRequest.getLedger().forEach((url, visited) -> {
                    try {
                        writer.write(String.format("URL: %s - Visited: %s%n", url, visited));
                    } catch (IOException e) {
                        log.error("Error writing to temporary file", e);
                    }
                });
            }
        } catch (IOException e) {
            log.error("Error creating temporary file", e);
        }
    }
}
