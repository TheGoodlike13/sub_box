package eu.goodlike.youtube;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class YoutubeChannelTest {

    @Test
    public void noBlankName() {
        assertInvalidChannel("channelName", null, "any");
        assertInvalidChannel("channelName", " ", "any");
    }

    @Test
    public void noBlankId() {
        assertInvalidChannel("channelId", "any", null);
        assertInvalidChannel("channelId", "any", " ");
    }

    private void assertInvalidChannel(String errorType, String channelName, String channelId) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new YoutubeChannel(channelName, channelId))
                .withMessageContaining(errorType);
    }

}
