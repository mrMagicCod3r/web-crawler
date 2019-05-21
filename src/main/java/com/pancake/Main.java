package com.pancake;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws Exception {
        String url = "https://monzo.com";


        LinkValidator linkValidator = new LinkValidator();
        PageReader pageReader = new JSoupPageReader(linkValidator);

        ExecutorService executor = Executors.newFixedThreadPool(50);
        Crawler crawler = new Crawler(pageReader, executor);
        crawler.crawl(url);
        crawler.getCrawled();

        for (Map.Entry<String, Set<String>> pages : crawler.getCrawled().entrySet()) {

            System.out.println(pages.getKey() + pages.getValue().toString());

        }

        System.out.println("Errors: " + crawler.getErrors().size());
    }
}
