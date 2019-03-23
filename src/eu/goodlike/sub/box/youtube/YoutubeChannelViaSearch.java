package eu.goodlike.sub.box.youtube;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.common.base.MoreObjects;
import eu.goodlike.sub.box.SubscriptionItem;
import eu.goodlike.sub.box.channel.Channel;
import eu.goodlike.sub.box.list.Playlist;
import eu.goodlike.sub.box.util.require.Require;
import okhttp3.HttpUrl;

import java.util.Objects;
import java.util.stream.Stream;

import static eu.goodlike.sub.box.util.require.Require.titled;

public final class YoutubeChannelViaSearch implements Channel {

  @Override
  public String getId() {
    return result.getId();
  }

  @Override
  public String getTitle() {
    return result.getTitle();
  }

  @Override
  public HttpUrl getUrl() {
    return result.getUrl();
  }

  @Override
  public Stream<SubscriptionItem> getCurrentItems() {
    return getUploadPlaylist().getCurrentItems();
  }

  public YoutubeChannelViaSearch(YouTube youtube, YoutubeChannelSearchResult result) {
    this.youtube = Require.notNull(youtube, titled("youtube"));
    this.result = Require.notNull(result, titled("result"));
  }

  private final YouTube youtube;
  private final YoutubeChannelSearchResult result;

  private Playlist getUploadPlaylist() {
    return new YoutubePlaylist(youtube, getUploadPlaylistId(findChannelInfo()));
  }

  private ChannelListResponse findChannelInfo() {
    return YoutubeApiUtils.call(() -> youtube.channels().list("contentDetails"), request -> {
      request.setId(getId());
      request.setMaxResults(1L);
    });
  }

  private String getUploadPlaylistId(ChannelListResponse response) {
    if (response.getItems().isEmpty())
      throw new YoutubeWarningException("The channel is deleted or banned.");

    return response.getItems().get(0).getContentDetails().getRelatedPlaylists().getUploads();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("result", result)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    YoutubeChannelViaSearch that = (YoutubeChannelViaSearch)o;
    return Objects.equals(result, that.result);
  }

  @Override
  public int hashCode() {
    return Objects.hash(result);
  }

}
