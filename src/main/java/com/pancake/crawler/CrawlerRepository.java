package com.pancake.crawler;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class CrawlerRepository {

    @Getter
    private final Map<String, Set<String>> pages = new HashMap<>();

    @Getter
    private final List<Exception> crawlingExceptions = new ArrayList<>();

    public void markAsSeen(String url) {
        pages.put(url, Collections.emptySet());
    }

    public boolean contains(String url) {
        return pages.containsKey(url);
    }

    public void storeCrawledPage(String url, Set<String> children) {
        pages.put(url, children);
    }

    public void recordException(Exception exception) {
        crawlingExceptions.add(exception);
    }
}
