package eu.goodlike.sub.box.youtube;

import com.google.common.collect.ImmutableList;
import eu.goodlike.sub.box.search.Result;
import eu.goodlike.sub.box.search.Search;
import eu.goodlike.test.mocks.MockHttpTransport;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static eu.goodlike.sub.box.youtube.YoutubeChannelMock.*;
import static eu.goodlike.test.asserts.Asserts.*;
import static org.assertj.core.api.Assertions.assertThat;

public class YoutubeChannelSearchTest {

  private final MockHttpTransport mockHttpTransport = new MockHttpTransport();
  private final Search search = new YoutubeChannelSearch(mockHttpTransport.createMockYoutube());

  @Test
  public void nullInputs() {
    assertInvalidNull("youtube", YoutubeChannelSearch::new);
  }

  @Test
  public void invalidSearches() {
    assertInvalidBlank("searchQuery", query -> search.doSearch(query, 1));
    assertInvalidNegativeOrZero("maxResults", maxResults -> search.doSearch("any", maxResults));
  }

  @Test
  @Disabled
  public void searchError() {
    // TODO
  }

  @Test
  public void noSearchResults() throws IOException {
    mockHttpTransport.setResponse(
        "{\"items\":[]}",
        "get",
        "search",
        ImmutableList.of("q=definitely produces no results", "maxResults=1", "part=snippet")
    );

    List<Result> searchResults = search.doSearch("definitely produces no results", 1);

    assertThat(searchResults).isNotNull().isEmpty();
  }

  @Test
  public void performYoutubeSearch() throws IOException {
    mockHttpTransport.setResponse(
        "{\"items\":[{\"snippet\":{\"channelId\":\"" + GOODLIKE_CHANNEL_ID + "\",\"channelTitle\": \"" + GOODLIKE_CHANNEL_TITLE + "\"}}]}",
        "get",
        "search",
        ImmutableList.of("q=let's code", "maxResults=1", "part=snippet")
    );

    List<Result> searchResults = search.doSearch("let's code", 1);

    assertThat(searchResults).containsExactly(ofGoodlike());
  }

}
