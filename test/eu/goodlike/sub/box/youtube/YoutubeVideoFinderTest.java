package eu.goodlike.sub.box.youtube;

import com.google.api.services.youtube.YouTube;
import eu.goodlike.sub.box.video.VideoFinder;
import eu.goodlike.test.mocks.http.MockHttpTransport;
import org.junit.jupiter.api.Test;

import static eu.goodlike.test.asserts.Asserts.assertNotBlank;
import static eu.goodlike.test.asserts.Asserts.assertNotNull;
import static eu.goodlike.test.mocks.youtube.VideoMocks.getOsuVideoLookup;
import static org.assertj.core.api.Assertions.assertThat;

public class YoutubeVideoFinderTest {

  private final YouTube youtube = new MockHttpTransport(YoutubeVideoFinderTest.class).createMockYoutube();
  private final VideoFinder finder = new YoutubeVideoFinder(youtube);

  @Test
  public void nullInputs() {
    assertNotNull("youtube", YoutubeVideoFinder::new);
    assertNotBlank("videoId", finder::find);
  }

  @Test
  public void findVideos() {
    assertThat(finder.find("mNwgepMSn5E")).contains(getOsuVideoLookup());
    assertThat(finder.find("bad_id")).isEmpty();
    assertThat(finder.find("deleted")).isEmpty();
    assertThat(finder.find("private")).isEmpty();
  }

}
