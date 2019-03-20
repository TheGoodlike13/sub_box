package eu.goodlike.sub.box.youtube;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.common.base.MoreObjects;
import eu.goodlike.sub.box.list.Playlist;
import eu.goodlike.sub.box.search.Result;
import eu.goodlike.sub.box.util.require.Require;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
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
  public Stream<Result> getVideos() {
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
    return StreamEx.iterate(getFirstPage(), response -> tryGetPlaylistPage(response.getNextPageToken()))
        .takeWhileInclusive(response -> StringUtils.isNotBlank(response.getNextPageToken()));
  }

  private PlaylistItemListResponse getFirstPage() {
    return tryGetPlaylistPage(null);
  }

  private PlaylistItemListResponse tryGetPlaylistPage(String pageToken) {
    try {
      return getPlaylistPage(pageToken);
    } catch (GoogleJsonResponseException e) {
      throw new YoutubeWarningException(e);
    } catch (IOException e) {
      throw new IllegalStateException("Unexpected error", e);
    }
  }

  private PlaylistItemListResponse getPlaylistPage(String pageToken) throws IOException {
    YouTube.PlaylistItems.List request = youtube.playlistItems().list("snippet");

    request.setPageToken(pageToken);
    request.setPlaylistId(playlistId);
    request.setMaxResults(MAX_RESULTS_MAX_VALUE);

    return request.execute();
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
