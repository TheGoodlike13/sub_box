package eu.goodlike.sub.box.youtube;

import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.Video;
import eu.goodlike.test.mocks.youtube.VideoMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static eu.goodlike.test.asserts.Asserts.assertNotBlank;
import static eu.goodlike.test.asserts.Asserts.assertNotNull;
import static eu.goodlike.test.mocks.youtube.VideoMocks.*;
import static org.assertj.core.api.Assertions.assertThat;

public class YoutubeVideoTest {

  private static Stream<Arguments> supplyWithNullSnippet() {
    return Stream.of(
        Arguments.of("video via playlist", (Supplier<YoutubeVideo>)() -> new YoutubeVideoViaPlaylist(new PlaylistItem())),
        Arguments.of("video via lookup", (Supplier<YoutubeVideo>)() -> new YoutubeVideoViaLookup(new Video()))
    );
  }

  private static Stream<Arguments> createFromIdAndTitle() {
    return Stream.of(
        Arguments.of("video via playlist", (BiFunction<String, String, YoutubeVideo>)VideoMocks::toPlaylistVideo),
        Arguments.of("video via lookup", (BiFunction<String, String, YoutubeVideo>)VideoMocks::toVideoViaLookup)
    );
  }

  private static Stream<Arguments> osuVideo() {
    return Stream.of(
        Arguments.of("video via playlist", getOsuPlaylistVideo()),
        Arguments.of("video via lookup", getOsuVideoLookup())
    );
  }

  @Test
  public void nullInput() {
    assertNotNull("video", YoutubeVideoViaLookup::new);
    assertNotNull("playlistItem", YoutubeVideoViaPlaylist::new);
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("supplyWithNullSnippet")
  public void testNullSnippets(String description, Supplier<YoutubeVideo> nullSnippetYoutubeVideo) {
    assertNotNull("snippet", any -> nullSnippetYoutubeVideo.get());
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("createFromIdAndTitle")
  public void noBlankPlaylistItemResults(String description, BiFunction<String, String, YoutubeVideo> fromIdAndTitle) {
    assertNotBlank("videoId", input -> fromIdAndTitle.apply(input, "any"));
    assertNotBlank("videoTitle", input -> fromIdAndTitle.apply("any", input));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("osuVideo")
  public void getters(String description, YoutubeVideo osuVideo) {
    assertThat(osuVideo.getId()).isEqualTo(OSU_VIDEO_ID);
    assertThat(osuVideo.getTitle()).isEqualTo(OSU_VIDEO_TITLE);
    assertThat(osuVideo.getUrl()).isEqualTo(OSU_VIDEO_URL);
  }

}
