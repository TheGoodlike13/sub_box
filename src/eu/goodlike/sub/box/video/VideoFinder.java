package eu.goodlike.sub.box.video;

import java.util.Optional;

public interface VideoFinder {

  Optional<VideoItem> find(String videoId);

}
