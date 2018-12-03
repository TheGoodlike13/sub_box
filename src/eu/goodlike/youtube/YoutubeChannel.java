package eu.goodlike.youtube;

import com.google.api.services.youtube.model.SearchResult;
import eu.goodlike.search.Result;
import eu.goodlike.util.Require;
import okhttp3.HttpUrl;

import static eu.goodlike.util.Require.titled;

public final class YoutubeChannel implements Result {

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public HttpUrl getUrl() {
        return null;
    }

    public YoutubeChannel(SearchResult searchResult) {
        this.searchResult = Require.notNull(searchResult, titled("searchResult"));
        assertResponsePopulated();
    }

    private final SearchResult searchResult;

    private void assertResponsePopulated() {
        Require.notBlank(searchResult.getSnippet().getChannelId(), titled("channelId"));
        Require.notBlank(searchResult.getSnippet().getChannelTitle(), titled("channelTitle"));
    }

}
