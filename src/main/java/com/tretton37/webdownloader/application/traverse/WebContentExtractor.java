package com.tretton37.webdownloader.application.traverse;

import org.jsoup.nodes.Document;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class WebContentExtractor {

    private final Document document;
    private final String baseUrl;
    private List<String> linkTags;
    private List<String> assetTags;

    private WebContentExtractor(Document document, String baseUrl) {
        this.document = document;
        this.baseUrl = baseUrl;
    }

    public static WebContentExtractor fromDocument(Document document, String baseUrl) {
        return new WebContentExtractor(document, baseUrl);
    }

    public WebContentExtractor links(String... linkTags) {
        this.linkTags = Arrays.asList(linkTags);
        return this;
    }

    public WebContentExtractor assets(String... assetTags) {
        this.assetTags = Arrays.asList(assetTags);
        return this;
    }

    public Stream<String> extract() {
        return Stream.concat(extractLinks(), extractAssets());
    }

    private Stream<String> extractLinks() {
        if (CollectionUtils.isEmpty(linkTags)) {
            return Stream.empty();
        }

        return document.select(createOrCssSelector(linkTags))
                .parallelStream()
                .map(e -> e.absUrl("href"))
                .filter(Predicate.not(String::isEmpty))
                .filter(url -> url.contains(baseUrl))
                .filter(url -> !url.contains("#"));
    }

    private Stream<String> extractAssets() {
        if (CollectionUtils.isEmpty(assetTags)) {
            return Stream.empty();
        }

        return document.select(createOrCssSelector(assetTags))
                .parallelStream()
                .map(e -> e.absUrl("src"))
                .filter(Predicate.not(String::isEmpty))
                .filter(url -> url.contains(baseUrl));
    }

    private String createOrCssSelector(List<String> tags) {
        return String.join(",", tags);
    }
}
