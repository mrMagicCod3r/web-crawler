package com.pancake;

import com.pancake.crawler.CrawledPagePointer;
import com.pancake.crawler.CrawledPagePointerProcessor;
import com.pancake.crawler.Crawler;
import com.pancake.pagereader.PageReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CrawlerTest {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    @Mock
    private PageReader pageReader;
    @Mock
    private CrawledPagePointerProcessor crawledPagePointerProcessor;
    private Crawler crawler;

    @Before
    public void setup() {
        crawler = new Crawler(pageReader, executorService, crawledPagePointerProcessor);
    }

    @Test
    public void shouldCrawlWithoutChildren() throws ExecutionException, InterruptedException {
        //given
        String url = "alone url";
        Set<String> childUrls = new HashSet<>();

        when(pageReader.getChildLinks(url)).then((Answer<Set<String>>) invocation -> {
            Thread.sleep(100);
            return childUrls;
        });

        //when
        crawler.crawl(url);

        //then
        ArgumentCaptor<CrawledPagePointer> crawledPagePointerArgumentCaptor = ArgumentCaptor.forClass(CrawledPagePointer.class);
        verify(crawledPagePointerProcessor).handleCrawledPage(crawledPagePointerArgumentCaptor.capture(), any());
        CrawledPagePointer pagePointer = crawledPagePointerArgumentCaptor.getValue();
        assertThat(pagePointer.getPageUrl()).isEqualTo(url);
        assertThat(pagePointer.getChildrenUrls()).isEqualTo(childUrls);
    }

    @Test
    public void shouldCrawlWithChild(){
        //given
        String url = "parent url";
        String childUrl = "child url";
        Set<String> rootChildUrls = new HashSet<>();
        Set<String> childChildUrls = new HashSet<>();

        when(pageReader.getChildLinks(url)).then((Answer<Set<String>>) invocation -> {
            Thread.sleep(100);
            return rootChildUrls;
        });

        when(pageReader.getChildLinks(childUrl)).then((Answer<Set<String>>) invocation -> {
            Thread.sleep(100);
            return childChildUrls;
        });

        Mockito.doAnswer(invocation -> {
            CrawledPagePointer pagePointer = invocation.getArgumentAt(0, CrawledPagePointer.class);
            Consumer<String> consumer = invocation.getArgumentAt(1, Consumer.class);
            if (pagePointer.getPageUrl().equals(url)) {
                consumer.accept(childUrl);
            }
            return null;
        }).when(crawledPagePointerProcessor).handleCrawledPage(any(), any());

        //when
        crawler.crawl(url);

        //then
        ArgumentCaptor<CrawledPagePointer> crawledPagePointerArgumentCaptor = ArgumentCaptor.forClass(CrawledPagePointer.class);
        verify(crawledPagePointerProcessor, times(2)).handleCrawledPage(crawledPagePointerArgumentCaptor.capture(), any());
        List<CrawledPagePointer> pagePointers = crawledPagePointerArgumentCaptor.getAllValues();

        assertThat(pagePointers)
                .extracting(CrawledPagePointer::getPageUrl, this::childUrlExtractor)
                .containsOnly(
                        tuple(url, rootChildUrls),
                        tuple(childUrl, childChildUrls)
                );
    }

    private Set<String> childUrlExtractor(CrawledPagePointer crawledPagePointer) {
        try {
            return crawledPagePointer.getChildrenUrls();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}