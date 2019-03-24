import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import eu.goodlike.sub.box.http.OkHttpTransport;
import eu.goodlike.sub.box.http.RequestDebug;
import eu.goodlike.sub.box.http.YoutubeApiKeyProvider;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.List;

public final class YoutubeApiSpike {

  public static void main(String... args) throws IOException {
    OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(new RequestDebug())
        .addInterceptor(new YoutubeApiKeyProvider(PUBLIC_API_KEY))
        .build();

    YouTube youtube = new YouTube.Builder(new OkHttpTransport(client), new JacksonFactory(), null)
        .setApplicationName("sub_box")
        .build();

    getVideo(youtube).forEach(System.out::println);
  }

  private static List<?> getVideo(YouTube youtube) throws IOException {
    YouTube.Videos.List playlist = youtube.videos().list("id,snippet");

    playlist.setMaxResults(1L);
    playlist.setId("PxWCEehAAZE");

    VideoListResponse response = playlist.execute();
    return response.getItems();
  }

  private static List<?> getPlaylistVideos(YouTube youtube) throws IOException {
    YouTube.PlaylistItems.List playlist = youtube.playlistItems().list("id,snippet");

    playlist.setMaxResults(50L);
    playlist.setPlaylistId("PLh0Ul3zO7LAhPnJh-SF59Le5Ui1NDEXU1");

    PlaylistItemListResponse response = playlist.execute();
    return response.getItems();
  }

  private static List<?> performSearch(YouTube youtube) throws IOException {
    YouTube.Search.List search = youtube.search().list("id,snippet");

    search.setMaxResults(13L);
    search.setQ("let's code");

    SearchListResponse response = search.execute();
    return response.getItems();
  }

  private static final String PUBLIC_API_KEY = "AIzaSyC7Z0dhCTFFR_0Gt4YpuFjIlmEPFuvqpg8";

}
