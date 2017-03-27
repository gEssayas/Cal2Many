package javarag.impl.inst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Interfaces {
	private final Map<Class<?>, List<Class<?>>> cache;

	public Interfaces() {
		cache = new HashMap<>();
	}

	public List<Class<?>> getTopSortedInterfaces(Class<?> clazz) {
		if (cache.containsKey(clazz)) {
			return cache.get(clazz);
		} else {
			List<Class<?>> list = new ArrayList<>();
			visit(clazz, new HashSet<Class<?>>(), list);
			Collections.reverse(list);
			list = Collections.unmodifiableList(list);
			cache.put(clazz, list);
			return list;
		}
	}

	private void visit(Class<?> clazz, Set<Class<?>> visited, List<Class<?>> result) {
		if (clazz != null && !visited.contains(clazz)) {
			visited.add(clazz);
			visit(clazz.getSuperclass(), visited, result);
			for (Class<?> i : clazz.getInterfaces()) {
				visit(i, visited, result);
			}
			if (clazz.isInterface()) {
				result.add(clazz);
			}
		}
	}
}
