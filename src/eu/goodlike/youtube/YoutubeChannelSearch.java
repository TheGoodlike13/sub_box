package eu.goodlike.youtube;

import eu.goodlike.search.Result;
import eu.goodlike.search.Search;
import eu.goodlike.util.Require;

import java.util.ArrayList;
import java.util.List;

import static eu.goodlike.util.Require.titled;

public final class YoutubeChannelSearch implements Search {

    @Override
    public List<Result> doSearch(String searchQuery, int maxResults) {
        Require.notBlank(searchQuery, titled("searchQuery"));
        Require.positive(maxResults, titled("maxResults"));

        return new ArrayList<>();
    }

}
