package eu.goodlike.test.asserts;

import org.junit.jupiter.api.function.ThrowingConsumer;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Reusable assertions
 */
public final class Asserts {

  /**
   * @throws AssertionError if consumerForNull does NOT throw an IllegalArgumentException with inputName and 'null'
   * when accepting null value
   */
  public static <T> void assertNotNull(String inputName, ThrowingConsumer<T> consumerForNull) {
    assertInvalid("null", null, inputName, consumerForNull);
  }

  /**
   * @throws AssertionError if consumerForBlankStrings does NOT throw an IllegalArgumentException with inputName and
   * 'blank' when accepting any null or blank value
   */
  public static void assertNotBlank(String inputName, ThrowingConsumer<String> consumerForBlankStrings) {
    for (String blankString : BLANK_STRINGS) {
      assertInvalid("blank", blankString, inputName, consumerForBlankStrings);
    }
  }

  /**
   * @throws AssertionError if consumerForInts does NOT throw an IllegalArgumentException with inputName and 'negative'
   * or 'zero' when accepting any negative integer or zero respectively
   */
  public static void assertPositive(String inputName, ThrowingConsumer<Integer> consumerForInts) {
    assertInvalid("negative", -1, inputName, consumerForInts);
    assertInvalid("zero", 0, inputName, consumerForInts);
  }

  private Asserts() {
    throw new AssertionError("Do not instantiate! Use static fields/methods!");
  }

  private static final List<String> BLANK_STRINGS = Arrays.asList(null, "", " ");

  private static <T> void assertInvalid(String invalidity, T invalidInput, String inputName, ThrowingConsumer<T> consumerForInvalidInputs) {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> consumerForInvalidInputs.accept(invalidInput))
        .withMessageContaining(inputName)
        .withMessageContaining(invalidity);
  }

}
