package com.pancake.pagereader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class UrlProcessor {

    private final UrlNormalizer urlNormalizer;

    public Set<String> getNormalizedChildUrls(String rootUrl, Set<String> links) {
        Set<String> childLinks = new HashSet<>();

        for (String link : links) {
            if (isValidUrl(link)) {
                link = urlNormalizer.normalize(link);
                if (isChild(link, rootUrl)) {
                    childLinks.add(link);
                }
            }
        }
        return childLinks;
    }

    private boolean isChild(String childUrl, String rootUrl) {
        return !childUrl.equals(rootUrl) && childUrl.startsWith(rootUrl);
    }

    private boolean isValidUrl(String url) {
        return url.startsWith("http");
    }
}
