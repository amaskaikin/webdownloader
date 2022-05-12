package com.tretton37.webdownloader.application.loader;

import com.tretton37.webdownloader.application.client.WebClientProvider;
import me.tongfei.progressbar.ProgressBar;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class WebPageDownloaderImplTest {

    @SpyBean
    private WebPageDownloaderImpl webPageDownloader;
    @MockBean
    private WebClientProvider clientProvider;
    @Mock
    private ProgressBar progressBar;
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec<?> webClientRequest;
    @Mock
    private WebClient.ResponseSpec webClientResponse;
    @Mock
    private Flux<DataBuffer> flux;
    @Mock
    private PathBasedDataBufferFlux pathBasedDataBufferFlux;

    @Before
    public void setUp() {
        doReturn(webClient).when(clientProvider).getWebClient(anyString());
        doReturn(webClientRequest).when(webClient).get();
        doReturn(webClientResponse).when(webClientRequest).retrieve();
        doReturn(flux).when(webClientResponse).bodyToFlux(DataBuffer.class);
        doReturn(progressBar).when(progressBar).step();
    }

    @Test
    public void testDownloadAsync_shouldWriteAllUrls() throws MalformedURLException {
        doReturn(pathBasedDataBufferFlux).when(webPageDownloader)
                .createPathDataBufferFlux(eq(flux), any(Path.class));
        doReturn(Mono.empty()).when(pathBasedDataBufferFlux).write();
        Set<URL> urls= new HashSet<>(Arrays.asList(new URL("https://tretton37.com/"),
                new URL("https://tretton37.com/who-we-are")));

        webPageDownloader.downloadAsync(urls, progressBar);

        verify(pathBasedDataBufferFlux, times(2)).write();
        verify(progressBar, times(2)).step();
    }
}
