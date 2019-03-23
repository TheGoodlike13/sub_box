package eu.goodlike.sub.box.youtube;

import com.google.api.services.youtube.YouTubeRequest;

import java.io.IOException;

@FunctionalInterface
interface YoutubeApiRequestSupplier<T, R extends YouTubeRequest<T>> {

  R createRequest() throws IOException;

}
