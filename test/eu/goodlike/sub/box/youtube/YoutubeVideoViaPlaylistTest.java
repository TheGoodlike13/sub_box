package eu.goodlike.sub.box.youtube;

import com.google.api.services.youtube.model.PlaylistItem;
import eu.goodlike.sub.box.video.VideoItem;
import org.junit.jupiter.api.Test;

import static eu.goodlike.test.asserts.Asserts.assertNotBlank;
import static eu.goodlike.test.asserts.Asserts.assertNotNull;
import static eu.goodlike.test.mocks.youtube.VideoMocks.*;
import static org.assertj.core.api.Assertions.assertThat;

public class YoutubeVideoViaPlaylistTest {

  @Test
  public void nullInput() {
    assertNotNull("playlistItem", YoutubeVideoViaPlaylist::new);
    assertNotNull("snippet", any -> new YoutubeVideoViaPlaylist(new PlaylistItem()));
  }

  @Test
  public void noBlankPlaylistItemResults() {
    assertNotBlank("videoId", input -> toPlaylistVideo(input, "any"));
    assertNotBlank("videoTitle", input -> toPlaylistVideo("any", input));
  }

  @Test
  public void getters() {
    VideoItem video = getOsuPlaylistVideo();

    assertThat(video.getId()).isEqualTo(OSU_VIDEO_ID);
    assertThat(video.getUrl()).isEqualTo(OSU_VIDEO_URL);
    assertThat(video.getTitle()).isEqualTo(OSU_VIDEO_TITLE);
  }

}
