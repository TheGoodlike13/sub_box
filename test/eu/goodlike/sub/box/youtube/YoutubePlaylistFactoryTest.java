package eu.goodlike.sub.box.youtube;

import com.google.api.services.youtube.YouTube;
import eu.goodlike.sub.box.list.PlaylistFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static eu.goodlike.test.asserts.Asserts.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;

public class YoutubePlaylistFactoryTest {

  private final YouTube youtube = Mockito.mock(YouTube.class);
  private final PlaylistFactory playlistFactory = new YoutubePlaylistFactory(youtube);

  @Test
  public void nullInputs() {
    assertNotNull("youtube", YoutubePlaylistFactory::new);
  }

  @Test
  public void newPlaylist() {
    assertThat(playlistFactory.newPlaylist("any")).isEqualTo(new YoutubePlaylist(youtube, "any"));
  }

}
