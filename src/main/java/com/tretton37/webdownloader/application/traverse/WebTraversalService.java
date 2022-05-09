package com.tretton37.webdownloader.application.traverse;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class WebTraversalService {

    public Set<URL> traverse() {
        final Set<String> visited = Collections.synchronizedSet(new HashSet<>());
        // ToDo: register webservice somewhere else
        Set<URL> result = getRelativeRefs(visited, "https://tretton37.com/")
                .parallel()
                .map(e -> getRelativeRefs(visited, e))
                .reduce(Stream::concat).orElse(Stream.empty())
                .map(url -> {
                    try {
                        return new URL(url);
                    } catch (MalformedURLException e) {
                        log.error("traverse: Couldn't parse URL string: {}", url);
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());

        log.info("traverse: URLs to download count: {}", result.size());

        return result;
    }

    private Stream<String> getRelativeRefs(final Set<String> visited, final String relativeUrl) {

        if (visited.contains(relativeUrl)) {
            return Stream.empty();
        }
        visited.add(relativeUrl);

        try {
            Document document = Jsoup.connect(relativeUrl).get();
            return document.select("a")
                    .parallelStream()
                    .map(e -> e.absUrl("href"))
                    .filter(Predicate.not(String::isEmpty))
                    .filter(url -> url.contains("https://tretton37.com/"))
                    .filter(url -> !url.contains("#"));

        }
        catch (IOException e) {
            // ToDo: Replace with custom exception
            throw new RuntimeException(e);
        }
    }
}
