package com.tretton37.webdownloader.application;

import com.tretton37.webdownloader.application.loader.WebPageDownloader;
import com.tretton37.webdownloader.application.traverse.WebTraversalService;
import me.tongfei.progressbar.ProgressBar;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class ApplicationRunnerTest {

    @MockBean
    private WebTraversalService traversalService;
    @MockBean
    private WebPageDownloader webPageDownloader;

    private ApplicationRunner applicationRunnerSpy;

    @Before
    public void setUp() {
        applicationRunnerSpy = spy(new ApplicationRunner(traversalService, webPageDownloader));
    }

    @Test
    public void testRun() throws Exception {
        Set<URL> urls = new HashSet<>();
        when(traversalService.traverse(any())).thenReturn(urls);
        doNothing().when(webPageDownloader).downloadAsync(anySet(), any(ProgressBar.class));

        applicationRunnerSpy.run();

        verify(traversalService).traverse(any());
        verify(webPageDownloader).downloadAsync(eq(urls), any(ProgressBar.class));
    }
}
