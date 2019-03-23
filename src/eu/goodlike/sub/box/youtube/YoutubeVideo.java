package eu.goodlike.sub.box.youtube;

import com.google.common.base.MoreObjects;
import eu.goodlike.sub.box.video.VideoItem;
import okhttp3.HttpUrl;

import java.util.Objects;

public abstract class YoutubeVideo implements VideoItem {

  protected abstract String getVideoId();

  protected abstract String getVideoTitle();

  @Override
  public final String getId() {
    return getVideoId();
  }

  @Override
  public final String getTitle() {
    return getVideoTitle();
  }

  @Override
  public final HttpUrl getUrl() {
    return HttpUrl.parse(VIDEO_URL_PREFIX + getVideoId());
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
    YoutubeVideo that = (YoutubeVideo)o;
    return Objects.equals(getId(), that.getId()) &&
        Objects.equals(getTitle(), that.getTitle()) &&
        Objects.equals(getUrl(), that.getUrl());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getTitle(), getUrl());
  }

}
