package eu.goodlike.sub.box.youtube;

import eu.goodlike.sub.box.search.Result;
import eu.goodlike.sub.box.search.Search;
import eu.goodlike.sub.box.util.Require;

import java.util.ArrayList;
import java.util.List;

import static eu.goodlike.sub.box.util.Require.titled;

public final class YoutubeChannelSearch implements Search {

  @Override
  public List<Result> doSearch(String searchQuery, int maxResults) {
    Require.notBlank(searchQuery, titled("searchQuery"));
    Require.positive(maxResults, titled("maxResults"));

    return new ArrayList<>();
  }

}
