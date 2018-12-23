package eu.goodlike.sub.box.youtube;

import eu.goodlike.sub.box.search.Result;
import eu.goodlike.sub.box.search.Search;
import eu.goodlike.test.mocks.http.MockHttpTransport;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static eu.goodlike.sub.box.youtube.YoutubeChannelMock.ofGoodlike;
import static eu.goodlike.test.asserts.Asserts.*;
import static org.assertj.core.api.Assertions.assertThat;

public class YoutubeChannelSearchTest {

  private final MockHttpTransport mockHttpTransport = new MockHttpTransport(YoutubeChannelSearchTest.class);
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
    List<Result> searchResults = search.doSearch("definitely produces no results", 1);

    assertThat(searchResults).isNotNull().isEmpty();
  }

  @Test
  public void performYoutubeSearch() throws IOException {
    List<Result> searchResults = search.doSearch("let's code", 1);

    assertThat(searchResults).containsExactly(ofGoodlike());
  }

}
