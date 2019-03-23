package eu.goodlike.sub.box.youtube;

import java.io.IOException;

public class YoutubeErrorException extends RuntimeException {

  YoutubeErrorException(String message, IOException cause) {
    super(message, cause);
  }

}
