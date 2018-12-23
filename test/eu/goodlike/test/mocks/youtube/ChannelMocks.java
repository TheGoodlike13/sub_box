package eu.goodlike.test.mocks.youtube;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import eu.goodlike.sub.box.youtube.YoutubeChannel;
import okhttp3.HttpUrl;

public final class ChannelMocks {

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

  public static YoutubeChannel getGoodlikeChannel() {
    return toYoutubeChannel(GOODLIKE_CHANNEL_ID, GOODLIKE_CHANNEL_TITLE);
  }

  private ChannelMocks() {
    throw new AssertionError("Do not instantiate! Use static fields/methods!");
  }

}
