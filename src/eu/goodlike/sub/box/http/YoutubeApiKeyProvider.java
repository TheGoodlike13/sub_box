package eu.goodlike.sub.box.http;

import eu.goodlike.sub.box.util.require.Require;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

import static eu.goodlike.sub.box.util.require.Require.titled;

public final class YoutubeApiKeyProvider implements Interceptor {

  @Override
  public Response intercept(Chain chain) throws IOException {
    return chain.proceed(appendApiKeyIfMissing(chain.request()));
  }

  public YoutubeApiKeyProvider(String apiKey) {
    this.apiKey = Require.notBlank(apiKey, titled("apiKey"));
  }

  private final String apiKey;

  private Request appendApiKeyIfMissing(Request request) {
    return request.url().queryParameterNames().contains(API_KEY_PARAM_NAME)
        ? request
        : appendApiKey(request);
  }

  private Request appendApiKey(Request request) {
    return request.newBuilder()
        .url(appendApiKey(request.url()))
        .build();
  }

  private HttpUrl appendApiKey(HttpUrl url) {
    return url.newBuilder()
    .setQueryParameter(API_KEY_PARAM_NAME, apiKey)
    .build();
  }

  private static final String API_KEY_PARAM_NAME = "key";

}
