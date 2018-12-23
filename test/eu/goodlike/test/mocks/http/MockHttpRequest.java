package eu.goodlike.test.mocks.http;

import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.Collections;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * {@link LowLevelHttpRequest} implementation used internally by {@link MockHttpTransport}.
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

  MockHttpRequest(String successfulResponse) {
    this(IOUtils.toInputStream(StringUtils.trimToEmpty(successfulResponse), UTF_8), 200, "OK");
  }

  MockHttpRequest(InputStream response, int statusCode, String reasonPhrase) {
    this.response = response;
    this.statusCode = statusCode;
    this.reasonPhrase = reasonPhrase;
  }

  private final InputStream response;
  private final int statusCode;
  private final String reasonPhrase;

  private final class MockResponse extends LowLevelHttpResponse {
    @Override
    public InputStream getContent() {
      return response;
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
  }

}
