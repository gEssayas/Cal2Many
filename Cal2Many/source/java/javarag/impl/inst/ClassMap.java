package javarag.impl.inst;

import java.util.IdentityHashMap;
import java.util.List;

public class ClassMap<T> {
	private final IdentityHashMap<Class<?>, T> map;
	private final Interfaces is;

	private ClassMap(IdentityHashMap<Class<?>, T> map) {
		this.map = map;
		this.is = new Interfaces();
	}

	public T get(Class<?> key) {
		if (key.isInterface() || key.isPrimitive()) {
			throw new IllegalArgumentException(key.getSimpleName() + " is not a class.");
		}
		T value = getByClass(key);
		if (value != null) {
			return value;
		} else {
			return getByInterface(key);
		}
	}

	private T getByInterface(Class<?> key) {
		List<Class<?>> interfaces = is.getTopSortedInterfaces(key);
		Class<?> candidate = null;
		for (Class<?> i : interfaces) {
			if (map.containsKey(i)) {
				if (candidate == null) {
					candidate = i;
				} else if (!i.isAssignableFrom(candidate)) {
					throw new AmbiguousDefinitionException("Ambiguous definition for class " + key.getSimpleName() + ".");
				}
			}
		}
		T result = candidate != null ? map.get(candidate) : null;
		map.put(key, result);
		return result;
	}

	private T getByClass(Class<?> key) {
		if (key == null) {
			return null;
		}
		if (map.containsKey(key)) {
			return map.get(key);
		}
		T result = getByClass(key.getSuperclass());
		map.put(key, result);
		return result;
	}

	public static class Builder<T> {
		private IdentityHashMap<Class<?>, T> map;

		public Builder() {
			map = new IdentityHashMap<>();
		}

		public void put(Class<?> key, T value) {
			if (map == null) {
				throw new IllegalStateException("ClassMap is already built.");
			}
			if (key.isPrimitive()) {
				throw new IllegalArgumentException(key.getSimpleName() + " is not a class or interface.");
			}
			map.put(key, value);
		}

		public ClassMap<T> build() {
			ClassMap<T> result = new ClassMap<>(map);
			map = null;
			return result;
		}
	}
}
