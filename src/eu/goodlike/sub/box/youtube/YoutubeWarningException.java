package eu.goodlike.sub.box.youtube;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import eu.goodlike.sub.box.util.require.Require;

import static eu.goodlike.sub.box.util.require.Require.titled;

public class YoutubeWarningException extends RuntimeException {

  public YoutubeWarningException(GoogleJsonResponseException cause) {
    super(deriveMessage(cause), cause);
  }

  private static String deriveMessage(GoogleJsonResponseException cause) {
    GoogleJsonError details = Require.notNull(cause, titled("cause")).getDetails();
    return details == null ? NO_DETAILS_MESSAGE : details.toString();
  }

  private static final String NO_DETAILS_MESSAGE = "YouTube did not provide an error description.";

}
