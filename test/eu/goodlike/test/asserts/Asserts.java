package eu.goodlike.test.asserts;

import org.junit.jupiter.api.function.ThrowingConsumer;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public final class Asserts {

    public static <T> void assertInvalidNull(String inputName, ThrowingConsumer<T> consumerForNull) {
        assertInvalid("null", null, inputName, consumerForNull);
    }

    public static void assertInvalidBlank(String inputName, ThrowingConsumer<String> consumerForBlankStrings) {
        for (String blankString : BLANK_STRINGS) {
            assertInvalid("blank", blankString, inputName, consumerForBlankStrings);
        }
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
