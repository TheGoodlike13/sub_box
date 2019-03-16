package eu.goodlike.sub.box.youtube;

import com.google.api.services.youtube.model.SearchResult;
import com.google.common.base.MoreObjects;
import eu.goodlike.sub.box.search.Result;
import eu.goodlike.sub.box.util.require.Require;
import okhttp3.HttpUrl;

import java.util.Objects;

import static eu.goodlike.sub.box.util.require.Require.titled;

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

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("title", getTitle())
        .add("url", getUrl())
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    YoutubeChannel that = (YoutubeChannel)o;
    return Objects.equals(getTitle(), that.getTitle()) &&
        Objects.equals(getUrl(), that.getUrl());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTitle(), getUrl());
  }

}
