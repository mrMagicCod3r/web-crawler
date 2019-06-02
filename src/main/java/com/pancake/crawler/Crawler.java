package com.pancake.crawler;

import com.pancake.pagereader.PageReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
public class Crawler {

    private final PageReader pageReader;
    private final ExecutorService executorService;
    private final CrawledPagePointerProcessor crawledPagePointerProcessor;
    private final Deque<CrawledPagePointer> runningTasks = new ArrayDeque<>();

    public void crawl(String url) {
        submitTask(url);
        while (!runningTasks.isEmpty()) {
            CrawledPagePointer crawledPagePointer = runningTasks.pop();
            if (crawledPagePointer.isCrawled()) {
                crawledPagePointerProcessor.handleCrawledPage(crawledPagePointer, this::submitTask);
            } else {
                runningTasks.push(crawledPagePointer);
            }
        }
        executorService.shutdown();
    }

    private void submitTask(String url) {
        Callable<Set<String>> task = () -> pageReader.getChildLinks(url);
        Future<Set<String>> future = executorService.submit(task);
        runningTasks.push(new CrawledPagePointer(url, future));
    }
}