import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import eu.goodlike.sub.box.http.OkHttpTransport;
import okhttp3.OkHttpClient;

import java.io.IOException;

public final class YoutubeApiSpike {

  public static void main(String... args) throws IOException {
    YouTube youtube = new YouTube.Builder(new OkHttpTransport(new OkHttpClient()), new JacksonFactory(), null)
        .setApplicationName("sub_box")
        .build();

    YouTube.PlaylistItems.List playlist = youtube.playlistItems().list("id,snippet");

    playlist.setKey(PUBLIC_API_KEY);
    playlist.setMaxResults(50L);
    playlist.setPlaylistId("PLh0Ul3zO7LAgYgCMftymjlVtvZr_Ywtva");

    PlaylistItemListResponse response = playlist.execute();
    for (PlaylistItem item : response.getItems()) {
      System.out.println(item);
    }
  }

  private static void performSearch(YouTube youtube) throws IOException {
    YouTube.Search.List search = youtube.search().list("id,snippet");

    search.setKey(PUBLIC_API_KEY);
    search.setMaxResults(13L);
    search.setQ("let's code");

    SearchListResponse response = search.execute();
    for (SearchResult item : response.getItems()) {
      System.out.println(item);
    }
  }

  private static final String PUBLIC_API_KEY = "AIzaSyC7Z0dhCTFFR_0Gt4YpuFjIlmEPFuvqpg8";

}
