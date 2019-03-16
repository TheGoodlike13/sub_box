package eu.goodlike.sub.box.youtube;

import com.google.api.services.youtube.model.SearchResult;
import org.junit.jupiter.api.Test;

import static eu.goodlike.test.asserts.Asserts.assertNotBlank;
import static eu.goodlike.test.asserts.Asserts.assertNotNull;
import static eu.goodlike.test.mocks.youtube.ChannelMocks.*;
import static org.assertj.core.api.Assertions.assertThat;

public class YoutubeChannelTest {

  @Test
  public void nullSearchResult() {
    assertNotNull("searchResult", YoutubeChannel::new);
    assertNotNull("snippet", any -> new YoutubeChannel(new SearchResult()));
  }

  @Test
  public void noBlankSearchResultContents() {
    assertNotBlank("channelId", input -> toYoutubeChannel(input, "any"));
    assertNotBlank("channelTitle", input -> toYoutubeChannel("any", input));
  }

  @Test
  public void getters() {
    YoutubeChannel channel = getGoodlikeChannel();

    assertThat(channel.getUrl()).isEqualTo(GOODLIKE_CHANNEL_URL);
    assertThat(channel.getTitle()).isEqualTo(GOODLIKE_CHANNEL_TITLE);
  }

}
