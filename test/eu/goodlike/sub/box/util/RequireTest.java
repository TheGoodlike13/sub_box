package eu.goodlike.sub.box.util;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static eu.goodlike.sub.box.util.Require.titled;
import static eu.goodlike.test.asserts.Asserts.assertInvalidBlank;
import static eu.goodlike.test.asserts.Asserts.assertInvalidNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class RequireTest {

  @Test
  public void blankTitle() {
    assertInvalidBlank("title", title -> Require.titled(title).getTitle());
  }

  @Test
  public void title() {
    assertThat(titled("input")).isNotNull()
        .extracting(Require.Title::getTitle)
        .isEqualTo("input");
  }

  @Test
  public void requireNotNullWithObjectForgotten() {
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> Require.notNull(titled("any")))
        .withMessageContaining("Require::notNull");
  }

  @Test
  public void requireFailsWithoutTitle() {
    assertInvalidNull("object", Require::notNull);
    assertInvalidBlank("string", Require::notBlank);
    assertFails("integer", -1, Require::positive, "cannot be negative or zero");
    assertFails("integer", 0, Require::positive, "cannot be negative or zero");
  }

  @Test
  public void requireFailsWithTitle() {
    assertInvalidNull("input", input -> Require.notNull(input, titled("input")));
    assertInvalidBlank("input", input -> Require.notBlank(input, titled("input")));
    assertFails("input", -1, input -> Require.positive(input, titled("input")), "cannot be negative or zero");
    assertFails("input", 0, input -> Require.positive(input, titled("input")), "cannot be negative or zero");
  }

  @Test
  public void nullTitles() {
    assertInvalidNull("title object", (Require.Title input) -> Require.notNull("any", input));
    assertInvalidNull("title object", (Require.Title input) -> Require.notBlank("any", input));
    assertInvalidNull("title object", (Require.Title input) -> Require.positive(1, input));
  }

  @Test
  public void requireSucceedsWithoutTitle() {
    assertThat(Require.notNull("any")).isEqualTo("any");
    assertThat(Require.notBlank("any")).isEqualTo("any");
    assertThat(Require.positive(1)).isEqualTo(1);
  }

  @Test
  public void requireSucceedsWithTitle() {
    assertThat(Require.notNull("any", titled("input"))).isEqualTo("any");
    assertThat(Require.notBlank("any", titled("input"))).isEqualTo("any");
    assertThat(Require.positive(1, titled("input"))).isEqualTo(1);
  }

  private <T> void assertFails(String type, T t, Consumer<T> requireCall, String error) {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> requireCall.accept(t))
        .withMessage("Invalid " + type + ": " + error + ".");
  }

}
