package eu.goodlike.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.function.Predicate;

public final class Require {

    interface Title {
        String getTitle();
    }

    public static Title titled(String title) {
        Require.notBlank(title, () -> "title");
        return () -> title;
    }

    public static <T> T notNull(T t) {
        return notNull(t, () -> "object");
    }

    public static <T> T notNull(T t, Title title) {
        return failIf(Objects::isNull, t, title, "cannot be null");
    }

    public static String notBlank(String s) {
        return notBlank(s, () -> "string");
    }

    public static String notBlank(String s, Title title) {
        return failIf(StringUtils::isBlank, s, title, "cannot be blank");
    }

    private Require() {
        throw new AssertionError("Do not instantiate! Use static fields/methods!");
    }

    private static <T> T failIf(Predicate<T> conditionToFail, T t, Title title, String errorDescription) {
        assertTitleNotNull(title);
        if (conditionToFail.test(t)) {
            throw new IllegalArgumentException("Invalid " + title.getTitle() + ": " + errorDescription + ".");
        }
        return t;
    }

    private static void assertTitleNotNull(Title title) {
        if (title == null) {
            throw new IllegalArgumentException("Invalid title object: cannot be null.");
        }
    }

}
