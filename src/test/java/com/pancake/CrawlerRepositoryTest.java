package com.pancake;

import com.pancake.crawler.CrawlerRepository;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CrawlerRepositoryTest {

    private CrawlerRepository repository = new CrawlerRepository();

    @Test
    public void shouldMarkAsSeen() {
        //given
        String url = "crazy url";

        //when
        repository.markAsSeen(url);

        //then
        assertThat(repository.getPages()).containsKeys(url);
    }

    @Test
    public void shouldNotContain() {
        //given
        String url = "lazy url";

        //then
        assertThat(repository.contains(url)).isFalse();
    }

    @Test
    public void shouldContain() {
        //given
        String url = "pinky url";

        //when
        repository.getPages().put(url, null);

        //then
        assertThat(repository.contains(url)).isTrue();
    }

    @Test
    public void shouldStoreCrawledPage() {
        //given
        String url = "last url";
        Set<String> childUrls = new HashSet<>();

        //when
        repository.storeCrawledPage(url, childUrls);

        //then
        assertThat(repository.getPages()).hasSize(1);
        assertThat(repository.getPages()).containsEntry(url, childUrls);
    }

    @Test
    public void shouldRecordException() {
        //given
        Exception exception = new Exception();

        //when
        repository.recordException(exception);

        //then
        assertThat(repository.getCrawlingExceptions()).hasSize(1);
        assertThat(repository.getCrawlingExceptions()).contains(exception);
    }
}