package eu.goodlike.search;

import java.util.List;

public interface Search {

    List<Result> doSearch(String searchQuery, int maxResults);

}
