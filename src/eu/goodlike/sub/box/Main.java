package eu.goodlike.sub.box;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import eu.goodlike.sub.box.channel.Channel;
import eu.goodlike.sub.box.channel.ChannelSearch;
import eu.goodlike.sub.box.http.OkHttpTransport;
import eu.goodlike.sub.box.http.YoutubeApiKeyProvider;
import eu.goodlike.sub.box.youtube.YoutubeChannelSearch;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public final class Main implements AutoCloseable {

  public static void main(String... args) throws IOException {
    int maxResults = getMaxResultsParam(args);

    printIntroduction();

    try (Main main = new Main(maxResults)) {
      main.run();
    }

    System.out.println("Bye!");
  }

  public void run() throws IOException {
    String input;
    while ((input = readInput()) != null)
      interpretInputAndPerformAppropriateTask(input);
  }

  @Override
  public void close() throws IOException {
    transport.shutdown();
  }

  public Main(int maxResults) {
    this.maxResults = maxResults;

    OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(new YoutubeApiKeyProvider(PUBLIC_API_KEY))
        .build();
    this.transport = new OkHttpTransport(client);

    YouTube youtube = new YouTube.Builder(transport, new JacksonFactory(), null)
        .setApplicationName("sub_box")
        .build();
    this.channelSearch = new YoutubeChannelSearch(youtube);
  }

  private final int maxResults;
  private final HttpTransport transport;
  private final ChannelSearch channelSearch;

  private List<Channel> channels;

  private void interpretInputAndPerformAppropriateTask(String input) throws IOException {
    if (input.startsWith("q="))
      performChannelSearch(input.substring(2));
    else
      System.out.println("Unknown query. Try again!");
  }

  private void performChannelSearch(String input) throws IOException {
    this.channels = channelSearch.doSearch(input, maxResults);
    printChannelSearchResult();
  }

  private void printChannelSearchResult() {
    int indexSize = String.valueOf(channels.size()).length();

    int position = 1;
    for (Channel channel : channels) {
      String positionString = StringUtils.leftPad(String.valueOf(position++), indexSize);
      System.out.println("Found channel " + positionString + ": " + channel.getTitle() + " @" + channel.getUrl());
    }
  }

  private static final String DEFAULT_MAX_RESULTS = "13";
  private static final String PUBLIC_API_KEY = "AIzaSyC7Z0dhCTFFR_0Gt4YpuFjIlmEPFuvqpg8";

  private static final Scanner INPUT_READER = new Scanner(System.in);

  private static int getMaxResultsParam(String[] args) {
    String maxResultsArg = args == null || args.length < 1
        ? DEFAULT_MAX_RESULTS
        : args[0];
    return Integer.parseInt(maxResultsArg);
  }

  private static void printIntroduction() {
    System.out.println("Supported queries:");
    System.out.println();
    System.out.println("q=<anyString>");
    System.out.println("  Search for channel");
    System.out.println("  Example: q=TheGoodlike13");
    System.out.println();
    System.out.println("Empty query will exit the program loop.");
  }

  private static String readInput() {
    System.out.println();
    System.out.print("Query: ");
    return StringUtils.trimToNull(INPUT_READER.nextLine());
  }

}
