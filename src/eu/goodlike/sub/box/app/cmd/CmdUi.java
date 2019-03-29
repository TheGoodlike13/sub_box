package eu.goodlike.sub.box.app.cmd;

import eu.goodlike.sub.box.app.ApplicationUi;
import eu.goodlike.sub.box.search.Search;
import eu.goodlike.sub.box.youtube.YoutubeWarningException;

public final class CmdUi implements ApplicationUi {

  @Override
  public void signalUnknownCommand() {
    System.out.println("Unknown query. Try again!");
  }

  @Override
  public void signalBrowserSetting(boolean useBrowser) {
    System.out.println("Setting to use browser: " + useBrowser);
  }

  @Override
  public void signalMaxResults(int maxResults) {
    System.out.println("Max results for search set to " + maxResults);
  }

  @Override
  public void signalNoSuchVideo(String videoId) {
    System.out.println("Video with id " + videoId + " has either been deleted, private'd, or never existed in the first place.");
  }

  @Override
  public void signalNoPreviousChannelSearch() {
    System.out.println("Please perform at least one search before using channel position query.");
  }

  @Override
  public void signalNoPreviousPlaylist() {
    System.out.println("Please select at least one playlist before using video position query.");
  }

  @Override
  public void signalInvalidIntegerPosition() {
    System.out.println("Query did not contain a valid number. Position must be positive.");
  }

  @Override
  public void signalTooLargePosition(int max) {
    System.out.println("Position is too large (there are " + max + " elements).");
  }

  @Override
  public void signalSubscribableWarning(String subscribableDescription, YoutubeWarningException e) {
    System.out.println("Cannot get videos for " + subscribableDescription + ": " + e.getMessage());
  }

  @Override
  public void printItem(String itemDescription, Search.Result item) {
    System.out.println(itemDescription + ": " + item.getTitle() + " @" + item.getUrl());
  }

}
