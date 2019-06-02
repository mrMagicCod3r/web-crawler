package com.pancake;

import com.pancake.crawler.CrawledPagePointerProcessor;
import com.pancake.crawler.Crawler;
import com.pancake.crawler.CrawlerRepository;
import com.pancake.pagereader.JSoupPageReader;
import com.pancake.pagereader.PageReader;
import com.pancake.pagereader.UrlNormalizer;
import com.pancake.pagereader.UrlProcessor;
import com.pancake.sitemap.SiteMapPrinter;
import com.pancake.sitemap.TreeSitemapPrinter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Main {

    public static void main(String[] args) {

        String url;

        UrlNormalizer urlNormalizer = new UrlNormalizer();

        if (args.length > 0) {
            url = args[0];
        } else {
            System.out.println("You have to provide url to crawl.");
            return;
        }
        url = urlNormalizer.normalize(url);
        log.info("Crawling page: {} ", url);

        // here very advanced dependency injection framework
        UrlProcessor urlProcessor = new UrlProcessor(urlNormalizer);
        PageReader pageReader = new JSoupPageReader(urlProcessor);
        CrawlerRepository crawlerRepository = new CrawlerRepository();
        CrawledPagePointerProcessor crawledPagePointerProcessor = new CrawledPagePointerProcessor(crawlerRepository);

        ExecutorService executor = Executors.newCachedThreadPool();
        Crawler crawler = new Crawler(pageReader, executor, crawledPagePointerProcessor);

        Instant start = Instant.now();
        crawler.crawl(url);
        Instant finish = Instant.now();

        Map<String, Set<String>> crawledPages = crawlerRepository.getPages();

        SiteMapPrinter siteMapPrinter = new TreeSitemapPrinter();
        siteMapPrinter.print(url, crawledPages);

        log.info("Took {} to crawl.", Duration.between(start, finish));
        log.info("Pages count: {}", crawledPages.size());

        List<Exception> crawlingExceptions = crawlerRepository.getCrawlingExceptions();
        log.info("Errors count: {}", crawlingExceptions.size());
        if (log.isDebugEnabled()) {
            crawlingExceptions.forEach(exception -> log.error("Crawling exception", exception));
        }
    }
}
