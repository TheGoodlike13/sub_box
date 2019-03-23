package eu.goodlike.sub.box.youtube;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import eu.goodlike.sub.box.util.require.Require;
import eu.goodlike.sub.box.video.VideoFinder;
import eu.goodlike.sub.box.video.VideoItem;

import java.util.Optional;

import static eu.goodlike.sub.box.util.require.Require.titled;

public final class YoutubeVideoFinder implements VideoFinder {

  @Override
  public Optional<VideoItem> find(String videoId) {
    Require.notBlank(videoId, titled("videoId"));
    return getVideoById(videoId).getItems().stream()
        .map(this::toVideoViaLookup)
        .findFirst();
  }

  public YoutubeVideoFinder(YouTube youtube) {
    this.youtube = Require.notNull(youtube, titled("youtube"));
  }

  private final YouTube youtube;

  private VideoListResponse getVideoById(String videoId) {
    return YoutubeApiUtils.call(() -> youtube.videos().list("id,snippet"), request -> {
      request.setId(videoId);
      request.setMaxResults(1L);
    });
  }

  private VideoItem toVideoViaLookup(Video video) {
    return new YoutubeVideoViaLookup(video);
  }

}
