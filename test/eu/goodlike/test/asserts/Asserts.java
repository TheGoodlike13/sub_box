package eu.goodlike.test.asserts;

import eu.goodlike.sub.box.util.Require;
import org.junit.jupiter.api.function.ThrowingConsumer;

import java.util.Arrays;
import java.util.List;

import static eu.goodlike.sub.box.util.Require.titled;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Reusable assertions
 */
public final class Asserts {

  /**
   * @throws AssertionError if consumerForNull does NOT throw an IllegalArgumentException with inputName and 'null'
   * when accepting null value
   */
  public static <T> void assertInvalidNull(String inputName, ThrowingConsumer<T> consumerForNull) {
    assertValidAssertion(inputName, consumerForNull);
    assertInvalid("null", null, inputName, consumerForNull);
  }

  /**
   * @throws AssertionError if consumerForBlankStrings does NOT throw an IllegalArgumentException with inputName and
   * 'blank' when accepting any null or blank value
   */
  public static void assertInvalidBlank(String inputName, ThrowingConsumer<String> consumerForBlankStrings) {
    assertValidAssertion(inputName, consumerForBlankStrings);
    for (String blankString : BLANK_STRINGS) {
      assertInvalid("blank", blankString, inputName, consumerForBlankStrings);
    }
  }

  /**
   * @throws AssertionError if consumerForInts does NOT throw an IllegalArgumentException with inputName and 'negative'
   * or 'zero' when accepting any negative integer or zero respectively
   */
  public static void assertInvalidNegativeOrZero(String inputName, ThrowingConsumer<Integer> consumerForInts) {
    assertValidAssertion(inputName, consumerForInts);
    assertInvalid("negative", -1, inputName, consumerForInts);
    assertInvalid("zero", 0, inputName, consumerForInts);
  }

  private Asserts() {
    throw new AssertionError("Do not instantiate! Use static fields/methods!");
  }

  private static final List<String> BLANK_STRINGS = Arrays.asList(null, "", " ");

  private static void assertValidAssertion(String inputName, ThrowingConsumer<?> consumer) {
    Require.notBlank(inputName, titled("inputName"));
    Require.notNull(consumer, titled("consumer"));
  }

  private static <T> void assertInvalid(String invalidity, T invalidInput, String inputName, ThrowingConsumer<T> consumerForInvalidInputs) {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> consumerForInvalidInputs.accept(invalidInput))
        .withMessageContaining(inputName)
        .withMessageContaining(invalidity);
  }

}
