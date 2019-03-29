package eu.goodlike.sub.box.app;

import eu.goodlike.sub.box.search.Search;
import eu.goodlike.sub.box.youtube.YoutubeWarningException;

public interface ApplicationUi {

  void signalUnknownCommand();

  void signalBrowserSetting(boolean useBrowser);

  void signalMaxResults(int maxResults);

  void signalNoSuchVideo(String videoId);

  void signalNoPreviousChannelSearch();

  void signalNoPreviousPlaylist();

  void signalInvalidIntegerPosition();

  void signalTooLargePosition(int max);

  void signalSubscribableWarning(String subscribableDescription, YoutubeWarningException e);

  void printItem(String itemDescription, Search.Result item);

  void signalNoResultFromChannelSearch();

}
