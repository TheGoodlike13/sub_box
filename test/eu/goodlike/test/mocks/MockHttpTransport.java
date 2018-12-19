package eu.goodlike.test.mocks;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.common.collect.Maps;
import eu.goodlike.sub.box.util.Require;
import okhttp3.HttpUrl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.*;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static eu.goodlike.sub.box.util.Require.titled;
import static java.nio.charset.StandardCharsets.UTF_8;

public final class MockHttpTransport extends HttpTransport {

  public YouTube createMockYoutube() {
    return new YouTube.Builder(this, JSON_FACTORY, null)
        .setApplicationName("youtube-test")
        .build();
  }

  public void setResponse(String response, String method, String api, List<String> params) {
    setResponse(response, method, api, params.stream().map(this::toEntry).collect(toImmutableMap(Map.Entry::getKey, Map.Entry::getValue)));
  }

  public void setResponse(String response, String method, String api, Map<String, String> params) {
    HttpUrl.Builder builder = new HttpUrl.Builder();
    params.forEach(builder::addQueryParameter);
    HttpUrl url = builder
        .scheme("https")
        .host("www.googleapis.com")
        .addPathSegment("youtube")
        .addPathSegment("v3")
        .addPathSegment(api)
        .build();
    mockResponses.put(new Key(method, url), response);
  }

  @Override
  protected LowLevelHttpRequest buildRequest(String method, String url) {
    return new MockRequest(mockResponses.get(new Key(method, url)));
  }

  private final Map<MockHttpTransport.Key, String> mockResponses = new HashMap<>();

  private Map.Entry<String, String> toEntry(String query) {
    return Maps.immutableEntry(
        StringUtils.substringBefore(query, "="),
        StringUtils.substringAfterLast(query, "=")
    );
  }

  private static final JacksonFactory JSON_FACTORY = new JacksonFactory();

  private static final class Key {
    private Key(String method, String url) {
      this(method, HttpUrl.get(url));
    }

    private Key(String method, HttpUrl url) {
      this.method = Require.notBlank(method, titled("method")).toUpperCase();
      this.url = sortQuery(url);
    }

    private final String method;
    private final HttpUrl url;

    private HttpUrl sortQuery(HttpUrl url) {
      TreeMap<String, String> sortedQueryParams = new TreeMap<>();
      for (int i = 0; i < url.querySize(); i ++) {
        sortedQueryParams.put(url.queryParameterName(i), url.queryParameterValue(i));
      }
      HttpUrl.Builder builder = url.newBuilder();
      url.queryParameterNames().forEach(builder::removeAllQueryParameters);
      url.queryParameterNames().forEach(builder::removeAllEncodedQueryParameters);
      sortedQueryParams.forEach(builder::addQueryParameter);
      return builder.build();
    }

    @Override
    public String toString() {
      return method + " " + url;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Key key = (Key)o;
      return Objects.equals(method, key.method) &&
          Objects.equals(url, key.url);
    }

    @Override
    public int hashCode() {
      return Objects.hash(method, url);
    }
  }

  private static final class MockRequest extends LowLevelHttpRequest {
    @Override
    public void addHeader(String name, String value) {

    }

    @Override
    public LowLevelHttpResponse execute() {
      return new MockResponse();
    }

    private MockRequest(String response) {
      this.response = StringUtils.trimToEmpty(response);
    }

    private final String response;

    private final class MockResponse extends LowLevelHttpResponse {
      @Override
      public InputStream getContent() {
        return IOUtils.toInputStream(response, UTF_8);
      }

      @Override
      public String getContentEncoding() {
        return null;
      }

      @Override
      public long getContentLength() {
        return response.length();
      }

      @Override
      public String getContentType() {
        return "application/json; charset=utf-8";
      }

      @Override
      public String getStatusLine() {
        return "HTTP/1.1 200 OK";
      }

      @Override
      public int getStatusCode() {
        return 200;
      }

      @Override
      public String getReasonPhrase() {
        return "OK";
      }

      @Override
      public int getHeaderCount() {
        return 0;
      }

      @Override
      public String getHeaderName(int index) {
        return Collections.<String>emptyList().get(index);
      }

      @Override
      public String getHeaderValue(int index) {
        return Collections.<String>emptyList().get(index);
      }
    }
  }

}
