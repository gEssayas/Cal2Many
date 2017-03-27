package javarag.impl.inst;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

public class ClassMultiMap<T> {
	private final Map<Class<?>, Set<T>> cache;
	private final Map<Class<?>, Set<T>> map;
	
	private ClassMultiMap(Map<Class<?>, Set<T>> map) {
		this.map = map;
		this.cache = map();
	}
	
	private static <E> Set<E> set() {
		return new HashSet<>();
	}
	
	private static <K, V> Map<K, V> map() {
		return new HashMap<>();
	}
	
	public Set<T> get(Class<?> key) {
		if (cache.containsKey(key)) {
			return cache.get(key);
		}
		Set<T> result = set();
		Class<?> clazz = key;
		while (clazz != null) {
			result.addAll(getFromMap(clazz));
			clazz = clazz.getSuperclass();
		}
		Queue<Class<?>> interfaces = new LinkedList<>();
		interfaces.addAll(asList(key.getInterfaces()));
		while (!interfaces.isEmpty()) {
			Class<?> iface = interfaces.remove();
			result.addAll(getFromMap(iface));
			interfaces.addAll(asList(iface.getInterfaces()));
		}
		result = unmodifiableSet(result);
		cache.put(key, result);
		return result;
	}
	
	private Set<T> getFromMap(Class<?> key) {
		Set<T> result = map.get(key);
		return result == null ? Collections.<T> emptySet() : result;
	}
	
	public static class Builder<T> {
		private Map<Class<?>, Set<T>> map;

		public Builder() {
			map = map();
		}

		public void put(Class<?> key, T value) {
			if (map == null) {
				throw new IllegalStateException("ClassMap is already built.");
			}
			Set<T> set = map.get(key);
			if (set == null) {
				set = set();
				map.put(key, set);
			}
			set.add(value);
		}

		public ClassMultiMap<T> build() {
			ClassMultiMap<T> result = new ClassMultiMap<>(map);
			map = null;
			return result;
		}
	}

}
