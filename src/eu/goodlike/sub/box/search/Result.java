package eu.goodlike.sub.box.search;

import okhttp3.HttpUrl;

public interface Result {

  String getTitle();

  HttpUrl getUrl();

}