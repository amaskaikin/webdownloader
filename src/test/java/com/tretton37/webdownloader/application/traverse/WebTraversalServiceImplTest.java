package com.tretton37.webdownloader.application.traverse;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class WebTraversalServiceImplTest {

    @Value("${html.file}")
    private String htmlFile;

    @Autowired
    private ApplicationContext applicationContext;

    @SpyBean
    private WebTraversalServiceImpl webTraversalService;
    @Mock
    private Connection jsoupConnection;
    @Mock
    private Connection.Response jsoupResponse;

    @Before
    public void setUp() throws Exception {
        doReturn(jsoupConnection).when(webTraversalService)
                .getJsoupConnection(anyString());
        doReturn(jsoupResponse).when(jsoupConnection).execute();
        when(jsoupResponse.contentType()).thenReturn(MediaType.TEXT_HTML.getType());
    }

    @Test
    public void testTraverse() throws Exception {
        doReturn(getTestHtmlPage()).when(jsoupResponse).parse();

        Set<URL> urls = webTraversalService.traverse("https://tretton37.com/");

        assertEquals(36, urls.size());
        assertTrue(urls.contains(new URL("https://tretton37.com/contact")));
    }

    @Test
    public void testTraverse_shouldIgnoreNonHtmlContent() throws Exception {
        when(jsoupResponse.contentType()).thenReturn(MediaType.APPLICATION_JSON.getType());
        doReturn(getTestHtmlPage()).when(jsoupResponse).parse();

        Set<URL> urls = webTraversalService.traverse("https://tretton37.com/");

        assertEquals(0, urls.size());
    }

    private Document getTestHtmlPage() throws IOException {
        Resource resource = applicationContext.getResource(
                "classpath:" + htmlFile);
        return Jsoup.parse(new String(Files.readAllBytes(
                Paths.get(resource.getURI())))
        );
    }
}
