package eu.goodlike.youtube;

import eu.goodlike.search.Result;
import eu.goodlike.search.Search;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class YoutubeChannelSearch implements Search {

    @Override
    public List<Result> doSearch(String searchQuery, int maxResults) {
        if (StringUtils.isBlank(searchQuery)) {
            throw new IllegalArgumentException("Cannot be blank: searchQuery");
        }
        if (maxResults <= 0) {
            throw new IllegalArgumentException("Cannot be blank: maxResults");
        }

        return new ArrayList<>();
    }

}
