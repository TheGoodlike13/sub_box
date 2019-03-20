package eu.goodlike.sub.box.youtube;

import com.google.api.services.youtube.YouTube;
import eu.goodlike.test.mocks.http.MockHttpTransport;
import org.junit.jupiter.api.Test;

import static eu.goodlike.test.asserts.Asserts.assertNotNull;
import static eu.goodlike.test.mocks.youtube.ChannelMocks.*;
import static eu.goodlike.test.mocks.youtube.VideoMocks.getOsuVideo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class YoutubeChannelViaSearchTest {

  private final YouTube youtube = new MockHttpTransport(YoutubeChannelViaSearchTest.class).createMockYoutube();

  @Test
  public void nullInputs() {
    assertNotNull("youtube", (YouTube youtube) -> new YoutubeChannelViaSearch(youtube, getGoodlikeChannelResult()));
    assertNotNull("result", (YoutubeChannelSearchResult result) -> new YoutubeChannelViaSearch(youtube, result));
  }

  @Test
  public void getters() {
    YoutubeChannelViaSearch channel = new YoutubeChannelViaSearch(youtube, getGoodlikeChannelResult());

    assertThat(channel.getId()).isEqualTo(GOODLIKE_CHANNEL_ID);
    assertThat(channel.getTitle()).isEqualTo(GOODLIKE_CHANNEL_TITLE);
    assertThat(channel.getUrl()).isEqualTo(GOODLIKE_CHANNEL_URL);
  }

  @Test
  public void uploadedVideosForNormalChannel() {
    YoutubeChannelSearchResult normalChannel = getGoodlikeChannelResult();
    YoutubeChannelViaSearch channel = new YoutubeChannelViaSearch(youtube, normalChannel);

    assertThat(channel.getUploadedVideos()).containsExactly(getOsuVideo());
  }

  @Test
  public void uploadedVideosForChannelDeletedBeforeLookup() {
    YoutubeChannelSearchResult deletedChannel = toChannelSearchResult("alreadyDeleted", "Channel is already deleted");
    YoutubeChannelViaSearch channel = new YoutubeChannelViaSearch(youtube, deletedChannel);

    assertThatExceptionOfType(YoutubeWarningException.class)
        .isThrownBy(channel::getUploadedVideos)
        .withMessageContaining("The channel is deleted or banned.");
  }

  @Test
  public void uploadedVideosForChannelDeletedAfterLookup() {
    YoutubeChannelSearchResult deletedChannel = toChannelSearchResult("almostDeleted", "About to be deleted");
    YoutubeChannelViaSearch channel = new YoutubeChannelViaSearch(youtube, deletedChannel);

    assertThatExceptionOfType(YoutubeWarningException.class)
        .isThrownBy(channel::getUploadedVideos)
        .withMessageContaining("The playlist identified with the requests playlistId parameter cannot be found.");
  }

}
