package com.pancake;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class JSoupPageReader implements PageReader {

    private final LinkValidator linkValidator;

    @Override
    public Set<String> getLinks(String url) throws PageReadException {

        try {
            Set<String> links = new HashSet<>();
            System.out.println("Parsing: " + url);
            Document document = Jsoup.connect(url).get();
            Elements linksOnPage = document.select("a[href]");
            for (Element page : linksOnPage) {
                String parsedLink = page.attr("abs:href"); // normalize?

                if (linkValidator.isValid(parsedLink)) {
                    URL url1 = new URL(parsedLink);
                    links.add(url1.getProtocol() + "://" + url1.getHost() + url1.getPath()); //FIXME omitted port
                }
            }
            return links;
        } catch (IOException e) {
            throw new PageReadException(e);
        }
    }
}
