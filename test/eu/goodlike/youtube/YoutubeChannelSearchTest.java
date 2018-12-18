package eu.goodlike.youtube;

import eu.goodlike.search.Result;
import eu.goodlike.search.Search;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static eu.goodlike.youtube.YoutubeChannelMock.ofGoodlike;
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
    public void noSearchResults() {
        List<Result> searchResults = search.doSearch("definitely produces no results", 1);

        assertThat(searchResults).isNotNull().isEmpty();
    }

    @Test
    @Disabled
    public void performYoutubeSearch() {
        List<Result> searchResults = search.doSearch("let's code", 1);

        assertThat(searchResults).containsExactly(ofGoodlike());
    }

    private void assertInvalidSearch(String errorType, String searchQuery, int maxResults) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> search.doSearch(searchQuery, maxResults))
                .withMessageContaining(errorType);
    }

}
