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

  private static final String KEY = "mock_key";

  private final Interceptor apiKeyProvider = new YoutubeApiKeyProvider(KEY);

  private final Interceptor.Chain chain = Mockito.mock(Interceptor.Chain.class);
  private final ArgumentCaptor<Request> request = ArgumentCaptor.forClass(Request.class);

  @Test
  public void noBlankApiKey() {
    assertNotBlank("apiKey", YoutubeApiKeyProvider::new);
  }

  @Test
  public void addApiKeyToUrl() throws IOException {
    Request originalRequest = OkHttpMocks.basicRequest().build();
    Mockito.when(chain.request()).thenReturn(originalRequest);

    apiKeyProvider.intercept(chain);

    Mockito.verify(chain).proceed(request.capture());
    HttpUrl finalUrl = request.getValue().url();
    assertThat(finalUrl.queryParameter("key")).isEqualTo(KEY);
  }

  @Test
  public void doNotOverrideApiKeyInUrl() throws IOException {
    Request originalRequest = OkHttpMocks.basicRequest()
        .url(HttpUrl.parse(OkHttpMocks.basicUrl()).newBuilder()
            .setQueryParameter("key", "original_key")
            .build())
        .build();
    Mockito.when(chain.request()).thenReturn(originalRequest);

    apiKeyProvider.intercept(chain);

    Mockito.verify(chain).proceed(request.capture());
    HttpUrl finalUrl = request.getValue().url();
    assertThat(finalUrl.queryParameter("key")).isEqualTo("original_key");
  }

}
