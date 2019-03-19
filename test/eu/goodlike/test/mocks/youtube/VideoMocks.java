package eu.goodlike.test.mocks.youtube;

import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.ResourceId;
import eu.goodlike.sub.box.youtube.YoutubeVideo;
import okhttp3.HttpUrl;

public final class VideoMocks {

  public static final String OSU_VIDEO_ID = "mNwgepMSn5E";
  public static final String OSU_VIDEO_TITLE = "How to cheat at Osu!";
  public static final HttpUrl OSU_VIDEO_URL = HttpUrl.parse("https://www.youtube.com/watch?v=" + OSU_VIDEO_ID);

  public static YoutubeVideo toYoutubeVideo(String videoId, String videoTitle) {
    ResourceId id = new ResourceId();
    id.setKind("youtube#video");
    id.setVideoId(videoId);

    PlaylistItemSnippet snippet = new PlaylistItemSnippet();
    snippet.setResourceId(id);
    snippet.setTitle(videoTitle);

    PlaylistItem playlistItem = new PlaylistItem();
    playlistItem.setSnippet(snippet);

    return new YoutubeVideo(playlistItem);
  }

  public static YoutubeVideo getOsuVideo() {
    return toYoutubeVideo(OSU_VIDEO_ID, OSU_VIDEO_TITLE);
  }

  private VideoMocks() {
    throw new AssertionError("Do not instantiate! Use static fields/methods!");
  }

}
