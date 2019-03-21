package eu.goodlike.sub.box.youtube;

import com.google.api.services.youtube.YouTube;
import eu.goodlike.sub.box.list.Playlist;
import eu.goodlike.sub.box.list.PlaylistFactory;
import eu.goodlike.sub.box.util.require.Require;

import static eu.goodlike.sub.box.util.require.Require.titled;

public final class YoutubePlaylistFactory implements PlaylistFactory {

  @Override
  public Playlist newPlaylist(String playlistId) {
    return new YoutubePlaylist(youtube, playlistId);
  }

  public YoutubePlaylistFactory(YouTube youtube) {
    this.youtube = Require.notNull(youtube, titled("youtube"));
  }

  private final YouTube youtube;

}
