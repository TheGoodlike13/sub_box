package eu.goodlike.sub.box.youtube;

import com.google.api.services.youtube.model.PlaylistItem;
import eu.goodlike.sub.box.util.require.Require;

import static eu.goodlike.sub.box.util.require.Require.titled;

public final class YoutubeVideoViaPlaylist extends YoutubeVideo {

  public YoutubeVideoViaPlaylist(PlaylistItem playlistItem) {
    this.playlistItem = Require.notNull(playlistItem, titled("playlistItem"));
    assertItemPopulated();
  }

  private final PlaylistItem playlistItem;

  private void assertItemPopulated() {
    Require.notNull(playlistItem.getSnippet(), titled("snippet"));
    Require.notBlank(getVideoId(), titled("videoId"));
    Require.notBlank(getVideoTitle(), titled("videoTitle"));
  }

  @Override
  protected String getVideoTitle() {
    return playlistItem.getSnippet().getTitle();
  }

  @Override
  protected String getVideoId() {
    return playlistItem.getSnippet().getResourceId().getVideoId();
  }

}
