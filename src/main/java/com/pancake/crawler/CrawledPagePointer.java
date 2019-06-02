package com.pancake.crawler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RequiredArgsConstructor
public class CrawledPagePointer {

    @Getter
    private final String pageUrl;
    private final Future<Set<String>> futureChildrenUrls;

    public boolean isCrawled() {
        return futureChildrenUrls.isDone();
    }

    // in more advanced program might be worth encapsulating it better
    public Set<String> getChildrenUrls() throws ExecutionException, InterruptedException {
        return futureChildrenUrls.get();
    }
}
