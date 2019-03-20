package eu.goodlike.sub.box.channel;

import eu.goodlike.sub.box.search.Search;

import java.io.IOException;
import java.util.List;

public interface ChannelSearch extends Search {

  @Override
  List<Channel> doSearch(String searchQuery, int maxResults) throws IOException;

}
