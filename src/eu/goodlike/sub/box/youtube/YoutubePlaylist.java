package eu.goodlike.sub.box.youtube;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.common.base.MoreObjects;
import eu.goodlike.sub.box.SubscriptionItem;
import eu.goodlike.sub.box.list.Playlist;
import eu.goodlike.sub.box.util.require.Require;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import static eu.goodlike.sub.box.util.require.Require.titled;

public final class YoutubePlaylist implements Playlist {

  @Override
  public String getId() {
    return playlistId;
  }

  @Override
  public Stream<SubscriptionItem> getCurrentItems() {
    return getAllPlaylistPages()
        .map(PlaylistItemListResponse::getItems)
        .flatMap(Collection::stream)
        .map(YoutubeVideo::new);
  }

  public YoutubePlaylist(YouTube youtube, String playlistId) {
    this.youtube = Require.notNull(youtube, titled("youtube"));
    this.playlistId = Require.notBlank(playlistId, titled("playlistId"));
  }

  private final YouTube youtube;
  private final String playlistId;

  private Stream<PlaylistItemListResponse> getAllPlaylistPages() {
    return StreamEx.iterate(getFirstPage(), response -> getPlaylistPage(response.getNextPageToken()))
        .takeWhileInclusive(response -> StringUtils.isNotBlank(response.getNextPageToken()));
  }

  private PlaylistItemListResponse getFirstPage() {
    return getPlaylistPage(null);
  }

  private PlaylistItemListResponse getPlaylistPage(String pageToken) {
    return YoutubeApiUtils.call(() -> youtube.playlistItems().list("snippet"), request -> {
      request.setPageToken(pageToken);
      request.setPlaylistId(playlistId);
      request.setMaxResults(MAX_RESULTS_MAX_VALUE);
    });
  }

  private static final Long MAX_RESULTS_MAX_VALUE = 50L;

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("playlistId", playlistId)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    YoutubePlaylist that = (YoutubePlaylist)o;
    return Objects.equals(playlistId, that.playlistId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(playlistId);
  }

}
