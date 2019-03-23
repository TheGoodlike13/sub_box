package eu.goodlike.sub.box.youtube;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import com.google.api.services.youtube.YouTubeRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class YoutubeApiUtilsTest {

  @Test
  public void requestCannotBeMade() {
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> YoutubeApiUtils.call(this::throwIOException, request -> {}))
        .withMessageContaining("Cannot initiate YouTube requests. Some YouTube request initializer throw an exception.");
  }

  @Test
  public void responseCannotBeRetrieved() {
    assertThatExceptionOfType(YoutubeErrorException.class)
        .isThrownBy(() -> YoutubeApiUtils.call(() -> mockYoutubeRequest(any -> throwIOException()), request -> {}))
        .withMessageContaining("Cannot get valid response from YouTube API.");
  }

  @Test
  public void responseContainsError() {
    assertThatExceptionOfType(YoutubeWarningException.class)
        .isThrownBy(() -> YoutubeApiUtils.call(() -> mockYoutubeRequest(any -> throwJsonResponseException()), request -> {}))
        .withMessageContaining("YouTube did not provide an error description.");
  }

  @Test
  public void successfulResponse() {
    String result = YoutubeApiUtils.call(() -> mockYoutubeRequest(any -> "success!"), request -> {});

    assertThat(result).isEqualTo("success!");
  }

  @Test
  public void parametersAreSet() throws IOException {
    YouTubeRequest<String> request = mockYoutubeRequest(any -> "success!");

    YoutubeApiUtils.call(() -> request, r -> r.set("param", "value"));

    Mockito.verify(request).set("param", "value");
  }

  private YouTubeRequest<String> throwIOException() throws IOException {
    throw new IOException("IO try. IO fail.");
  }

  private YouTubeRequest<String> mockYoutubeRequest(Answer<?> answer) throws IOException {
    @SuppressWarnings("unchecked")
    YouTubeRequest<String> request = Mockito.mock(YouTubeRequest.class);
    Mockito.when(request.execute()).then(answer);
    return request;
  }

  private Object throwJsonResponseException() throws GoogleJsonResponseException {
    throw new GoogleJsonResponseException(new HttpResponseException.Builder(403, "Forbidden", new HttpHeaders()), null);
  }

}
