package eu.goodlike.sub.box.youtube;

import com.google.api.services.youtube.YouTube;
import eu.goodlike.sub.box.list.Playlist;
import eu.goodlike.test.mocks.http.MockHttpTransport;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

import static eu.goodlike.test.asserts.Asserts.assertNotBlank;
import static eu.goodlike.test.asserts.Asserts.assertNotNull;
import static eu.goodlike.test.mocks.youtube.VideoMocks.getOsuPlaylistVideo;
import static eu.goodlike.test.mocks.youtube.VideoMocks.toPlaylistVideo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class YoutubePlaylistTest {

  private final MockHttpTransport mockHttpTransport = new MockHttpTransport(YoutubePlaylistTest.class);
  private final YouTube youtube = mockHttpTransport.createMockYoutube();

  @Test
  public void nullInputs() {
    assertNotNull("youtube", (YouTube youtube) -> new YoutubePlaylist(youtube, "any"));
    assertNotBlank("playlistId", blank -> new YoutubePlaylist(youtube, blank));
  }

  @Test
  public void getId() {
    Playlist any = new YoutubePlaylist(youtube, "any");

    assertThat(any.getId()).isEqualTo("any");
  }

  @Test
  public void emptyPlaylist() {
    Playlist empty = new YoutubePlaylist(youtube, "empty");

    assertThat(empty.getCurrentItems()).isEmpty();
  }

  @Test
  public void smallPlaylist() {
    Playlist small = new YoutubePlaylist(youtube, "small");

    assertThat(small.getCurrentItems()).containsExactly(getOsuPlaylistVideo());
  }

  @Test
  public void massivePlaylist() {
    Playlist massive = new YoutubePlaylist(youtube, "massive");

    YoutubeVideoViaPlaylist fakeVideo = toPlaylistVideo("fakeId", "fakeTitle");
    assertThat(massive.getCurrentItems())
        .haveExactly(50, new Condition<>(fakeVideo::equals, "fake video"))
        .containsOnlyOnce(getOsuPlaylistVideo());
  }

  @Test
  public void deletedPlaylist() {
    Playlist deleted = new YoutubePlaylist(youtube, "deleted");

    assertThatExceptionOfType(YoutubeWarningException.class)
        .isThrownBy(deleted::getCurrentItems)
        .withMessageContaining("The playlist identified with the requests playlistId parameter cannot be found.");
  }

  @Test
  public void privatePlaylist() {
    Playlist privatePlaylist = new YoutubePlaylist(youtube, "private");

    assertThatExceptionOfType(YoutubeWarningException.class)
        .isThrownBy(privatePlaylist::getCurrentItems)
        .withMessageContaining("The request is not properly authorized to retrieve the specified playlist.");
  }

}
