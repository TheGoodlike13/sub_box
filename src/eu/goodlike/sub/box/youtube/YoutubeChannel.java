package eu.goodlike.sub.box.youtube;

import com.google.api.services.youtube.model.SearchResult;
import eu.goodlike.sub.box.search.Result;
import eu.goodlike.sub.box.util.Require;
import okhttp3.HttpUrl;

import static eu.goodlike.sub.box.util.Require.titled;

public final class YoutubeChannel implements Result {

    @Override
    public String getTitle() {
        return getChannelTitle();
    }

    @Override
    public HttpUrl getUrl() {
        return HttpUrl.parse(CHANNEL_URL_PREFIX + getChannelId());
    }

    public YoutubeChannel(SearchResult searchResult) {
        this.searchResult = Require.notNull(searchResult, titled("searchResult"));
        assertResponsePopulated();
    }

    private final SearchResult searchResult;

    private void assertResponsePopulated() {
        Require.notNull(searchResult.getSnippet(), titled("snippet"));
        Require.notBlank(getChannelId(), titled("channelId"));
        Require.notBlank(getChannelTitle(), titled("channelTitle"));
    }

    private String getChannelId() {
        return searchResult.getSnippet().getChannelId();
    }

    private String getChannelTitle() {
        return searchResult.getSnippet().getChannelTitle();
    }

    private static final String CHANNEL_URL_PREFIX = "https://www.youtube.com/channel/";

}
