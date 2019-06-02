package com.pancake.pagereader;

import java.util.Set;

public interface PageReader {

    Set<String> getChildLinks(String url) throws PageReadException;
}
