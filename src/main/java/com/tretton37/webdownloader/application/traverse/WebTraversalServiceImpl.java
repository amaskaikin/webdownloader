package com.tretton37.webdownloader.application.traverse;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class WebTraversalServiceImpl implements WebTraversalService {

    @Override
    public Set<URL> traverse() {
        final Set<String> visited = Collections.synchronizedSet(new HashSet<>());
        // ToDo: register webservice somewhere else
        Set<URL> result = retrieveSiteContent(visited, "https://tretton37.com/")
                .parallel()
                .map(e -> retrieveSiteContent(visited, e))
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

    private Stream<String> retrieveSiteContent(final Set<String> visited, final String relativeUrl) {
        // ToDo: improve synchronization
        if (visited.contains(relativeUrl)) {
            return Stream.empty();
        }
        visited.add(relativeUrl);

        try {
            Connection.Response jsoupResponse = Jsoup.connect(relativeUrl)
                    .ignoreContentType(Boolean.TRUE)
                    .execute();
            if (!Objects.requireNonNull(jsoupResponse.contentType())
                    .contains(MediaType.TEXT_HTML.getType())) {
                return Stream.empty();
            }
            Document document = jsoupResponse.parse();

            return WebContentExtractor.fromDocument(document, "https://tretton37.com/")
                    .links("a", "link")
                    .assets("img", "script", "video")
                    .extract();

        }
        catch (IOException e) {
            // ToDo: Replace with custom exception
            throw new RuntimeException(e);
        }
    }
}
