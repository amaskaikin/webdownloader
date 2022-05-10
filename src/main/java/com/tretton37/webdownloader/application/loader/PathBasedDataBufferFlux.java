package com.tretton37.webdownloader.application.loader;

import lombok.Data;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@Data
public class PathBasedDataBufferFlux {
    private final Flux<DataBuffer> dataBufferFlux;
    private final Path path;

    public PathBasedDataBufferFlux(Flux<DataBuffer> flux, Path path) {
        this.dataBufferFlux = flux;
        this.path = path;
    }

    public Mono<Void> write() {
        return DataBufferUtils
                .write(dataBufferFlux, path, CREATE, TRUNCATE_EXISTING)
                .subscribeOn(Schedulers.parallel());
    }
}
