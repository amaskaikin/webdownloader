package com.tretton37.webdownloader.application.client;

import org.springframework.web.reactive.function.client.WebClient;

public interface WebClientProvider {

    WebClient getWebClient(String url);

}
