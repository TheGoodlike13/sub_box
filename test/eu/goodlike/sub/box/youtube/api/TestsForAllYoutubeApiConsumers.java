package eu.goodlike.sub.box.youtube.api;

import com.google.api.services.youtube.YouTube;
import eu.goodlike.sub.box.youtube.YoutubeChannelSearch;
import eu.goodlike.sub.box.youtube.YoutubePlaylist;
import eu.goodlike.test.mocks.http.MockHttpTransport;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class TestsForAllYoutubeApiConsumers implements ArgumentsProvider {

  @Override
  public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
    return Stream.of(
        Arguments.of("youtubeChannelSearch", youtubeChannelSearch()),
        Arguments.of("playlistItems", playlistItems())
    );
  }

  @ParameterizedTest(name = "{0}")
  @ArgumentsSource(TestsForAllYoutubeApiConsumers.class)
  public void handleQuotaExceeded(String apiDescription, ThrowingConsumer<YouTube> apiCall) {
    YouTube mockTube = new MockHttpTransport(TestsForAllYoutubeApiConsumers.class, "quotaExceeded.mockhttp").createMockYoutube();
    assertThatExceptionOfType(IllegalStateException.class)  // TODO: improve the mechanism for handling API errors
        .isThrownBy(() -> apiCall.accept(mockTube))
        .withMessageContaining("The request cannot be completed because you have exceeded your quota.");
  }

  private ThrowingConsumer<YouTube> youtubeChannelSearch() {
    return youtube -> new YoutubeChannelSearch(youtube).doSearch("any", 1);
  }

  private ThrowingConsumer<YouTube> playlistItems() {
    return youtube -> new YoutubePlaylist(youtube, "any").getVideos().toArray();
  }

}
