package eu.goodlike.youtube;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingConsumer;

import static eu.goodlike.asserts.Asserts.assertInvalidBlank;
import static eu.goodlike.asserts.Asserts.assertInvalidNull;

public class YoutubeChannelTest {

    @Test
    public void nullSearchResult() {
        assertInvalidNull("searchResult", (ThrowingConsumer<SearchResult>)YoutubeChannel::new);
    }

    @Test
    public void noBlankSearchResultContents() {
        assertInvalidBlank("channelId", input -> populateSearchResult(input, "any"));
        assertInvalidBlank("channelTitle", input -> populateSearchResult("any", input));
    }

    private void populateSearchResult(String channelId, String channelTitle) {
        SearchResultSnippet snippet = new SearchResultSnippet();
        snippet.setChannelId(channelId);
        snippet.setChannelTitle(channelTitle);

        SearchResult searchResult = new SearchResult();
        searchResult.setSnippet(snippet);

        new YoutubeChannel(searchResult);
    }

}
