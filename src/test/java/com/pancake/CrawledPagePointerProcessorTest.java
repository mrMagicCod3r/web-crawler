package com.pancake;

import com.pancake.crawler.CrawledPagePointer;
import com.pancake.crawler.CrawledPagePointerProcessor;
import com.pancake.crawler.CrawlerRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CrawledPagePointerProcessorTest {

    @Mock
    private CrawlerRepository crawlerRepository;

    @InjectMocks
    private CrawledPagePointerProcessor processor;

    @Test
    public void shouldStoreException() throws ExecutionException, InterruptedException {
        //given
        String rootUrl = "happy rootUrl";
        Future<Set<String>> future = mock(Future.class);
        Consumer<String> callback = mock(Consumer.class);
        CrawledPagePointer crawledPagePointer = new CrawledPagePointer(rootUrl, future);

        RuntimeException exception = new RuntimeException();
        when(future.get()).thenThrow(exception);

        //when
        processor.handleCrawledPage(crawledPagePointer, callback);

        //then
        verify(crawlerRepository).recordException(exception);
        verifyNoMoreInteractions(crawlerRepository);
    }

    @Test
    public void shouldStorePageAndCallCallback() throws ExecutionException, InterruptedException {
        //given
        String rootUrl = "happy rootUrl";
        String seenUrl = "seen url";
        String unseenUrl = "unseen url";
        Future<Set<String>> future = mock(Future.class);
        Consumer<String> callback = mock(Consumer.class);

        Set<String> childrenUrls = new HashSet<>();
        childrenUrls.add(seenUrl);
        childrenUrls.add(unseenUrl);
        CrawledPagePointer crawledPagePointer = new CrawledPagePointer(rootUrl, future);

        when(future.isDone()).thenReturn(true);
        when(future.get()).thenReturn(childrenUrls);

        when(crawlerRepository.contains(seenUrl)).thenReturn(true);
        when(crawlerRepository.contains(unseenUrl)).thenReturn(false);

        //when
        processor.handleCrawledPage(crawledPagePointer, callback);

        //then
        verify(crawlerRepository).storeCrawledPage(rootUrl, childrenUrls);
        verify(crawlerRepository).markAsSeen(unseenUrl);
        verify(crawlerRepository, never()).markAsSeen(seenUrl);

        verify(callback).accept(unseenUrl);
        verifyNoMoreInteractions(callback);
    }
}