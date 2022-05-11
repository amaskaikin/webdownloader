package com.tretton37.webdownloader.application.loader;

import com.tretton37.webdownloader.application.client.WebClientProvider;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.tongfei.progressbar.ProgressBar;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Component;
import reactor.util.annotation.Nullable;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@Component
@Slf4j
public class WebPageDownloaderImpl implements WebPageDownloader {

    @Value("${webdownloader.base-dir}")
    private String baseDir;

    private final WebClientProvider clientProvider;

    @Autowired
    public WebPageDownloaderImpl(WebClientProvider clientProvider) {
        this.clientProvider = clientProvider;
    }

    @Override
    public void downloadAsync(Set<URL> urls) {
        this.downloadAsync(urls, null);
    }

    @Override
    public void downloadAsync(final Set<URL> urls, @Nullable final ProgressBar progressBar) {
        urls.parallelStream()
                .map(url -> new PathBasedDataBufferFlux(
                        clientProvider.getWebClient(url.toString())
                                .get()
                                .retrieve()
                                .bodyToFlux(DataBuffer.class),
                        getPath(url))
                )
                .forEach(dataBufferFlux -> dataBufferFlux.write()
                        .doOnSuccess(e -> {
                            if (progressBar != null) {
                                progressBar.step();
                            }
                        })
                        .block());
    }

    private Path getPath(URL url) {
        Path result = computeValidTargetPath(url);
        try {
            Files.createDirectories(result.getParent());
        } catch (IOException e) {
            log.error("getTargetPath: Failed to create target directories: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        log.trace("getPath: Result path: {}", result);
        return result;
    }

    private Path computeValidTargetPath(@NonNull URL url) {
        String path = url.getPath();
        StringBuilder builder = new StringBuilder(url.getPath());
        if ("/".equals(path)) {
            builder.append("index");
        }
        if (StringUtils.isEmpty(FilenameUtils.getExtension(path))) {
            builder.append(FilenameUtils.EXTENSION_SEPARATOR);
            builder.append("html");
        }

        return Paths.get(baseDir, builder.toString());
    }
}
