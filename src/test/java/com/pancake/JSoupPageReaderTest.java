package com.pancake;

import com.pancake.pagereader.JSoupPageReader;
import com.pancake.pagereader.PageReadException;
import com.pancake.pagereader.UrlProcessor;
import mockit.Mock;
import mockit.MockUp;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JSoupPageReaderTest {

    private UrlProcessor urlProcessor = mock(UrlProcessor.class);
    private JSoupPageReader jSoupPageReader;

    @Captor
    private ArgumentCaptor<Set<String>> parsedLinksCaptor;

    @Before
    public void setup() {
        jSoupPageReader = new JSoupPageReader(urlProcessor);
    }

    @Test
    public void shouldGetChildLinks() throws IOException {

        //given
        String baseUrl = "https://monzo.com";
        String htmlFilePath = "test.html";
        Set<String> expectedChildLinks = new HashSet<>();
        Document document = getDocument(htmlFilePath, baseUrl);
        mockJsoup(baseUrl, document);

        when(urlProcessor.getNormalizedChildUrls(eq(baseUrl), anySet())).thenReturn(expectedChildLinks);

        //when
        Set<String> childLinks = jSoupPageReader.getChildLinks(baseUrl);

        //then
        assertThat(childLinks).isEqualTo(expectedChildLinks);

        verify(urlProcessor).getNormalizedChildUrls(eq(baseUrl), parsedLinksCaptor.capture());
        Set<String> parsedLinks = parsedLinksCaptor.getValue();
        assertThat(parsedLinks).containsOnly(
                "https://monzo.com/about",
                "https://monzo.com/transparency",
                "https://web.monzo.com"
        );
    }


    @Test(expected = PageReadException.class)
    public void shouldWrapException() {

        //given
        String baseUrl = "creepy url";
        setJsoupToThrowException();

        //when
        jSoupPageReader.getChildLinks(baseUrl);

        //then PageReadException thrown
    }

    private Document getDocument(String filePath, String baseUrl) throws IOException {
        return Jsoup.parse(getClass().getClassLoader().getResourceAsStream(filePath), "UTF-8", baseUrl);
    }

    private void setJsoupToThrowException() {
        new MockUp<Jsoup>() {
            @Mock
            public Connection connect(String url) {
                throw new RuntimeException();
            }
        };
    }

    private void mockJsoup(String documentUrl, Document documentToReturn) throws IOException {
        Connection connection = mock(Connection.class);
        when(connection.get()).thenReturn(documentToReturn);
        new MockUp<Jsoup>() {
            @Mock
            public Connection connect(String url) {
                if (url.equals(documentUrl)) {
                    return connection;
                } else {
                    return null;
                }
            }
        };
    }
}