package javarag.coll;

import java.util.BitSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class Builders {
	public static Builder<BitSet, Integer> bitSetBuilder() {
		return new BitSetBuilder();
	}

	public static <T> Builder<Set<T>, T> setBuilder() {
		return new CollectionBuilder<Set<T>, T>(new LinkedHashSet<T>());
	}

	public static <T> Builder<SortedSet<T>, T> sortedSetBuilder() {
		return new CollectionBuilder<SortedSet<T>, T>(new TreeSet<T>());
	}

	public static <K, V> Builder<Map<K, V>, Map.Entry<K, V>> mapBuilder() {
		return new MapBuilder<Map<K, V>, K, V>(new LinkedHashMap<K, V>());
	}

	public static <K, V> Builder<Map<K, V>, Map.Entry<K, V>> identityMapBuilder() {
		return new MapBuilder<Map<K, V>, K, V>(new IdentityHashMap<K, V>());
	}

	public static <K, V> Builder<SortedMap<K, V>, Map.Entry<K, V>> sortedMapBuilder() {
		return new MapBuilder<SortedMap<K, V>, K, V>(new TreeMap<K, V>());
	}

}
