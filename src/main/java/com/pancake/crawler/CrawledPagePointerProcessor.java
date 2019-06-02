package com.pancake.crawler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Slf4j
public class CrawledPagePointerProcessor {

    private final CrawlerRepository crawlerRepository;

    public void handleCrawledPage(CrawledPagePointer crawledPagePointer, Consumer<String> unseenUrlCallback) {
        try {
            String pageUrl = crawledPagePointer.getPageUrl();
            List<String> unseenUrls = storeAndReturnUnseen(pageUrl, crawledPagePointer.getChildrenUrls());
            unseenUrls.forEach(unseenUrlCallback::accept);
        } catch (Exception ex) {
            crawlerRepository.recordException(ex);
        }
    }

    private List<String> storeAndReturnUnseen(String url, Set<String> childUrls) {
        crawlerRepository.storeCrawledPage(url, childUrls);

        List<String> newLinks = new ArrayList<>();
        for (String link : childUrls) {
            if (!crawlerRepository.contains(link)) {
                log.debug("Discovered new link: " + link);
                crawlerRepository.markAsSeen(link);
                newLinks.add(link);
            }
        }
        return newLinks;
    }
}
