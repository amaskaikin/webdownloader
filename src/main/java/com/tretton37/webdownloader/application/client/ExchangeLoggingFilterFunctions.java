package com.tretton37.webdownloader.application.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

public class ExchangeLoggingFilterFunctions {
    private static final Logger log = LoggerFactory.getLogger(WebClientProviderImpl.class);

    public static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            if (log.isTraceEnabled()) {
                log.trace("Request: \n\turl={}\n\tmethod={}\n\t", request.url(), request.method());

                log.trace("Headers: \n{}", request.headers().entrySet()
                        .stream()
                        .map(entry -> entry.getKey() + "=" + entry.getValue())
                        .collect(Collectors.joining("\n ==> "))
                );

                log.trace("Request: body={}", request.body());
            } else if (log.isDebugEnabled()) {
                log.debug("Request: \n\turl={}\n\tmethod={}\n\theaders={}",
                        request.url(), request.method(), request.headers().keySet());
            }

            return Mono.just(request);
        });
    }

    public static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            if (log.isDebugEnabled()) {
                log.debug("Response: \n\tstatuscode={}\n\tbodyToMono={}\n\theaders={}",
                        response.statusCode(), response.bodyToMono(String.class),
                        response.headers().asHttpHeaders().keySet());
            }
            return Mono.just(response);
        });
    }
}
