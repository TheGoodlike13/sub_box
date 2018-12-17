package eu.goodlike.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public final class Require {

    interface Title {
        String getTitle();
    }

    public static Title titled(String title) {
        return () -> Require.notBlank(title, () -> "title");
    }

    public static void notNull(Title t) {
        throw new IllegalStateException("Require::notNull call with only title!");
    }

    public static <T> T notNull(T t, Title title) {
        return failIf(Objects::isNull, t, title, "cannot be null");
    }

    public static int positive(int integer, Title title) {
        return failIf(i -> i <= 0, integer, title, "cannot be negative or zero");
    }

    public static String notBlank(String s, Title title) {
        return failIf(StringUtils::isBlank, s, title, "cannot be blank");
    }

    public static <T> T notNull(T t) {
        return notNull(t, () -> "object");
    }

    public static String notBlank(String s) {
        return notBlank(s, () -> "string");
    }

    public static int positive(int integer) {
        return positive(integer, () -> "integer");
    }

    private Require() {
        throw new AssertionError("Do not instantiate! Use static fields/methods!");
    }

    private static <T> T failIf(Predicate<T> conditionToFail, T t, Title title, String errorDescription) {
        assertNotNullTitle(title);
        if (conditionToFail.test(t))
            throw new IllegalArgumentException("Invalid " + title.getTitle() + ": " + errorDescription + ".");

        return t;
    }

    private static int failIf(IntPredicate conditionToFail, int integer, Title title, String errorDescription) {
        assertNotNullTitle(title);
        if (conditionToFail.test(integer))
            throw new IllegalArgumentException("Invalid " + title.getTitle() + ": " + errorDescription + ".");

        return integer;
    }

    private static Title assertNotNullTitle(Title title) {
        if (title == null)
            throw new IllegalArgumentException("Invalid title object: cannot be null.");

        return title;
    }

}
