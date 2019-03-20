package eu.goodlike.test.mocks.youtube;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import eu.goodlike.sub.box.youtube.YoutubeChannelSearchResult;
import okhttp3.HttpUrl;

public final class ChannelMocks {

  public static final String GOODLIKE_CHANNEL_ID = "UC0dEueRlBJhHKfvWvJfjwxw";
  public static final String GOODLIKE_CHANNEL_TITLE = "TheGoodlike13";
  public static final HttpUrl GOODLIKE_CHANNEL_URL = HttpUrl.parse("https://www.youtube.com/channel/" + GOODLIKE_CHANNEL_ID);
  public static final String GOODLIKE_UPLOAD_PLAYLIST_ID = "UUlzfx-qU_5Xufh5BTmKnRdQ";

  public static YoutubeChannelSearchResult toChannelSearchResult(String channelId, String channelTitle) {
    SearchResultSnippet snippet = new SearchResultSnippet();
    snippet.setChannelId(channelId);
    snippet.setChannelTitle(channelTitle);

    SearchResult searchResult = new SearchResult();
    searchResult.setSnippet(snippet);

    return new YoutubeChannelSearchResult(searchResult);
  }

  public static YoutubeChannelSearchResult getGoodlikeChannelResult() {
    return toChannelSearchResult(GOODLIKE_CHANNEL_ID, GOODLIKE_CHANNEL_TITLE);
  }

  private ChannelMocks() {
    throw new AssertionError("Do not instantiate! Use static fields/methods!");
  }

}
