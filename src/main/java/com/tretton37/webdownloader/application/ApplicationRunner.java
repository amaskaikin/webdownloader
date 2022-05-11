package com.tretton37.webdownloader.application;

import com.tretton37.webdownloader.application.loader.WebPageDownloader;
import com.tretton37.webdownloader.application.traverse.WebTraversalService;
import lombok.extern.slf4j.Slf4j;
import me.tongfei.progressbar.ProgressBar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Set;

@Component
@Slf4j
public class ApplicationRunner implements CommandLineRunner {

    @Value("${webdownloader.base-url}")
    private String baseUrl;

    private final WebTraversalService traversalService;
    private final WebPageDownloader downloader;

    @Autowired
    public ApplicationRunner(WebTraversalService traversalService, WebPageDownloader downloader) {
        this.traversalService = traversalService;
        this.downloader = downloader;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Application start");
        System.out.printf("Fetching %s content info... %n", baseUrl);

        Set<URL> urls = traversalService.traverse(baseUrl);

        System.out.printf("Total content size: %s%n", urls.size());

        try (ProgressBar progressBar = new ProgressBar("Downloading...", urls.size())) {
            downloader.downloadAsync(urls, progressBar);
        }

        System.out.println("Web Content downloaded successfully");
        System.out.println("Application finish");
    }
}
