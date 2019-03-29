package eu.goodlike.sub.box.app.cmd;

import eu.goodlike.sub.box.app.launch.LinkLauncherType;
import eu.goodlike.sub.box.search.Search;
import eu.goodlike.sub.box.youtube.YoutubeChannelViaSearch;
import eu.goodlike.sub.box.youtube.YoutubeWarningException;
import okhttp3.HttpUrl;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.stream.Stream;

import static eu.goodlike.test.mocks.youtube.ChannelMocks.getGoodlikeChannelResult;
import static eu.goodlike.test.mocks.youtube.ChannelMocks.toChannelSearchResult;
import static eu.goodlike.test.mocks.youtube.VideoMocks.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class CmdApplicationTest {

  private final TestProfile mocksCalled = new TestProfile();
  private final CmdApplication application = new CmdApplication(mocksCalled);

  private final Search.Result goodlikeChannel = new YoutubeChannelViaSearch(application.getYoutube(), getGoodlikeChannelResult());
  private final Search.Result osuVideoFromPlaylist = getOsuPlaylistVideo();
  private final Search.Result osuVideoViaLookup = getOsuVideoLookup();

  private final Search.Result fakeVideo = toPlaylistVideo("fakeId", "fakeTitle");
  private final Search.Result deletedAfterSearch = new YoutubeChannelViaSearch(
      application.getYoutube(),
      toChannelSearchResult("deletedAfterSearch", "Deleted after search")
  );
  private final Search.Result deletedAfterPlaylistLookup = new YoutubeChannelViaSearch(
      application.getYoutube(),
      toChannelSearchResult("almostDeleted", "Deleted after playlist lookup")
  );

  @Test
  public void unknownCommand() {
    input("dasilfasdnfasdf");

    mocksCalled.ui().signalUnknownCommand();
  }

  @Test
  public void changeMaxResults() {
    input("!max=25");

    mocksCalled.ui().signalMaxResults(25);
  }

  @Test
  public void changeMaxResultsInvalid() {
    input("!max=invalid");

    mocksCalled.ui().signalMaxResults(1);
  }

  @Test
  public void changeMaxResultsTooSmall() {
    input("!max=0");

    mocksCalled.ui().signalMaxResults(1);
  }

  @Test
  public void changeMaxResultsTooLarge() {
    input("!max=100");

    mocksCalled.ui().signalMaxResults(50);
  }

  @Test
  public void flipBrowserBehaviour() {
    input("!b");

    mocksCalled.ui().signalBrowserSetting(false);

    input("!b");

    mocksCalled.ui().signalBrowserSetting(true);
  }

  @Test
  public void searchChannel() {
    input("c=thegoodlike13");

    for (int i = 1; i <= 13; i++) {
      String prefix = "Found channel " + StringUtils.leftPad(String.valueOf(i), 2);
      mocksCalled.ui().printItem(prefix, goodlikeChannel);
    }
    mocksCalled.ui(13).printItem(anyString(), any(Search.Result.class));
  }

  @Test
  public void searchChannelNoResults() {
    input("c=is this even possible?");

    mocksCalled.ui().signalNoResultFromChannelSearch();
  }

  @Test
  public void searchChannelOnlyOne() {
    input("!max=1", "c=thegoodlike13");

    mocksCalled.ui().signalMaxResults(1);

    mocksCalled.ui().printItem("Found channel 1", goodlikeChannel);
    mocksCalled.ui(1).printItem(anyString(), any(Search.Result.class));
  }

  @Test
  public void findPlaylist() {
    input("p=PLh0Ul3zO7LAhXL0wblm4z-uWU4RGxtElv");

    for (int i = 1; i <= 100; i++) {
      String prefix = "Playlist video " + StringUtils.leftPad(String.valueOf(i), 3);
      mocksCalled.ui().printItem(prefix, fakeVideo);
    }
    mocksCalled.ui().printItem("Playlist video 101", osuVideoFromPlaylist);
    mocksCalled.ui(101).printItem(anyString(), any(Search.Result.class));
  }

  @Test
  public void findPlaylistDeleted() {
    input("p=deleted");

    mocksCalled.uiWarn(
        "playlist with id deleted",
        e -> e.isInstanceOf(YoutubeWarningException.class).hasMessageContaining("404")
    );
  }

  @Test
  public void findPlaylistPrivate() {
    input("p=private");

    mocksCalled.uiWarn(
        "playlist with id private",
        e -> e.isInstanceOf(YoutubeWarningException.class).hasMessageContaining("403")
    );
  }

  @Test
  public void findVideo() {
    input("v=mNwgepMSn5E");

    mocksCalled.ui().printItem("Selected video", osuVideoViaLookup);
    mocksCalled.launched(osuVideoViaLookup.getUrl(), true);
  }

  @Test
  public void findVideoMissing() {
    input("v=missing");

    mocksCalled.ui().signalNoSuchVideo("missing");
  }

  @Test
  public void switchToClipboardAndFindVideo() {
    input("!b", "v=mNwgepMSn5E");

    mocksCalled.ui().signalBrowserSetting(false);

    mocksCalled.ui().printItem("Selected video", osuVideoViaLookup);
    mocksCalled.launched(osuVideoViaLookup.getUrl(), false);
  }

  @Test
  public void switchToClipboardThenBrowserAndFindVideo() {
    input("!b", "!b", "v=mNwgepMSn5E");

    mocksCalled.ui().signalBrowserSetting(false);

    mocksCalled.ui().signalBrowserSetting(true);

    mocksCalled.ui().printItem("Selected video", osuVideoViaLookup);
    mocksCalled.launched(osuVideoViaLookup.getUrl(), true);
  }

  @Test
  public void findVideoBrowserFails() {
    Mockito.when(mocksCalled.getDefaultLauncher().launch(any(HttpUrl.class))).thenReturn(false);

    input(false, "v=mNwgepMSn5E");

    mocksCalled.ui().printItem("Selected video", osuVideoViaLookup);
    mocksCalled.launched(osuVideoViaLookup.getUrl(), true, true);
  }

  @Test
  public void findVideoBrowserAndClipboardFails() {
    Mockito.when(mocksCalled.getDefaultLauncher().launch(any(HttpUrl.class))).thenReturn(false);
    Mockito.when(mocksCalled.getBackupLauncher().launch(any(HttpUrl.class))).thenReturn(false);

    input(false, "v=mNwgepMSn5E");

    Mockito.verify(mocksCalled.getLauncherListener()).onUnsupported(LinkLauncherType.OTHER, osuVideoViaLookup.getUrl());
    mocksCalled.launched(osuVideoViaLookup.getUrl(), true, true);
  }

  @Test
  public void selectSearchResult() {
    input("!max=1", "c=thegoodlike13", "cn=1");

    mocksCalled.ui().signalMaxResults(1);

    mocksCalled.ui().printItem("Found channel 1", goodlikeChannel);

    mocksCalled.ui().printItem("Uploaded video 1", osuVideoFromPlaylist);
  }

  @Test
  public void selectSearchResultInvalid() {
    input("!max=1", "c=thegoodlike13", "cn=invalid");

    mocksCalled.ui().signalMaxResults(1);

    mocksCalled.ui().printItem("Found channel 1", goodlikeChannel);

    mocksCalled.ui().signalInvalidIntegerPosition();
  }

  @Test
  public void selectSearchResultTooSmall() {
    input("!max=1", "c=thegoodlike13", "cn=0");

    mocksCalled.ui().signalMaxResults(1);

    mocksCalled.ui().printItem("Found channel 1", goodlikeChannel);

    mocksCalled.ui().signalInvalidIntegerPosition();
  }

  @Test
  public void selectSearchResultTooLarge() {
    input("!max=1", "c=thegoodlike13", "cn=2");

    mocksCalled.ui().signalMaxResults(1);

    mocksCalled.ui().printItem("Found channel 1", goodlikeChannel);

    mocksCalled.ui().signalTooLargePosition(1);
  }

  @Test
  public void selectSearchResultDeletedAfterSearch() {
    input("!max=1", "c=deleteAfterSearch", "cn=1");

    mocksCalled.ui().signalMaxResults(1);

    mocksCalled.ui().printItem("Found channel 1", deletedAfterSearch);

    mocksCalled.uiWarn(
        "channel Deleted after search",
        e -> e.isInstanceOf(YoutubeWarningException.class).hasMessageContaining("The channel is deleted or banned.")
    );
  }

  @Test
  public void selectSearchResultDeletedAfterPlaylistLookup() {
    input("!max=1", "c=deleteAfterPlaylistLookup", "cn=1");

    mocksCalled.ui().signalMaxResults(1);

    mocksCalled.ui().printItem("Found channel 1", deletedAfterPlaylistLookup);

    mocksCalled.uiWarn(
        "channel Deleted after playlist lookup",
        e -> e.isInstanceOf(YoutubeWarningException.class).hasMessageContaining("404")
    );
  }

  @Test
  public void selectVideoFromPlaylist() {
    input("p=UUlzfx-qU_5Xufh5BTmKnRdQ", "vn=1");

    mocksCalled.ui().printItem("Playlist video 1", osuVideoFromPlaylist);

    mocksCalled.ui().printItem("Selected video", osuVideoFromPlaylist);
    mocksCalled.launched(osuVideoFromPlaylist.getUrl(), true);
  }

  @Test
  public void selectVideoFromPlaylistInvalid() {
    input("p=UUlzfx-qU_5Xufh5BTmKnRdQ", "vn=invalid");

    mocksCalled.ui().printItem("Playlist video 1", osuVideoFromPlaylist);

    mocksCalled.ui().signalInvalidIntegerPosition();
  }

  @Test
  public void selectVideoFromPlaylistTooSmall() {
    input("p=UUlzfx-qU_5Xufh5BTmKnRdQ", "vn=0");

    mocksCalled.ui().printItem("Playlist video 1", osuVideoFromPlaylist);

    mocksCalled.ui().signalInvalidIntegerPosition();
  }

  @Test
  public void selectVideoFromPlaylistTooLarge() {
    input("p=UUlzfx-qU_5Xufh5BTmKnRdQ", "vn=2");

    mocksCalled.ui().printItem("Playlist video 1", osuVideoFromPlaylist);

    mocksCalled.ui().signalTooLargePosition(1);
  }

  @Test
  public void selectVideoFromChannel() {
    input("!max=1", "c=thegoodlike13", "cn=1", "vn=1");

    mocksCalled.ui().signalMaxResults(1);

    mocksCalled.ui().printItem("Found channel 1", goodlikeChannel);

    mocksCalled.ui().printItem("Uploaded video 1", osuVideoFromPlaylist);

    mocksCalled.ui().printItem("Selected video", osuVideoFromPlaylist);
    mocksCalled.launched(osuVideoFromPlaylist.getUrl(), true);
  }

  @Test
  public void selectVideoFromChannelInvalid() {
    input("!max=1", "c=thegoodlike13", "cn=1", "vn=invalid");

    mocksCalled.ui().signalMaxResults(1);

    mocksCalled.ui().printItem("Found channel 1", goodlikeChannel);

    mocksCalled.ui().printItem("Uploaded video 1", osuVideoFromPlaylist);

    mocksCalled.ui().signalInvalidIntegerPosition();
  }

  @Test
  public void selectVideoFromChannelTooSmall() {
    input("!max=1", "c=thegoodlike13", "cn=1", "vn=0");

    mocksCalled.ui().signalMaxResults(1);

    mocksCalled.ui().printItem("Found channel 1", goodlikeChannel);

    mocksCalled.ui().printItem("Uploaded video 1", osuVideoFromPlaylist);

    mocksCalled.ui().signalInvalidIntegerPosition();
  }

  @Test
  public void selectVideoFromChannelTooLarge() {
    input("!max=1", "c=thegoodlike13", "cn=1", "vn=2");

    mocksCalled.ui().signalMaxResults(1);

    mocksCalled.ui().printItem("Found channel 1", goodlikeChannel);

    mocksCalled.ui().printItem("Uploaded video 1", osuVideoFromPlaylist);

    mocksCalled.ui().signalTooLargePosition(1);
  }

  private void input(String... input) {
    input(true, input);
  }

  private void input(boolean reset, String... input) {
    if (reset)
      mocksCalled.reset();

    Stream.of(input).forEach(application::interpretInputAndPerformAdequateResponse);
  }

}
