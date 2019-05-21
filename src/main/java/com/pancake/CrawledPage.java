package com.pancake;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.concurrent.Future;

@RequiredArgsConstructor
@Getter
public class CrawledPage {

    private final String pageUrl;
    private final Future<Set<String>> futureLinks;
}
