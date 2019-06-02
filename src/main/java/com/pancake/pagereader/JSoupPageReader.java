package com.pancake.pagereader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class JSoupPageReader implements PageReader {

    private final UrlProcessor urlProcessor;

    @Override
    public Set<String> getChildLinks(String rootUrl) throws PageReadException {

        try {
            log.debug("Visiting: " + rootUrl);
            // I use JSoup here because it gives me some nice features like handling compression or url resolution.
            // Normally I'd separate connection and parsing logic but KISS in this case.
            Document document = Jsoup.connect(rootUrl).get();

            Set<String> linksOnPage = document
                    .select("a[href]")
                    .stream()
                    .map(rawLink -> rawLink.attr("abs:href"))
                    .collect(Collectors.toSet());

            return urlProcessor.getNormalizedChildUrls(rootUrl, linksOnPage);
        } catch (Exception e) {
            throw new PageReadException(e);
        }
    }
}
