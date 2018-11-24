package eu.goodlike.youtube;

import eu.goodlike.search.Search;
import eu.goodlike.search.SearchResult;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class YoutubeChannelSearchTest {

    private final Search search = new YoutubeChannelSearch();

    @Test
    public void invalidSearches() {
        assertInvalidSearch("searchQuery", null, 1);
        assertInvalidSearch("searchQuery", " ", 1);
        assertInvalidSearch("maxResults", "any", -1);
        assertInvalidSearch("maxResults", "any", 0);
    }

    @Test
    @Disabled
    public void performYoutubeSearch() {
//        SearchResult result = new YoutubeChannel(channelName, channelId);

        List<SearchResult> searchResults = search.doSearch("let's code", 1);

        assertThat(searchResults).isNotNull()
                .containsExactly();
    }

    private void assertInvalidSearch(String errorType, String searchQuery, int maxResults) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> search.doSearch(searchQuery, maxResults))
                .withMessageContaining(errorType);
    }

}
