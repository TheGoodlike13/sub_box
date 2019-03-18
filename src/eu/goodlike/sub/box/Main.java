package eu.goodlike.sub.box;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import eu.goodlike.sub.box.http.OkHttpTransport;
import eu.goodlike.sub.box.http.YoutubeApiKeyProvider;
import eu.goodlike.sub.box.search.Result;
import eu.goodlike.sub.box.search.Search;
import eu.goodlike.sub.box.youtube.YoutubeChannelSearch;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Scanner;

public final class Main implements AutoCloseable {

  public static void main(String... args) throws IOException {
    try (Main main = new Main()) {
      main.run();
    }

    System.out.println("Bye!");
  }

  public void run() throws IOException {
    System.out.println("Channel search");

    String input;
    while ((input = readInput()) != null)
      performSearch(input);
  }

  @Override
  public void close() throws IOException {
    transport.shutdown();
  }

  public Main() {
    OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(new YoutubeApiKeyProvider(PUBLIC_API_KEY))
        .build();
    this.transport = new OkHttpTransport(client);

    YouTube youtube = new YouTube.Builder(transport, new JacksonFactory(), null)
        .setApplicationName("sub_box")
        .build();
    this.channelSearch = new YoutubeChannelSearch(youtube);
  }

  private final HttpTransport transport;
  private final Search channelSearch;

  private void performSearch(String input) throws IOException {
    channelSearch.doSearch(input, 1).forEach(this::printSearchResult);
  }

  private void printSearchResult(Result result) {
    System.out.println("Found channel: " + result.getTitle() + " @" + result.getUrl());
  }

  private static final Scanner INPUT_READER = new Scanner(System.in);
  private static final String PUBLIC_API_KEY = "AIzaSyC7Z0dhCTFFR_0Gt4YpuFjIlmEPFuvqpg8";

  private static String readInput() {
    System.out.print("Search query: ");
    return StringUtils.trimToNull(INPUT_READER.nextLine());
  }

}
