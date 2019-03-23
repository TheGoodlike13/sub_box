package eu.goodlike.sub.box.search;

import okhttp3.HttpUrl;

import java.util.List;

public interface Search {

  List<? extends Result> doSearch(String searchQuery, int maxResults);

  interface Result {
    String getId();

    String getTitle();

    HttpUrl getUrl();
  }

}
