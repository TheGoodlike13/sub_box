package eu.goodlike.search;

import okhttp3.HttpUrl;

public interface SearchResult {

    String getTitle();

    HttpUrl getUrl();

}
