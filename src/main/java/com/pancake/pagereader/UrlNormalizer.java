package com.pancake.pagereader;

public class UrlNormalizer {

    private static final String QUERY_CHAR = "?";
    private static final String ANCHOR_CHAR = "#";

    public String normalize(String url) {
        if (url != null) {
            String strippedUrl = extractBaseUrl(url);
            return removeLastSlash(strippedUrl)
                    .replaceAll(" ", "%20");
        }
        return null;
    }

    private String removeLastSlash(String url) { // that's a debatable decision to remove it
        if (url.endsWith("/")) {
            return url.substring(0, url.lastIndexOf("/"));
        } else {
            return url;
        }
    }

    private static String extractBaseUrl(String url) {

        int cutPosition = url.indexOf(QUERY_CHAR);
        if (cutPosition <= 0) {
            cutPosition = url.indexOf(ANCHOR_CHAR);
        }

        if (cutPosition >= 0) {
            url = url.substring(0, cutPosition);
        }

        return url;
    }
}
