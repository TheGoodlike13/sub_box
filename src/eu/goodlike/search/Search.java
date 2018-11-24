package eu.goodlike.search;

import java.util.List;

public interface Search {

    List<SearchResult> doSearch(String searchQuery, int maxResults);

}
