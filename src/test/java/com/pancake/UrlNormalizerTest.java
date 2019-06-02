package com.pancake;

import com.pancake.pagereader.UrlNormalizer;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UrlNormalizerTest {

    private UrlNormalizer urlNormalizer = new UrlNormalizer();

    @Test
    public void shouldHandleNull() {

        //given
        String url = null;

        //when
        String normalized = urlNormalizer.normalize(url);

        //then
        assertThat(normalized).isNull();
    }

    @Test
    public void shouldRemoveAnchor() {

        //given
        String url = "https://monzo.com/careers/#jobs";

        //when
        String normalized = urlNormalizer.normalize(url);

        //then
        assertThat(normalized).isEqualTo("https://monzo.com/careers");
    }

    @Test
    public void shouldRemoveQuery() {

        //given
        String url = "https://monzo.com/careers/?style=yolo";

        //when
        String normalized = urlNormalizer.normalize(url);

        //then
        assertThat(normalized).isEqualTo("https://monzo.com/careers");
    }

    @Test
    public void shouldRemoveTrailingSlash() {

        //given
        String url = "https://monzo.com/careers/";

        //when
        String normalized = urlNormalizer.normalize(url);

        //then
        assertThat(normalized).isEqualTo("https://monzo.com/careers");
    }

    @Test
    public void shouldEncodeSpaces() {

        //given
        String url = "https://monzo.com/ careers";

        //when
        String normalized = urlNormalizer.normalize(url);

        //then
        assertThat(normalized).isEqualTo("https://monzo.com/%20careers");
    }
}