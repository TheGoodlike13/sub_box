package eu.goodlike.test.mocks.http;

import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.Collections;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * {@link LowLevelHttpRequest} implementation used internally by {@link MockHttpTransport}. Always returns an 200 OK
 * with a pre-configured {@link String} response.
 */
final class MockHttpRequest extends LowLevelHttpRequest {

  @Override
  public LowLevelHttpResponse execute() {
    return new MockResponse();
  }

  @Override
  public void addHeader(String name, String value) {
    // do nothing
  }

  MockHttpRequest(String response) {
    this.response = StringUtils.trimToEmpty(response);
  }

  private final String response;

  private final class MockResponse extends LowLevelHttpResponse {
    @Override
    public InputStream getContent() {
      return IOUtils.toInputStream(response, UTF_8);
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
    public String getContentType() {
      return "application/json; charset=utf-8";
    }

    @Override
    public long getContentLength() {
      return response.length();
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
  }

}
