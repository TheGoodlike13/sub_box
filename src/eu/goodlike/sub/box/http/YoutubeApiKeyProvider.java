package eu.goodlike.sub.box.http;

import com.google.common.collect.ImmutableSet;
import eu.goodlike.sub.box.util.require.Require;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Set;

import static eu.goodlike.sub.box.util.require.Require.titled;

public final class YoutubeApiKeyProvider implements Interceptor {

  @Override
  public Response intercept(Chain chain) throws IOException {
    return chain.proceed(addApiKeyIfNeeded(chain.request()));
  }

  public YoutubeApiKeyProvider(String apiKey) {
    this.apiKey = Require.notBlank(apiKey, titled("apiKey"));
  }

  private final String apiKey;

  private Request addApiKeyIfNeeded(Request request) {
    return needsApiKey(request.url())
        ? addApiKey(request)
        : request;
  }

  private boolean needsApiKey(HttpUrl url) {
    return isApiRequest(url) && !alreadyHasKey(url);
  }

  private boolean isApiRequest(HttpUrl url) {
    return url.host().equals(API_HOST) && url.pathSegments().containsAll(API_PATHS);
  }

  private boolean alreadyHasKey(HttpUrl url) {
    return url.queryParameterNames().contains(API_KEY_PARAM_NAME);
  }

  private Request addApiKey(Request request) {
    return request.newBuilder()
        .url(addApiKeyParam(request.url()))
        .build();
  }

  private HttpUrl addApiKeyParam(HttpUrl url) {
    return url.newBuilder()
    .setQueryParameter(API_KEY_PARAM_NAME, apiKey)
    .build();
  }

  private static final String API_HOST = "www.googleapis.com";
  private static final Set<String> API_PATHS = ImmutableSet.of("youtube", "v3");

  private static final String API_KEY_PARAM_NAME = "key";

}
