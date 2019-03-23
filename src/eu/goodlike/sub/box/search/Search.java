package eu.goodlike.sub.box.search;

import java.util.List;

public interface Search {

  List<? extends Result> doSearch(String searchQuery, int maxResults);

}
