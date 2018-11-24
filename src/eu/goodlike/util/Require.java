package eu.goodlike.util;

import org.apache.commons.lang3.StringUtils;

public final class Require {

    protected interface Title {
        String getTitle();
    }

    public static Title titled(String title) {
        Require.notBlank(title, () -> "title");
        return () -> title;
    }

    public static String notBlank(String s) {
        return notBlank(s, () -> "string");
    }

    public static String notBlank(String s, Title title) {
        if (StringUtils.isBlank(s)) {
            throw new IllegalArgumentException("Invalid " + title.getTitle() + ": cannot be blank.");
        }
        return s;
    }

    private Require() {
        throw new AssertionError("Do not instantiate! Use static fields/methods!");
    }

}
