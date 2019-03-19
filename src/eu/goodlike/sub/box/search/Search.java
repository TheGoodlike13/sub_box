package eu.goodlike.sub.box.search;

import java.io.IOException;
import java.util.List;

public interface Search {

  // TODO: IOException may not be the best solution?
  List<Result> doSearch(String searchQuery, int maxResults) throws IOException;

}
