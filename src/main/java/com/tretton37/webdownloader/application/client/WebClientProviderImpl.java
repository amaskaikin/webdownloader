package com.tretton37.webdownloader.application.client;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Component
@Slf4j
public class WebClientProviderImpl implements WebClientProvider {
    // ToDo: Configure timeouts in .yaml

    @Override
    public WebClient getWebClient(String url) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(60))
                                .addHandlerLast(new ReadTimeoutHandler(60))
                );

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(ExchangeLoggingFilterFunctions.logRequest())
                .filter(ExchangeLoggingFilterFunctions.logResponse())
                .baseUrl(url)
                .build();
    }
}
