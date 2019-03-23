package eu.goodlike.sub.box.youtube;

import com.google.api.services.youtube.model.Video;
import com.google.common.base.MoreObjects;
import eu.goodlike.sub.box.util.require.Require;
import eu.goodlike.sub.box.video.VideoItem;
import okhttp3.HttpUrl;

import java.util.Objects;

import static eu.goodlike.sub.box.util.require.Require.titled;

public final class YoutubeVideoViaLookup implements VideoItem {

  @Override
  public String getId() {
    return getVideoId();
  }

  @Override
  public String getTitle() {
    return getVideoTitle();
  }

  @Override
  public HttpUrl getUrl() {
    return HttpUrl.parse(VIDEO_URL_PREFIX + getVideoId());
  }

  public YoutubeVideoViaLookup(Video video) {
    this.video = Require.notNull(video, titled("video"));
    assertVideoPopulated();
  }

  private final Video video;

  private void assertVideoPopulated() {
    Require.notNull(video.getSnippet(), titled("snippet"));
    Require.notBlank(getVideoId(), titled("videoId"));
    Require.notBlank(getVideoTitle(), titled("videoTitle"));
  }

  private String getVideoTitle() {
    return video.getSnippet().getTitle();
  }

  private String getVideoId() {
    return video.getId();
  }

  private static final String VIDEO_URL_PREFIX = "https://www.youtube.com/watch?v=";

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", getId())
        .add("title", getTitle())
        .add("url", getUrl())
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    YoutubeVideoViaLookup that = (YoutubeVideoViaLookup)o;
    return Objects.equals(getId(), that.getId()) &&
        Objects.equals(getTitle(), that.getTitle()) &&
        Objects.equals(getUrl(), that.getUrl());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getTitle(), getUrl());
  }

}
