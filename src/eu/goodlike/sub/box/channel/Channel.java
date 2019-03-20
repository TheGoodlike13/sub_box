package eu.goodlike.sub.box.channel;

import eu.goodlike.sub.box.search.Result;

import java.util.stream.Stream;

public interface Channel extends Result {

  Stream<Result> getUploadedVideos();

}
