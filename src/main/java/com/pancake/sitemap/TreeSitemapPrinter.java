package com.pancake.sitemap;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TreeSitemapPrinter implements SiteMapPrinter {

    public static final String INDENT = "        ";

    @Override
    public void print(String root, Map<String, Set<String>> webPages) {
        print(root, webPages, "");
    }

    private void print(String root, Map<String, Set<String>> pages, String indent) {
        System.out.println(indent + root);

        Set<String> childUrls = pages.getOrDefault(root, Collections.emptySet());
        for (String link : childUrls) {
            if (!link.equals(root) && link.startsWith(root)) {
                print(link, pages, indent + INDENT);
            }
        }
    }
}
