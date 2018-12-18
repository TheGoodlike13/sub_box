package eu.goodlike.sub.box.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.data.MapEntry;
import org.junit.jupiter.api.Test;

import static org.assertj.guava.api.Assertions.assertThat;


public class MultimapUtilsTest {

  @Test
  public void nullMap() {
    assertThat(MultimapUtils.toListMultimap(null)).isEmpty();
  }

  @Test
  public void valuePair() {
    assertThat(MultimapUtils.toListMultimap(ImmutableMap.of("key", ImmutableList.of("value"))))
        .contains(MapEntry.entry("key", "value"));
  }

  @Test
  public void pairOfValues() {
    assertThat(MultimapUtils.toListMultimap(ImmutableMap.of("key1", ImmutableList.of("value1"), "key2", ImmutableList.of("value2"))))
        .contains(MapEntry.entry("key1", "value1"), MapEntry.entry("key2", "value2"));
  }

  @Test
  public void multipleValues() {
    assertThat(MultimapUtils.toListMultimap(ImmutableMap.of("key", ImmutableList.of("value1", "value2"))))
        .contains(MapEntry.entry("key", "value1"), MapEntry.entry("key", "value2"));
  }

  @Test
  public void aLittleBitOfEverything() {
    assertThat(MultimapUtils.toListMultimap(ImmutableMap.of("key1", ImmutableList.of("value1", "value2"), "key2", ImmutableList.of("value3"))))
        .contains(MapEntry.entry("key1", "value1"), MapEntry.entry("key1", "value2"), MapEntry.entry("key2", "value3"));
  }

}
