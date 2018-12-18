package eu.goodlike.sub.box.util;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collector;

import static com.google.common.collect.ImmutableListMultimap.flatteningToImmutableListMultimap;

public final class MultimapUtils {

    public static <K, V> ListMultimap<K, V> toListMultimap(Map<K, List<V>> map) {
        return map == null
                ? ImmutableListMultimap.of()
                : map.entrySet().stream().collect(flatteningToImmutableListFromEntries());
    }

    private MultimapUtils() {
        throw new AssertionError("Do not instantiate! Use static fields/methods!");
    }

    private static <K, V> Collector<Map.Entry<K, List<V>>, ?, ImmutableListMultimap<K, V>> flatteningToImmutableListFromEntries() {
        return flatteningToImmutableListMultimap(Map.Entry::getKey, e -> e.getValue().stream());
    }

}
