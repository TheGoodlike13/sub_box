package eu.goodlike.sub.box.youtube;

import com.google.api.services.youtube.model.Video;
import eu.goodlike.sub.box.util.require.Require;

import static eu.goodlike.sub.box.util.require.Require.titled;

public final class YoutubeVideoViaLookup extends YoutubeVideo {

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

  @Override
  protected String getVideoId() {
    return video.getId();
  }

  @Override
  protected String getVideoTitle() {
    return video.getSnippet().getTitle();
  }

}
