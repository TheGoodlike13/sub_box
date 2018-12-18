package eu.goodlike.sub.box.youtube;

import com.google.api.services.youtube.model.SearchResult;
import org.junit.jupiter.api.Test;

import static eu.goodlike.sub.box.youtube.YoutubeChannelMock.*;
import static eu.goodlike.test.asserts.Asserts.assertInvalidBlank;
import static eu.goodlike.test.asserts.Asserts.assertInvalidNull;
import static org.assertj.core.api.Assertions.assertThat;

public class YoutubeChannelTest {

    @Test
    public void nullSearchResult() {
        assertInvalidNull("searchResult", YoutubeChannel::new);
        assertInvalidNull("snippet", any -> new YoutubeChannel(new SearchResult()));
    }

    @Test
    public void noBlankSearchResultContents() {
        assertInvalidBlank("channelId", input -> toYoutubeChannel(input, "any"));
        assertInvalidBlank("channelTitle", input -> toYoutubeChannel("any", input));
    }

    @Test
    public void getters() {
        YoutubeChannel channel = ofGoodlike();

        assertThat(channel.getUrl()).isEqualTo(GOODLIKE_CHANNEL_URL);
        assertThat(channel.getTitle()).isEqualTo(GOODLIKE_CHANNEL_TITLE);
    }

}
