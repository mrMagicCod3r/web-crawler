package com.pancake;

import com.pancake.pagereader.UrlNormalizer;
import com.pancake.pagereader.UrlProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UrlProcessorTest {

    @Mock
    private UrlNormalizer urlNormalizer;

    @InjectMocks
    private UrlProcessor urlProcessor;

    @Test
    public void shouldOnlyGetNormalizedChildUrls() {

        //given
        String rootUrl = "https://trololo.com";
        String childUrl1 = "trash";
        String childUrl2 = "+95979840940";
        String childUrl3 = "https://trololo.com";
        String childUrl4 = "https://trololo.com/potato";

        Set<String> links = new HashSet<>();
        links.add(childUrl1);
        links.add(childUrl2);
        links.add(childUrl3);
        links.add(childUrl4);

        when(urlNormalizer.normalize(any())).then(answer -> answer.getArgumentAt(0, String.class));

        //when
        Set<String> normalizedChildUrls = urlProcessor.getNormalizedChildUrls(rootUrl, links);

        //then
        assertThat(normalizedChildUrls).hasSize(1);
        assertThat(normalizedChildUrls).containsOnly(childUrl4);
    }
}