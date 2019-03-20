package eu.goodlike.sub.box.youtube;

import com.google.api.services.youtube.model.SearchResult;
import org.junit.jupiter.api.Test;

import static eu.goodlike.test.asserts.Asserts.assertNotBlank;
import static eu.goodlike.test.asserts.Asserts.assertNotNull;
import static eu.goodlike.test.mocks.youtube.ChannelMocks.*;
import static org.assertj.core.api.Assertions.assertThat;

public class YoutubeChannelSearchResultTest {

  @Test
  public void nullInput() {
    assertNotNull("searchResult", YoutubeChannelSearchResult::new);
    assertNotNull("snippet", any -> new YoutubeChannelSearchResult(new SearchResult()));
  }

  @Test
  public void noBlankSearchResultContents() {
    assertNotBlank("channelId", input -> toChannelSearchResult(input, "any"));
    assertNotBlank("channelTitle", input -> toChannelSearchResult("any", input));
  }

  @Test
  public void getters() {
    YoutubeChannelSearchResult channel = getGoodlikeChannelResult();

    assertThat(channel.getId()).isEqualTo(GOODLIKE_CHANNEL_ID);
    assertThat(channel.getUrl()).isEqualTo(GOODLIKE_CHANNEL_URL);
    assertThat(channel.getTitle()).isEqualTo(GOODLIKE_CHANNEL_TITLE);
  }

}
