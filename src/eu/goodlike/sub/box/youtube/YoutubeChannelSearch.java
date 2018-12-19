package eu.goodlike.sub.box.youtube;

import com.google.api.services.youtube.YouTube;
import eu.goodlike.sub.box.search.Result;
import eu.goodlike.sub.box.search.Search;
import eu.goodlike.sub.box.util.Require;

import java.io.IOException;
import java.util.List;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static eu.goodlike.sub.box.util.Require.titled;

public final class YoutubeChannelSearch implements Search {

  @Override
  public List<Result> doSearch(String searchQuery, int maxResults) throws IOException {
    Require.notBlank(searchQuery, titled("searchQuery"));
    Require.positive(maxResults, titled("maxResults"));

    YouTube.Search.List search = youtube.search().list("snippet");

    search.setQ(searchQuery);
    search.setMaxResults((long)maxResults);

    return search.execute().getItems().stream()
        .map(YoutubeChannel::new)
        .collect(toImmutableList());
  }

  public YoutubeChannelSearch(YouTube youtube) {
    this.youtube = Require.notNull(youtube, titled("youtube"));
  }

  private final YouTube youtube;

}
