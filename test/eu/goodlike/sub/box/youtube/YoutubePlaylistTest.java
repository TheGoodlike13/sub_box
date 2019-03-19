package eu.goodlike.sub.box.youtube;

import com.google.api.services.youtube.YouTube;
import eu.goodlike.sub.box.list.Playlist;
import eu.goodlike.test.mocks.http.MockHttpTransport;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

import static eu.goodlike.test.asserts.Asserts.assertNotBlank;
import static eu.goodlike.test.asserts.Asserts.assertNotNull;
import static eu.goodlike.test.mocks.youtube.VideoMocks.getOsuVideo;
import static eu.goodlike.test.mocks.youtube.VideoMocks.toYoutubeVideo;
import static org.assertj.core.api.Assertions.assertThat;

public class YoutubePlaylistTest {

  private final MockHttpTransport mockHttpTransport = new MockHttpTransport(YoutubePlaylistTest.class);
  private final YouTube youtube = mockHttpTransport.createMockYoutube();

  @Test
  public void nullInputs() {
    assertNotNull("youtube", (YouTube youtube) -> new YoutubePlaylist(youtube, "any"));
    assertNotBlank("playlistId", blank -> new YoutubePlaylist(youtube, blank));
  }

  @Test
  public void emptyPlaylist() {
    Playlist empty = new YoutubePlaylist(youtube, "empty");

    assertThat(empty.getVideos()).isEmpty();
  }

  @Test
  public void smallPlaylist() {
    Playlist small = new YoutubePlaylist(youtube, "small");

    assertThat(small.getVideos()).containsExactly(getOsuVideo());
  }

  @Test
  public void massivePlaylist() {
    Playlist massive = new YoutubePlaylist(youtube, "massive");

    YoutubeVideo fakeVideo = toYoutubeVideo("fakeId", "fakeTitle");
    assertThat(massive.getVideos())
        .haveExactly(50, new Condition<>(fakeVideo::equals, "fake video"))
        .containsOnlyOnce(getOsuVideo());
  }

}
