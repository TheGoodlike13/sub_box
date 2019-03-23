package eu.goodlike.sub.box.youtube;

import com.google.api.services.youtube.model.Video;
import eu.goodlike.sub.box.video.VideoItem;
import org.junit.jupiter.api.Test;

import static eu.goodlike.test.asserts.Asserts.assertNotBlank;
import static eu.goodlike.test.asserts.Asserts.assertNotNull;
import static eu.goodlike.test.mocks.youtube.VideoMocks.*;
import static org.assertj.core.api.Assertions.assertThat;

public class YoutubeVideoViaLookupTest {

  @Test
  public void nullInput() {
    assertNotNull("video", YoutubeVideoViaLookup::new);
    assertNotNull("snippet", any -> new YoutubeVideoViaLookup(new Video()));
  }

  @Test
  public void noBlankPlaylistItemResults() {
    assertNotBlank("videoId", input -> toVideoViaLookup(input, "any"));
    assertNotBlank("videoTitle", input -> toVideoViaLookup("any", input));
  }

  @Test
  public void getters() {
    VideoItem video = getOsuVideoLookup();

    assertThat(video.getId()).isEqualTo(OSU_VIDEO_ID);
    assertThat(video.getUrl()).isEqualTo(OSU_VIDEO_URL);
    assertThat(video.getTitle()).isEqualTo(OSU_VIDEO_TITLE);
  }

}
