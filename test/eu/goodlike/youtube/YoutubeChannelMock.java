package eu.goodlike.youtube;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import okhttp3.HttpUrl;

public final class YoutubeChannelMock {

    public static final String GOODLIKE_CHANNEL_ID = "UC0dEueRlBJhHKfvWvJfjwxw";
    public static final String GOODLIKE_CHANNEL_TITLE = "TheGoodlike13";
    public static final HttpUrl GOODLIKE_CHANNEL_URL = HttpUrl.parse("https://www.youtube.com/channel/" + GOODLIKE_CHANNEL_ID);

    public static YoutubeChannel toYoutubeChannel(String channelId, String channelTitle) {
        SearchResultSnippet snippet = new SearchResultSnippet();
        snippet.setChannelId(channelId);
        snippet.setChannelTitle(channelTitle);

        SearchResult searchResult = new SearchResult();
        searchResult.setSnippet(snippet);

        return new YoutubeChannel(searchResult);
    }

    public static YoutubeChannel ofGoodlike() {
        return toYoutubeChannel(GOODLIKE_CHANNEL_ID, GOODLIKE_CHANNEL_TITLE);
    }

    private YoutubeChannelMock() {
        throw new AssertionError("Do not instantiate! Use static fields/methods!");
    }

}
