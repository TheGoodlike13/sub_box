package eu.goodlike.sub.box;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import eu.goodlike.sub.box.channel.Channel;
import eu.goodlike.sub.box.channel.ChannelSearch;
import eu.goodlike.sub.box.http.OkHttpTransport;
import eu.goodlike.sub.box.http.RequestDebug;
import eu.goodlike.sub.box.http.YoutubeApiKeyProvider;
import eu.goodlike.sub.box.list.PlaylistFactory;
import eu.goodlike.sub.box.search.Search;
import eu.goodlike.sub.box.youtube.YoutubeChannelSearch;
import eu.goodlike.sub.box.youtube.YoutubePlaylistFactory;
import eu.goodlike.sub.box.youtube.YoutubeWarningException;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.toImmutableList;

public final class Main implements AutoCloseable {

  public static void main(String... args) throws IOException {
    printIntroduction();

    printDefaultBrowserBehavior();

    try (Main main = new Main()) {
      main.run();
    }

    System.out.println("Bye!");
  }

  public void run() {
    String input;
    while ((input = readInput()) != null)
      interpretInputAndPerformAppropriateTask(input);
  }

  @Override
  public void close() throws IOException {
    transport.shutdown();
  }

  public Main() {
    OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(new RequestDebug())
        .addInterceptor(new YoutubeApiKeyProvider(PUBLIC_API_KEY))
        .build();
    this.transport = new OkHttpTransport(client);

    YouTube youtube = new YouTube.Builder(transport, new JacksonFactory(), null)
        .setApplicationName("sub_box")
        .build();

    this.channelSearch = new YoutubeChannelSearch(youtube);
    this.playlistFactory = new YoutubePlaylistFactory(youtube);
  }

  private final HttpTransport transport;
  private final ChannelSearch channelSearch;
  private final PlaylistFactory playlistFactory;

  private int maxResults = DEFAULT_MAX_RESULTS;
  private boolean useBrowser = true;

  private List<Channel> channels;
  private List<SubscriptionItem> items;

  private void interpretInputAndPerformAppropriateTask(String input) {
    if (input.equals("!b"))
      flipBrowserBehavior();
    else if (input.startsWith("!max="))
      setMaxResults(input.substring(5));
    else if (input.startsWith("c="))
      performChannelSearch(input.substring(2));
    else if (input.startsWith("p="))
      showVideosForPlaylist(input.substring(2));
    else if (input.startsWith("cn="))
      showUploadsForPosition(input.substring(3));
    else if (input.startsWith("vn="))
      launchVideoForPosition(input.substring(3));
    else
      System.out.println("Unknown query. Try again!");
  }

  private void flipBrowserBehavior() {
    this.useBrowser = !this.useBrowser;
    System.out.println("Setting to use browser: " + useBrowser);
  }

  private void setMaxResults(String integer) {
    this.maxResults = Math.max(0, Math.min(getIntOrZero(integer), 50));
    System.out.println("Max results for search set to " + maxResults);
  }

  private void performChannelSearch(String channelSearchQuery) {
    this.channels = channelSearch.doSearch(channelSearchQuery, maxResults);
    printChannels();
  }

  private void printChannels() {
    print(channels, "Found channel");
  }

  private void showVideosForPlaylist(String playlistId) {
    this.items = getVideosForPlaylist(playlistId).collect(toImmutableList());
    print(items, "Playlist video");
  }

  private Stream<SubscriptionItem> getVideosForPlaylist(String playlistId) {
    return getItems(playlistFactory.newPlaylist(playlistId), "playlist with id " + playlistId);
  }

  private void showUploadsForPosition(String positionNumber) {
    if (channels == null)
      System.out.println("Please perform at least one search before using channel position query.");
    else {
      Integer position = getPosition(positionNumber, channels.size());
      if (position != null)
        printUploads(channels.get(position - 1));
    }
  }

  private void printUploads(Channel channel) {
    this.items = getItems(channel, "channel " + channel.getTitle()).collect(toImmutableList());
    print(items, "Uploaded video");
  }

  private void launchVideoForPosition(String positionNumber) {
    if (items == null)
      System.out.println("Please select at least one playlist before using video position query.");
    else {
      Integer position = getPosition(positionNumber, items.size());
      if (position != null)
        launchVideo(items.get(position - 1));
    }
  }

  private void launchVideo(SubscriptionItem item) {
    if (isBrowserSupported() && useBrowser)
      launchInBrowser(item);
    else
      launchToClipboard(item);
  }

  private void launchInBrowser(SubscriptionItem item) {
    try {
      Desktop.getDesktop().browse(item.getUrl().uri());
      System.out.println("Video URL launched in browser.");
    } catch (IOException e) {
      System.out.println("Could not launch video URL in browser: " + e);
      launchToClipboard(item);
    }
  }

  private void launchToClipboard(SubscriptionItem item) {
    try {
      StringSelection clipboardData = new StringSelection(item.getUrl().toString());
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(clipboardData, clipboardData);
      System.out.println("Video URL copied to clipboard.");
    } catch (Exception e) {
      System.out.println("Could not copy video URL to clipboard: " + e);
    }
  }

  private Integer getPosition(String positionNumber, int max) {
    int position = getIntOrZero(positionNumber);
    if (position <= 0)
      System.out.println("Query did not contain a valid number. Position must be positive.");
    else if (max < position)
      System.out.println("Position is too large (there are " + max + " elements).");
    else
      return position;

    return null;
  }

  private int getIntOrZero(String intInput) {
    try {
      return Integer.parseInt(intInput);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  private Stream<SubscriptionItem> getItems(Subscribable subscribable, String subscribableDescription) {
    try {
      return subscribable.getCurrentItems();
    }
    catch (YoutubeWarningException e) {
      System.out.println("Cannot get videos for " + subscribableDescription + ": " + e.getMessage());
      return Stream.empty();
    }
  }

  private void print(List<? extends Search.Result> printables, String printableDescription) {
    int indexSize = String.valueOf(printables.size()).length();

    int position = 1;
    for (Search.Result printable : printables) {
      String positionString = StringUtils.leftPad(String.valueOf(position++), indexSize);
      System.out.println(printableDescription + " " + positionString + ": " + printable.getTitle() + " @" + printable.getUrl());
    }
  }

  private static final int DEFAULT_MAX_RESULTS = 13;
  private static final String PUBLIC_API_KEY = "AIzaSyC7Z0dhCTFFR_0Gt4YpuFjIlmEPFuvqpg8";

  private static final Scanner INPUT_READER = new Scanner(System.in);

  private static void printIntroduction() {
    System.out.println("Supported queries:");
    System.out.println();
    System.out.println("!max=<integer>");
    System.out.println("  Example: !max=50");
    System.out.println("  Sets max queries for follow-up searches. Value will be constrained: 1 <= max <= 50");
    System.out.println();
    System.out.println("!b");
    System.out.println("  Flip browser behavior.");
    System.out.println("  If no browser is available, this option does nothing. All launches go to clipboard.");
    System.out.println("  If browser is available, switches to using clipboard instead, or back to browser.");
    System.out.println();
    System.out.println("c=<channelSearchQuery>");
    System.out.println("  Example: c=TheGoodlike13");
    System.out.println("  Search for channel, by any related query.");
    System.out.println();
    System.out.println("p=<playlistId>");
    System.out.println("  Example: p=PLh0Ul3zO7LAhXL0wblm4z-uWU4RGxtElv");
    System.out.println("  Retrieve videos in playlist, by id.");
    System.out.println();
    System.out.println("v=<videoId>");
    System.out.println("  Example: v=mNwgepMSn5E");
    System.out.println("  Retrieve video info, by id. Video url is launched.");
    System.out.println();
    System.out.println("cn=<positionNumber>");
    System.out.println("  Example: cn=1");
    System.out.println("  Print n-th channel's uploads. Channel is taken from last search.");
    System.out.println();
    System.out.println("vn=<positionNumber>");
    System.out.println("  Example: vn=1");
    System.out.println("  Launch n-th video. Video is taken from last playlist or channel.");
    System.out.println();
    System.out.println("Empty query will exit the program loop.");
  }

  private static void printDefaultBrowserBehavior() {
    System.out.println();

    if (isBrowserSupported())
      System.out.println("Launching over browser supported.");
    else
      System.out.println("Cannot launch over browser. Using clipboard instead.");
  }

  private static boolean isBrowserSupported() {
    return Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);
  }

  private static String readInput() {
    System.out.println();
    System.out.print("Query: ");
    return StringUtils.trimToNull(INPUT_READER.nextLine());
  }

}
