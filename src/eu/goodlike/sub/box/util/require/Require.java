package eu.goodlike.sub.box.util.require;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public final class Require {

  public static ParameterTitle titled(String title) {
    return () -> title;
  }

  public static void notNull(ParameterTitle parameterTitle) {
    String title = parameterTitle == null ? null : parameterTitle.get();
    throw new IllegalStateException("Invalid use of API! Require::notNull call with only title: " + title);
  }

  public static <T> T notNull(T t, ParameterTitle title) {
    return failIf(Objects::isNull, t, title, "null");
  }

  public static String notBlank(String s, ParameterTitle title) {
    return failIf(StringUtils::isBlank, s, title, "blank");
  }

  public static int positive(int integer, ParameterTitle title) {
    return failIf(i -> i <= 0, integer, title, "negative or zero");
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

  private static <T> T failIf(Predicate<T> conditionToFail, T t, ParameterTitle title, String badArgumentDescription) {
    if (conditionToFail.test(t))
      throw newError(title, badArgumentDescription);

    return t;
  }

  private static int failIf(IntPredicate conditionToFail, int integer, ParameterTitle title, String badArgumentDescription) {
    if (conditionToFail.test(integer))
      throw newError(title, badArgumentDescription);

    return integer;
  }

  private static IllegalArgumentException newError(ParameterTitle title, String badArgumentDescription) {
    return new IllegalArgumentException("Invalid " + title.get() + ":  cannot be " + badArgumentDescription + ".");
  }

}
