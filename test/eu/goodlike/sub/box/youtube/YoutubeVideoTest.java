package eu.goodlike.sub.box.youtube;

import com.google.api.services.youtube.model.PlaylistItem;
import org.junit.jupiter.api.Test;

import static eu.goodlike.test.asserts.Asserts.assertNotBlank;
import static eu.goodlike.test.asserts.Asserts.assertNotNull;
import static eu.goodlike.test.mocks.youtube.VideoMocks.*;
import static org.assertj.core.api.Assertions.assertThat;

public class YoutubeVideoTest {

  @Test
  public void nullInput() {
    assertNotNull("playlistItem", YoutubeVideo::new);
    assertNotNull("snippet", any -> new YoutubeVideo(new PlaylistItem()));
  }

  @Test
  public void noBlankPlaylistItemResults() {
    assertNotBlank("videoId", input -> toYoutubeVideo(input, "any"));
    assertNotBlank("videoTitle", input -> toYoutubeVideo("any", input));
  }


  @Test
  public void getters() {
    YoutubeVideo channel = getOsuVideo();

    assertThat(channel.getId()).isEqualTo(OSU_VIDEO_ID);
    assertThat(channel.getUrl()).isEqualTo(OSU_VIDEO_URL);
    assertThat(channel.getTitle()).isEqualTo(OSU_VIDEO_TITLE);
  }

}
