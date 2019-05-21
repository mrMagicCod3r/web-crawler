package com.pancake;

import lombok.Getter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Getter
public class Crawler {

    private final PageReader pageReader;

    private Set<String> visited = new HashSet<>();

    private Map<String, Set<String>> crawled = new ConcurrentHashMap<>();

    private List<Exception> errors = new ArrayList<>();

    private Deque<CrawledPage> runningTasks = new ArrayDeque<>();

    private ExecutorService executor;

    public Crawler(PageReader pageReader, ExecutorService executor) {
        this.pageReader = pageReader;
        this.executor = executor;
    }

    public void crawl(String url) {
        Callable<Set<String>> task = () -> pageReader.getLinks(url);
        Future<Set<String>> submit = executor.submit(task);
        runningTasks.push(new CrawledPage(url, submit));

        while (!runningTasks.isEmpty()) {
            CrawledPage crawledPage = runningTasks.pop();
            if (crawledPage.getFutureLinks().isDone()) {
                try {
                    handleLinks(crawledPage.getPageUrl(), crawledPage.getFutureLinks().get());
                } catch (Exception ex) {
                    errors.add(ex);
                }
            } else {
                runningTasks.push(crawledPage);
            }
        }
        executor.shutdown();
    }

    private void handleLinks(String parent, Set<String> children) {
        for (String link : children) {
            if (!visited.contains(link)) {
                Set<String> cchildren = crawled.getOrDefault(parent, new HashSet<>());
                cchildren.add(link);
                crawled.put(parent, cchildren);

                Callable<Set<String>> task = () -> pageReader.getLinks(link);
                Future<Set<String>> submit = executor.submit(task);
                runningTasks.push(new CrawledPage(link, submit));
            }
            visited.add(link);
        }
        visited.add(parent);
    }
}