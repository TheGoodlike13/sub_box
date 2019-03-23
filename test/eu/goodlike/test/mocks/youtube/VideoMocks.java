package eu.goodlike.test.mocks.youtube;

import com.google.api.services.youtube.model.*;
import eu.goodlike.sub.box.youtube.YoutubeVideoViaLookup;
import eu.goodlike.sub.box.youtube.YoutubeVideoViaPlaylist;
import okhttp3.HttpUrl;

public final class VideoMocks {

  public static final String OSU_VIDEO_ID = "mNwgepMSn5E";
  public static final String OSU_VIDEO_TITLE = "How to cheat at Osu!";
  public static final HttpUrl OSU_VIDEO_URL = HttpUrl.parse("https://www.youtube.com/watch?v=" + OSU_VIDEO_ID);

  public static YoutubeVideoViaPlaylist toPlaylistVideo(String videoId, String videoTitle) {
    ResourceId id = new ResourceId();
    id.setKind("youtube#video");
    id.setVideoId(videoId);

    PlaylistItemSnippet snippet = new PlaylistItemSnippet();
    snippet.setResourceId(id);
    snippet.setTitle(videoTitle);

    PlaylistItem playlistItem = new PlaylistItem();
    playlistItem.setSnippet(snippet);

    return new YoutubeVideoViaPlaylist(playlistItem);
  }

  public static YoutubeVideoViaLookup toVideoViaLookup(String videoId, String videoTitle) {
    VideoSnippet snippet = new VideoSnippet();
    snippet.setTitle(videoTitle);

    Video video = new Video();
    video.setId(videoId);
    video.setSnippet(snippet);

    return new YoutubeVideoViaLookup(video);
  }

  public static YoutubeVideoViaPlaylist getOsuPlaylistVideo() {
    return toPlaylistVideo(OSU_VIDEO_ID, OSU_VIDEO_TITLE);
  }

  public static YoutubeVideoViaLookup getOsuVideoLookup() {
    return toVideoViaLookup(OSU_VIDEO_ID, OSU_VIDEO_TITLE);
  }

  private VideoMocks() {
    throw new AssertionError("Do not instantiate! Use static fields/methods!");
  }

}
