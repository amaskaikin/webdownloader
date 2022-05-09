package com.tretton37.webdownloader.application;

import com.tretton37.webdownloader.application.traverse.WebTraversalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Set;

@Component
@Slf4j
public class ApplicationRunner implements CommandLineRunner {

    private final WebTraversalService traversalService;

    @Autowired
    public ApplicationRunner(WebTraversalService traversalService) {
        this.traversalService = traversalService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("RUN " + LocalDateTime.now());

        Set<URL> urls = traversalService.traverse();

        System.out.println("RUN END "  + LocalDateTime.now());
    }
}
