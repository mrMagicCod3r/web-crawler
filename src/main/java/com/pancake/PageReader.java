package com.pancake;

import java.util.Set;

public interface PageReader {

    Set<String> getLinks(String url) throws PageReadException;
}
