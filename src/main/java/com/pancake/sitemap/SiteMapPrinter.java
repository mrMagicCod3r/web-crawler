package com.pancake.sitemap;

import java.util.Map;
import java.util.Set;

public interface SiteMapPrinter {

    void print(String root, Map<String, Set<String>> webPages);
}
