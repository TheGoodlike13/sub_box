package eu.goodlike.sub.box.youtube;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTubeRequest;

import java.io.IOException;
import java.util.function.Consumer;

final class YoutubeApiUtils {

  static <T, R extends YouTubeRequest<T>> T call(YoutubeApiRequestSupplier<T, R> requestSupplier, Consumer<R> paramSetter) {
    R request = newRequest(requestSupplier);
    paramSetter.accept(request);
    return executeRequest(request);
  }

  private YoutubeApiUtils() {
    throw new AssertionError("Do not instantiate! Use static fields/methods!");
  }

  private static <T, R extends YouTubeRequest<T>> R newRequest(YoutubeApiRequestSupplier<T, R> requestSupplier) {
    try {
      return requestSupplier.createRequest();
    } catch (IOException e) {
      throw new IllegalStateException("Cannot initiate YouTube requests. Some YouTube request initializer throw an exception.", e);
    }
  }

  private static <T, R extends YouTubeRequest<T>> T executeRequest(R request) {
    try {
      return request.execute();
    } catch (GoogleJsonResponseException e) {
      throw new YoutubeWarningException(e);
    } catch (IOException e) {
      throw new YoutubeErrorException("Cannot get valid response from YouTube API.", e);
    }
  }

}
