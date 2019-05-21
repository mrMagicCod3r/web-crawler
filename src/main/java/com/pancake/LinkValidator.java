package com.pancake;

public class LinkValidator {

    public boolean isValid(String url) { //TODO fixme anchors
        return url.startsWith("https://monzo.com")
                && !url.startsWith("https://monzo.com/cdn-cgi/l/email-protection")
                && !url.endsWith(".js")
                && !url.endsWith(".css")
                && !url.endsWith(".rss")
                && !url.endsWith(".atom");
    }
}
