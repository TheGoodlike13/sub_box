package eu.goodlike.asserts;

import eu.goodlike.util.ThrowingConsumer;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public final class Asserts {

    public static void assertInvalidNull(String inputName, ThrowingConsumer<?> nullAverseConsumer) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> nullAverseConsumer.accept(null))
                .withMessageContaining(inputName)
                .withMessageContaining("null");
    }

    public static void assertInvalidBlank(String inputName, ThrowingConsumer<String> blankAverseConsumer) {
        for (String blankString : BLANK_STRINGS) {
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> blankAverseConsumer.accept(blankString))
                    .withMessageContaining(inputName)
                    .withMessageContaining("blank");
        }
    }

    private Asserts() {
        throw new AssertionError("Do not instantiate! Use static fields/methods!");
    }

    private static final List<String> BLANK_STRINGS = Arrays.asList(null, "", " ");

}
