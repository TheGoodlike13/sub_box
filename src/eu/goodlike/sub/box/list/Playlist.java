package eu.goodlike.sub.box.list;

import eu.goodlike.sub.box.search.Result;

import java.util.stream.Stream;

public interface Playlist {

  String getId();

  Stream<Result> getVideos();

}
