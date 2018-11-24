package eu.goodlike.util;

import org.junit.jupiter.api.Test;

import static eu.goodlike.util.Require.titled;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class RequireTest {

    @Test
    public void blankTitle() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> titled(null))
                .withMessageContaining("title")
                .withMessageContaining("blank");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> titled(" "))
                .withMessageContaining("title")
                .withMessageContaining("blank");
    }

    @Test
    public void title() {
        Require.Title title = titled("input");

        assertThat(title).isNotNull()
                .extracting(Require.Title::getTitle)
                .isEqualTo("input");
    }

    @Test
    public void notBlank() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Require.notBlank(null))
                .withMessageContaining("string")
                .withMessageContaining("blank");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Require.notBlank(" "))
                .withMessageContaining("string")
                .withMessageContaining("blank");

        assertThat(Require.notBlank("any")).isEqualTo("any");
    }

    @Test
    public void notBlankTitled() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Require.notBlank(null, titled("input")))
                .withMessageContaining("input")
                .withMessageContaining("blank");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Require.notBlank(" ", titled("input")))
                .withMessageContaining("input")
                .withMessageContaining("blank");

        assertThat(Require.notBlank("any", titled("input"))).isEqualTo("any");
    }

}
