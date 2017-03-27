package javarag.coll;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import java.util.Map.Entry;

public class MapBuilder<M extends Map<K, V>, K, V> implements Builder<M, Entry<K, V>> {
	private final M map;

	public MapBuilder(M map) {
		this.map = map;
	}

	@Override
	public void add(Entry<K, V> entry) {
		map.put(entry.getKey(), entry.getValue());
	}

	@Override
	public M build() {
		return map;
	}

	public static <K, V> Entry<K, V> entry(K key, V value) {
		return new SimpleImmutableEntry<>(key, value);
	}

}