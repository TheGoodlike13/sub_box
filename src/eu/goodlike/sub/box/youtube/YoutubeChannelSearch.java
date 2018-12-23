package eu.goodlike.sub.box.youtube;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
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

    return mapResult(performYoutubeSearch(searchQuery, maxResults));
  }

  public YoutubeChannelSearch(YouTube youtube) {
    this.youtube = Require.notNull(youtube, titled("youtube"));
  }

  private final YouTube youtube;

  private SearchListResponse performYoutubeSearch(String searchQuery, long maxResults) throws IOException {
    YouTube.Search.List search = youtube.search().list("snippet");

    search.setQ(searchQuery);
    search.setMaxResults(maxResults);

    try {
      return search.execute();
    }
    catch (GoogleJsonResponseException e) {
      throw new IllegalStateException(e);
    }
  }

  private List<Result> mapResult(SearchListResponse result) {
    return result.getItems().stream()
        .map(YoutubeChannel::new)
        .collect(toImmutableList());
  }

}
