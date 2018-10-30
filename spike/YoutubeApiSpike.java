import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;

public final class YoutubeApiSpike {

    private static final String PUBLIC_API_KEY = "AIzaSyC7Z0dhCTFFR_0Gt4YpuFjIlmEPFuvqpg8";

    public static void main(String... args) throws IOException {
        YouTube youTube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), null)
                .setApplicationName("sub_box")
                .build();

        YouTube.Search.List search = youTube.search().list("id,snippet");

        search.setKey(PUBLIC_API_KEY);
        search.setMaxResults(13L);
        search.setQ("let's code");

        SearchListResponse response = search.execute();
        for (SearchResult item : response.getItems()) {
            System.out.println(item);
        }
    }

}
