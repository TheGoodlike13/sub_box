package eu.goodlike.test.mocks.http;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import eu.goodlike.sub.box.util.Require;
import okhttp3.HttpUrl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static eu.goodlike.sub.box.util.Require.titled;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * {@link HttpTransport} implementation which mocks out the HTTP response by matching against the request URL.
 * <p/>
 * To mock out responses for some test class, you will require two types of resources:
 * <p/>1) A file containing definitions of how to relate URLs to responses;
 * <p/>2) Files containing JSON responses.
 * <p/>
 * The first type of resources should be contained next to the test class, have ".mockhttp" suffix and start with
 * lowercase.
 * <p/>
 * When deriving the name from test class, if its name ends with 'Test', it should be also excluded. Some examples:
 * <p/>YoutubeTest.class -> youtube.mockhttp
 * <p/>YoutubeSearchApiTest.class -> youtubeSearchApi.mockhttp
 * <p/>YoutubeSearchIT -> youtubeSearchIT.mockhttp
 * <p/>
 * These resources should have the following format (comments added only for explanation - WILL break the file format):
 * <pre>
 * PATH_TO_MATCH             // string expected in URL path, encoded
 *   PATH_TO_RESOURCE_OF_SECOND_TYPE
 *   OPTIONAL_STATUS_LINE    // defaults to 200 OK; expects 3 digits, a whitespace character and 1+ words for phrase
 *   OPTIONAL_QUERY_TO_MATCH // query expected in URL with format "name=value", encoded, single per line
 *                           // blank line indicates next resource or end of file
 *   PATH_TO_RESOURCE_OF_SECOND_TYPE
 *                           // the above resource will match PATH_TO_MATCH and any query parameters
 * </pre>
 * URLs are matched top to bottom, with first match being used as response.
 * <p/>
 * The second type of resources are searched against the classpath and should contain valid JSON that Google API can
 * parse. It may omit optional values so long as parsing with these values omitted does not cause an error to occur or
 * some strong assumption to be broken.
 */
public final class MockHttpTransport extends HttpTransport {

  public YouTube createMockYoutube() {
    return new YouTube.Builder(this, JSON_FACTORY, null)
        .setApplicationName("youtube-test")
        .build();
  }

  @Override
  protected LowLevelHttpRequest buildRequest(String method, String url) {
    return new MockHttpRequest(findResponse(url));
  }

  public MockHttpTransport(Class<?> testClass) {
    Require.notNull(testClass, titled("testClass"));
    this.responses = parseResponses(testClass, deriveResourceName(testClass));
  }

  public MockHttpTransport(Class<?> testClass, String resource) {
    Require.notNull(testClass, titled("testClass"));
    this.responses = parseResponses(testClass, resource);
  }

  private final List<MockHttpResponse> responses;

  private static String deriveResourceName(Class<?> testClass) {
    String expectedResourceName = testClass.getSimpleName();
    if (expectedResourceName.endsWith("Test"))
      expectedResourceName = StringUtils.substringBeforeLast(expectedResourceName, "Test");

    return StringUtils.uncapitalize(expectedResourceName) + ".mockhttp";
  }

  private List<MockHttpResponse> parseResponses(Class<?> testClass, String resourceName) {
    Require.notBlank(resourceName, titled("resourceName"));
    try (InputStream resourceContainingResponses = getResourceContainingResponses(testClass, resourceName)) {
      return parseResponses(resourceContainingResponses);
    } catch (IOException e) {
      throw new AssertionError("An error occurred while trying to read resource '" + resourceName + "' derived from " + testClass, e);
    }
  }

  private InputStream getResourceContainingResponses(Class<?> testClass, String resourceName) {
    InputStream resourceContainingResponses = testClass.getResourceAsStream(resourceName);
    if (resourceContainingResponses == null)
      throw new AssertionError("Could not find resource '" + resourceName + "' derived from " + testClass);

    return resourceContainingResponses;
  }

  private List<MockHttpResponse> parseResponses(InputStream resourceContainingResponses) throws IOException {
    return new MockHttpResponseParser(IOUtils.readLines(resourceContainingResponses, UTF_8)).parseLines();
  }

  private MockHttpResponse findResponse(String url) {
    HttpUrl httpUrl = HttpUrl.parse(url);
    if (httpUrl == null)
      throw new AssertionError("Invalid URL passed into HttpTransport: " + url);

    return responses.stream()
        .filter(response -> response.matches(httpUrl))
        .findFirst()
        .orElseThrow(() -> new AssertionError("Could not find any response that matches URL: " + httpUrl));
  }

  private static final JacksonFactory JSON_FACTORY = new JacksonFactory();

}
