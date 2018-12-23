package eu.goodlike.test.mocks.http;

import com.google.api.client.http.LowLevelHttpResponse;
import com.google.common.collect.ImmutableMap;
import okhttp3.HttpUrl;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * {@link LowLevelHttpResponse} implementation used internally by {@link MockHttpTransport}.
 */
final class MockHttpResponse extends LowLevelHttpResponse {

  /**
   * @return true if this response should be returned for given url
   */
  boolean matches(HttpUrl url) {
    return matchesPath(url) && matchesQuery(url);
  }

  @Override
  public InputStream getContent() {
    InputStream responseStream = MockHttpResponse.class.getClassLoader().getResourceAsStream(responseResource);
    if (responseStream == null)
      throw new AssertionError("Cannot find resource containing HTTP mock configuration: " + this);

    return responseStream;
  }

  @Override
  public String getStatusLine() {
    return "HTTP/1.1 " + getStatusCode() + " " + getReasonPhrase();
  }

  @Override
  public int getStatusCode() {
    return statusCode;
  }

  @Override
  public String getReasonPhrase() {
    return reasonPhrase;
  }

  @Override
  public String getContentType() {
    return "application/json; charset=utf-8";
  }

  @Override
  public long getContentLength() {
    return -1;
  }

  @Override
  public String getContentEncoding() {
    return null;
  }

  @Override
  public int getHeaderCount() {
    return 0;
  }

  @Override
  @SuppressWarnings("ConstantConditions")
  public String getHeaderName(int index) {
    return Collections.<String>emptyList().get(index);
  }

  @Override
  @SuppressWarnings("ConstantConditions")
  public String getHeaderValue(int index) {
    return Collections.<String>emptyList().get(index);
  }

  MockHttpResponse(String pathMatcher, Map<String, String> queryMatchers, String responseResource) {
    this(pathMatcher, queryMatchers, responseResource, 200, "OK");
  }

  MockHttpResponse(String pathMatcher, Map<String, String> queryMatchers, String responseResource, int statusCode, String reasonPhrase) {
    this.pathMatcher = pathMatcher;
    this.queryMatchers = ImmutableMap.copyOf(queryMatchers);

    this.responseResource = responseResource;
    this.statusCode = statusCode;
    this.reasonPhrase = reasonPhrase;
  }

  private final String pathMatcher;
  private final Map<String, String> queryMatchers;

  private final String responseResource;
  private final int statusCode;
  private final String reasonPhrase;

  private boolean matchesPath(HttpUrl url) {
    return url.encodedPath().contains(pathMatcher);
  }

  private boolean matchesQuery(HttpUrl url) {
    for (Map.Entry<String, String> queryMatcher : queryMatchers.entrySet()) {
      String queryName = queryMatcher.getKey();
      String queryValue = queryMatcher.getValue();
      if (!Objects.equals(url.queryParameter(queryName), queryValue))
        return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "MockHttpResponse{" +
        "responseResource='" + responseResource + '\'' +
        ", statusCode=" + statusCode +
        ", reasonPhrase='" + reasonPhrase + '\'' +
        ", pathMatcher='" + pathMatcher + '\'' +
        ", queryMatchers=" + queryMatchers +
        '}';
  }

}
