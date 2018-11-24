package eu.goodlike.youtube;

import eu.goodlike.search.SearchResult;
import okhttp3.HttpUrl;
import org.apache.commons.lang3.StringUtils;

public final class YoutubeChannel implements SearchResult {

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public HttpUrl getUrl() {
        return null;
    }

    public YoutubeChannel(String channelName, String channelId) {
        if (StringUtils.isBlank(channelName)) {
            throw new IllegalArgumentException("Cannot be blank: channelName");
        }
        if (StringUtils.isBlank(channelId)) {
            throw new IllegalArgumentException("Cannot be blank: channelId");
        }

        this.channelName = channelName;
        this.channelId = channelId;
    }

    private final String channelName;
    private final String channelId;

}
