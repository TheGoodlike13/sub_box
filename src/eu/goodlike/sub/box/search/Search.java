package eu.goodlike.sub.box.search;

import java.util.List;

public interface Search {

    List<Result> doSearch(String searchQuery, int maxResults);

}
