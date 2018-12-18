package eu.goodlike.sub.box.http;

import com.google.api.client.testing.http.MockHttpContent;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static eu.goodlike.test.asserts.Asserts.assertInvalidBlank;
import static eu.goodlike.test.asserts.Asserts.assertInvalidNull;
import static eu.goodlike.test.mocks.OkHttpMocks.basicRequest;
import static eu.goodlike.test.mocks.OkHttpMocks.basicResponse;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.refEq;

public class OkHttpRequestTest {

  private final OkHttpClient clientMock = Mockito.mock(OkHttpClient.class);
  private final Request.Builder request = basicRequest();

  private final Call callMock = Mockito.mock(Call.class);

  @BeforeEach
  public void setup() throws IOException {
    Mockito.when(clientMock.connectTimeoutMillis()).thenReturn(secondsToMillis(20));
    Mockito.when(clientMock.readTimeoutMillis()).thenReturn(secondsToMillis(20));

    Mockito.when(clientMock.newCall(any(Request.class))).thenReturn(callMock);
    Mockito.when(callMock.execute()).thenReturn(basicResponse().build());
  }

  @Test
  public void nullInputs() {
    assertInvalidNull("client", (OkHttpClient client) -> new OkHttpRequest(client, request, "any"));
    assertInvalidNull("request", (Request.Builder request) -> new OkHttpRequest(clientMock, request, "any"));
    assertInvalidBlank("method", blank -> new OkHttpRequest(clientMock, request, blank));
  }

  @Test
  public void addHeader() {
    newRequest("get").addHeader("x-header", "value");

    assertThat(request.build().header("x-header")).isEqualTo("value");
  }

  @Test
  public void setTimeout() {
    OkHttpRequest okHttpRequest = new OkHttpRequest(new OkHttpClient(), request, "get");

    setTimeout(okHttpRequest, 30, 40);

    assertThat(okHttpRequest.client.connectTimeoutMillis()).isEqualTo(secondsToMillis(30));
    assertThat(okHttpRequest.client.readTimeoutMillis()).isEqualTo(secondsToMillis(40));
  }

  @Test
  public void executeRequest() throws IOException {
    newRequest("get").execute();

    Mockito.verify(clientMock).newCall(refEq(request.build()));
    Mockito.verify(callMock).execute();
  }

  @Test
  public void requestWithoutBody() throws IOException {
    newRequest("get").execute();

    Request actualRequest = request.build();
    assertThat(actualRequest.method()).isEqualTo("GET");
    assertThat(actualRequest.body()).isNull();
  }

  @Test
  public void requestWithBody() throws IOException {
    withContent(newRequest("post"), "text", null).execute();

    Request actualRequest = request.build();
    assertThat(actualRequest.method()).isEqualTo("POST");
    assertThat(actualRequest.header("Content-Encoding")).isNull();

    RequestBody body = actualRequest.body();
    assertThat(body).isNotNull();
    assertThat(body.contentType()).isEqualTo(MediaType.get("text/plain"));
    assertThat(body.contentLength()).isEqualTo(4);
  }

  @Test
  public void requestWithContentEncoding() throws IOException {
    withContent(newRequest("post"), "text", "identity").execute();

    Request actualRequest = request.build();
    assertThat(actualRequest.header("Content-Encoding")).isEqualTo("identity");
  }

  @Test
  public void requestWithMethodForWhichBodyIsForbidden() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> withContent(newRequest("get"), "any", null).execute());
  }

  @Test
  public void requestWithMethodForWhichNullBodyIsForbidden() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> newRequest("post").execute());
  }

  @Test
  public void uppercaseMethod() {
    assertThat(newRequest("get").method).isEqualTo("GET");
  }

  private OkHttpRequest newRequest(String method) {
    return new OkHttpRequest(clientMock, request, method);
  }

  private OkHttpRequest withContent(OkHttpRequest httpRequest, String text, String encoding) throws IOException {
    httpRequest.setContentType("text/plain");
    httpRequest.setContentEncoding(encoding);
    httpRequest.setContentLength(text.length());
    MockHttpContent content = new MockHttpContent();
    content.setContent(text.getBytes(UTF_8));
    httpRequest.setStreamingContent(content);
    return httpRequest;
  }

  private void setTimeout(OkHttpRequest okHttpRequest, int connectionTimeoutSeconds, int readTimeoutSeconds) {
    okHttpRequest.setTimeout(secondsToMillis(connectionTimeoutSeconds), secondsToMillis(readTimeoutSeconds));
  }

  private int secondsToMillis(int seconds) {
    return (int)TimeUnit.SECONDS.toMillis(seconds);
  }

}
