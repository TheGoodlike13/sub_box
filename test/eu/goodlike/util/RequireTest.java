package eu.goodlike.util;

import org.junit.jupiter.api.Test;

import static eu.goodlike.asserts.Asserts.assertInvalidBlank;
import static eu.goodlike.asserts.Asserts.assertInvalidNull;
import static eu.goodlike.util.Require.titled;
import static org.assertj.core.api.Assertions.assertThat;

public class RequireTest {

    @Test
    public void blankTitle() {
        assertInvalidBlank("title", Require::titled);
    }

    @Test
    public void title() {
        assertThat(titled("input")).isNotNull()
                .extracting(Require.Title::getTitle)
                .isEqualTo("input");
    }

    @Test
    public void requireFailsWithoutTitle() {
        assertInvalidNull("object", Require::notNull);
        assertInvalidBlank("string", Require::notBlank);
    }

    @Test
    public void requireFailsWithTitle() {
        assertInvalidNull("input", input -> Require.notNull(input, titled("input")));
        assertInvalidBlank("input", input -> Require.notBlank(input, titled("input")));
    }

    @Test
    public void nullTitles() {
        assertInvalidNull("title object", (Require.Title input) -> Require.notNull("any", input));
        assertInvalidNull("title object", (Require.Title input) -> Require.notBlank("any", input));
    }

    @Test
    public void requireSucceedsWithoutTitle() {
        assertThat(Require.notNull("any")).isEqualTo("any");
        assertThat(Require.notBlank("any")).isEqualTo("any");
    }

    @Test
    public void requireSucceedsWithTitle() {
        assertThat(Require.notNull("any", titled("input"))).isEqualTo("any");
        assertThat(Require.notBlank("any", titled("input"))).isEqualTo("any");
    }

}
