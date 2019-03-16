package eu.goodlike.sub.box.util.require;

import org.junit.jupiter.api.Test;

import static eu.goodlike.sub.box.util.require.Require.titled;
import static eu.goodlike.test.asserts.Asserts.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class RequireTest {

  @Test
  public void title() {
    assertThat(titled("input")).isNotNull()
        .extracting(ParameterTitle::get)
        .isEqualTo("input");
  }

  @Test
  public void requireNotNull_noObject_onlyTitle() {
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> Require.notNull(titled("any")))
        .withMessageContaining("Require::notNull");
  }

  @Test
  public void requireFailsWithoutTitle() {
    assertNotNull("object", Require::notNull);
    assertNotBlank("string", Require::notBlank);
    assertPositive("integer", Require::positive);
  }

  @Test
  public void requireFailsWithTitle() {
    assertNotNull("input", input -> Require.notNull(input, titled("input")));
    assertNotBlank("input", input -> Require.notBlank(input, titled("input")));
    assertPositive("input", input -> Require.positive(input, titled("input")));
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

}
