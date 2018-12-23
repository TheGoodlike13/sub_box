package eu.goodlike.test.mocks.http;

import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;

/**
 * {@link LowLevelHttpRequest} implementation used internally by {@link MockHttpTransport}.
 */
final class MockHttpRequest extends LowLevelHttpRequest {

  @Override
  public LowLevelHttpResponse execute() {
    return mockResponse;
  }

  @Override
  public void addHeader(String name, String value) {
    // do nothing
  }

  MockHttpRequest(LowLevelHttpResponse mockResponse) {
    this.mockResponse = mockResponse;
  }

  private final LowLevelHttpResponse mockResponse;

}
