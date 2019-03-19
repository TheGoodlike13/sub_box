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
    return chain.proceed(appendYoutubeApiKey(chain.request()));
  }

  public YoutubeApiKeyProvider(String apiKey) {
    this.apiKey = Require.notBlank(apiKey, titled("apiKey"));
  }

  private final String apiKey;

  private Request appendYoutubeApiKey(Request request) {
    return needsYoutubeApiKey(request.url())
        ? appendApiKey(request)
        : request;
  }

  private boolean needsYoutubeApiKey(HttpUrl url) {
    return isYoutubeApiRequest(url) && !alreadyHasKey(url);
  }

  private boolean isYoutubeApiRequest(HttpUrl url) {
    return url.host().equals(YOUTUBE_API_HOST) && url.pathSegments().containsAll(YOUTUBE_API_PATH);
  }

  private boolean alreadyHasKey(HttpUrl url) {
    return url.queryParameterNames().contains(API_KEY_PARAM_NAME);
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

  private static final String YOUTUBE_API_HOST = "www.googleapis.com";
  private static final Set<String> YOUTUBE_API_PATH = ImmutableSet.of("youtube", "v3");

  private static final String API_KEY_PARAM_NAME = "key";

}
