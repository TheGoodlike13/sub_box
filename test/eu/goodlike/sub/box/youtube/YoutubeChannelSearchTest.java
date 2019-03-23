package eu.goodlike.sub.box.youtube;

import com.google.api.services.youtube.YouTube;
import eu.goodlike.sub.box.channel.Channel;
import eu.goodlike.sub.box.channel.ChannelSearch;
import eu.goodlike.test.mocks.http.MockHttpTransport;
import org.junit.jupiter.api.Test;

import java.util.List;

import static eu.goodlike.test.asserts.Asserts.*;
import static eu.goodlike.test.mocks.youtube.ChannelMocks.getGoodlikeChannelResult;
import static org.assertj.core.api.Assertions.assertThat;

public class YoutubeChannelSearchTest {

  private final MockHttpTransport mockHttpTransport = new MockHttpTransport(YoutubeChannelSearchTest.class);
  private final YouTube youtube = mockHttpTransport.createMockYoutube();
  private final ChannelSearch search = new YoutubeChannelSearch(youtube);

  @Test
  public void nullInputs() {
    assertNotNull("youtube", YoutubeChannelSearch::new);
  }

  @Test
  public void invalidSearches() {
    assertNotBlank("searchQuery", query -> search.doSearch(query, 1));
    assertPositive("maxResults", maxResults -> search.doSearch("any", maxResults));
  }

  @Test
  public void noSearchResults() {
    List<Channel> searchResults = search.doSearch("definitely produces no results", 1);

    assertThat(searchResults).isNotNull().isEmpty();
  }

  @Test
  public void performYoutubeSearch() {
    List<Channel> searchResults = search.doSearch("let's code", 1);

    assertThat(searchResults).containsExactly(new YoutubeChannelViaSearch(youtube, getGoodlikeChannelResult()));
  }

}
