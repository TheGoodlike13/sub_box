package eu.goodlike.sub.box.app.cmd;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import eu.goodlike.sub.box.Subscribable;
import eu.goodlike.sub.box.SubscriptionItem;
import eu.goodlike.sub.box.app.ApplicationProfile;
import eu.goodlike.sub.box.app.ApplicationUi;
import eu.goodlike.sub.box.app.launch.SmartLinkLauncher;
import eu.goodlike.sub.box.app.launch.SmartLinkLauncherImpl;
import eu.goodlike.sub.box.channel.Channel;
import eu.goodlike.sub.box.channel.ChannelSearch;
import eu.goodlike.sub.box.list.PlaylistFactory;
import eu.goodlike.sub.box.search.Search;
import eu.goodlike.sub.box.video.VideoFinder;
import eu.goodlike.sub.box.video.VideoItem;
import eu.goodlike.sub.box.youtube.YoutubeChannelSearch;
import eu.goodlike.sub.box.youtube.YoutubePlaylistFactory;
import eu.goodlike.sub.box.youtube.YoutubeVideoFinder;
import eu.goodlike.sub.box.youtube.YoutubeWarningException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.toImmutableList;

public final class CmdApplication implements AutoCloseable {

  public void interpretInputAndPerformAdequateResponse(String input) {
    if (input.equals("!b"))
      flipBrowserBehavior();
    else if (input.startsWith("!max="))
      setMaxResults(input.substring(5));
    else if (input.startsWith("c="))
      performChannelSearch(input.substring(2));
    else if (input.startsWith("p="))
      showVideosForPlaylist(input.substring(2));
    else if (input.startsWith("v="))
      launchVideoForId(input.substring(2));
    else if (input.startsWith("cn="))
      showUploadsForPosition(input.substring(3));
    else if (input.startsWith("vn="))
      launchVideoForPosition(input.substring(3));
    else
      ui.signalUnknownCommand();
  }

  @Override
  public void close() throws IOException {
    transport.shutdown();
  }

  public CmdApplication(ApplicationProfile profile) {
    this.ui = profile.getUi();

    this.transport = profile.getHttpTransport();

    YouTube youtube = new YouTube.Builder(transport, new JacksonFactory(), null)
        .setApplicationName(profile.getApplicationName())
        .build();

    this.channelSearch = new YoutubeChannelSearch(youtube);
    this.playlistFactory = new YoutubePlaylistFactory(youtube);
    this.videoFinder = new YoutubeVideoFinder(youtube);

    this.linkLauncher = new SmartLinkLauncherImpl(profile.getDefaultLauncher(), profile.getBackupLauncher());
    this.linkLauncher.addListener(profile.getLauncherListener());
  }

  private final ApplicationUi ui;

  private final HttpTransport transport;

  private final ChannelSearch channelSearch;
  private final PlaylistFactory playlistFactory;
  private final VideoFinder videoFinder;

  private final SmartLinkLauncher linkLauncher;

  private int maxResults = DEFAULT_MAX_RESULTS;

  private List<Channel> channels;
  private List<SubscriptionItem> items;

  private void flipBrowserBehavior() {
    boolean useBrowser = linkLauncher.nextDefaultLauncher() == 0;
    ui.signalBrowserSetting(useBrowser);
  }

  private void setMaxResults(String integer) {
    this.maxResults = Math.max(1, Math.min(getIntOrZero(integer), 50));
    ui.signalMaxResults(maxResults);
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

  private void launchVideoForId(String videoId) {
    Optional<VideoItem> video = videoFinder.find(videoId);
    video.ifPresent(this::launchVideo);
    video.orElseGet(() -> printNoSuchVideo(videoId));
  }

  private VideoItem printNoSuchVideo(String videoId) {
    ui.signalNoSuchVideo(videoId);
    return null;
  }

  private void showUploadsForPosition(String positionNumber) {
    if (channels == null)
      ui.signalNoPreviousChannelSearch();
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
      ui.signalNoPreviousPlaylist();
    else {
      Integer position = getPosition(positionNumber, items.size());
      if (position != null)
        launchVideo(items.get(position - 1));
    }
  }

  private void launchVideo(SubscriptionItem item) {
    ui.printItem("Selected video", item);

    linkLauncher.launch(item.getUrl());
  }

  private Integer getPosition(String positionNumber, int max) {
    int position = getIntOrZero(positionNumber);
    if (position <= 0)
      ui.signalInvalidIntegerPosition();
    else if (max < position)
      ui.signalTooLargePosition(max);
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
      ui.signalSubscribableWarning(subscribableDescription, e);
      return Stream.empty();
    }
  }

  private void print(List<? extends Search.Result> printables, String printableDescription) {
    int indexSize = String.valueOf(printables.size()).length();

    int position = 1;
    for (Search.Result printable : printables) {
      String positionString = StringUtils.leftPad(String.valueOf(position++), indexSize);
      ui.printItem(printableDescription + " " + positionString, printable);
    }
  }

  private static final int DEFAULT_MAX_RESULTS = 13;

}
