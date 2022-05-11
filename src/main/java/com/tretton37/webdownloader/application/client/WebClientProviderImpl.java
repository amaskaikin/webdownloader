package com.tretton37.webdownloader.application.client;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Component
@Slf4j
public class WebClientProviderImpl implements WebClientProvider {

    @Value("${webdownloader.integration.connection-timeout-ms}")
    private Integer connectionTimeout;
    @Value("${webdownloader.integration.read-timeout-s}")
    private Integer readTimeout;
    @Value("${webdownloader.integration.write-timeout-s}")
    private Integer writeTimeout;

    @Override
    public WebClient getWebClient(String url) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(readTimeout))
                                .addHandlerLast(new WriteTimeoutHandler(writeTimeout))
                );

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(ExchangeLoggingFilterFunctions.logRequest())
                .filter(ExchangeLoggingFilterFunctions.logResponse())
                .baseUrl(url)
                .build();
    }
}
