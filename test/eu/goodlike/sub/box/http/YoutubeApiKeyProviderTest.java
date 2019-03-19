package eu.goodlike.sub.box.http;

import eu.goodlike.test.mocks.OkHttpMocks;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;

import static eu.goodlike.test.asserts.Asserts.assertNotBlank;
import static org.assertj.core.api.Assertions.assertThat;

public class YoutubeApiKeyProviderTest {

  private static final HttpUrl YOUTUBE_API_URL = HttpUrl.parse("https://www.googleapis.com/youtube/v3/stuff");

  private static final String API_KEY_PARAM_NAME = "key";
  private static final String API_KEY_PARAM_VALUE = "mock_key";

  private final Interceptor apiKeyProvider = new YoutubeApiKeyProvider(API_KEY_PARAM_VALUE);

  private final Interceptor.Chain chain = Mockito.mock(Interceptor.Chain.class);
  private final ArgumentCaptor<Request> request = ArgumentCaptor.forClass(Request.class);

  @Test
  public void noBlankApiKey() {
    assertNotBlank("apiKey", YoutubeApiKeyProvider::new);
  }

  @Test
  public void randomUrl_doNotAddKey() throws IOException {
    Request originalRequest = OkHttpMocks.basicRequest().build();
    Mockito.when(chain.request()).thenReturn(originalRequest);

    apiKeyProvider.intercept(chain);

    Mockito.verify(chain).proceed(request.capture());
    HttpUrl finalUrl = request.getValue().url();
    assertThat(finalUrl.queryParameterNames()).doesNotContain(API_KEY_PARAM_NAME);
  }

  @Test
  public void youtubeApi_addKey() throws IOException {
    Request originalRequest = OkHttpMocks.basicRequest()
        .url(YOUTUBE_API_URL)
        .build();
    Mockito.when(chain.request()).thenReturn(originalRequest);

    apiKeyProvider.intercept(chain);

    Mockito.verify(chain).proceed(request.capture());
    HttpUrl finalUrl = request.getValue().url();
    assertThat(finalUrl.queryParameter(API_KEY_PARAM_NAME)).isEqualTo(API_KEY_PARAM_VALUE);
  }

  @Test
  public void youtubeApi_alreadyHasKey_doNotAddKey() throws IOException {
    Request originalRequest = OkHttpMocks.basicRequest()
        .url(YOUTUBE_API_URL.newBuilder()
            .setQueryParameter(API_KEY_PARAM_NAME, "original_key")
            .build())
        .build();
    Mockito.when(chain.request()).thenReturn(originalRequest);

    apiKeyProvider.intercept(chain);

    Mockito.verify(chain).proceed(request.capture());
    HttpUrl finalUrl = request.getValue().url();
    assertThat(finalUrl.queryParameter(API_KEY_PARAM_NAME)).isEqualTo("original_key");
  }

}
