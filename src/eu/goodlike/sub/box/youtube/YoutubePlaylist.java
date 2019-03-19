package eu.goodlike.sub.box.youtube;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import eu.goodlike.sub.box.list.Playlist;
import eu.goodlike.sub.box.search.Result;
import eu.goodlike.sub.box.util.require.Require;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Stream;

import static eu.goodlike.sub.box.util.require.Require.titled;

public final class YoutubePlaylist implements Playlist {

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

  private static final Long MAX_RESULTS_MAX_VALUE = 50L;

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
      throw new IllegalStateException(String.valueOf(e.getDetails()), e);
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

}
