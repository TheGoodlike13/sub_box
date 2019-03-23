package eu.goodlike.sub.box.youtube;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import eu.goodlike.test.mocks.http.MockHttpTransport;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static eu.goodlike.test.asserts.Asserts.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("Convert2MethodRef")
public class YoutubeWarningExceptionTest {

  @Test
  public void nullCause() {
    assertNotNull("cause", (GoogleJsonResponseException e) -> new YoutubeWarningException(e));
  }

  @Test
  public void messageWithoutDetails() {
    assertThat(new YoutubeWarningException(newGoogleJsonResponseExceptionWithoutDetails()))
        .hasMessageContaining("YouTube did not provide an error description.");
  }

  @Test
  public void messageWithDetails() {
    assertThat(new YoutubeWarningException(newGoogleJsonResponseExceptionWithDetails()))
        .hasMessageContaining("The request cannot be completed because you have exceeded your quota.");
  }

  private GoogleJsonResponseException newGoogleJsonResponseExceptionWithoutDetails() {
    return new GoogleJsonResponseException(new HttpResponseException.Builder(403, "Forbidden", new HttpHeaders()), null);
  }

  private GoogleJsonResponseException newGoogleJsonResponseExceptionWithDetails() {
    try {
      performMockCallWhichThrowsGoogleJsonResponseException();
    } catch (GoogleJsonResponseException e) {
      return e;
    }
    throw new AssertionError("Invalid test: no exception thrown at all!");
  }

  private void performMockCallWhichThrowsGoogleJsonResponseException() throws GoogleJsonResponseException {
    try {
      new MockHttpTransport(YoutubeWarningExceptionTest.class)
          .createMockYoutube()
          .search()
          .list("snippet")
          .execute();
    } catch (GoogleJsonResponseException e) {
      throw e;
    } catch (IOException e) {
      throw new AssertionError("Invalid test: incorrect error thrown.", e);
    }
  }

}
