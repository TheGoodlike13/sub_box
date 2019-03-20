package eu.goodlike.sub.box.youtube;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import eu.goodlike.sub.box.channel.Channel;
import eu.goodlike.sub.box.channel.ChannelSearch;
import eu.goodlike.sub.box.util.require.Require;

import java.io.IOException;
import java.util.List;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static eu.goodlike.sub.box.util.require.Require.titled;

public final class YoutubeChannelSearch implements ChannelSearch {

  @Override
  public List<Channel> doSearch(String searchQuery, int maxResults) throws IOException {
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
    // TODO: no test for "snippet". add part=snippet to mock queries? or use a different mechanism?

    search.setQ(searchQuery);
    search.setMaxResults(maxResults);

    try {
      return search.execute();
    }
    catch (GoogleJsonResponseException e) {
      throw new YoutubeWarningException(e);
    }
  }

  private List<Channel> mapResult(SearchListResponse result) {
    // TODO: WTF it returns same channel multiple times???
    return result.getItems().stream()
        .map(YoutubeChannelSearchResult::new)
        .map(searchResult -> new YoutubeChannelViaSearch(youtube, searchResult))
        .collect(toImmutableList());
  }

}
