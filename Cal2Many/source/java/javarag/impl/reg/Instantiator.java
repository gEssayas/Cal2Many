package javarag.impl.reg;

import java.util.HashMap;
import java.util.Map;

public class Instantiator {
	private final Map<Class<?>, Object> instances;

	public Instantiator() {
		instances = new HashMap<>();
	}

	public Object getInstance(Class<?> type) {
		if (instances.containsKey(type)) {
			return instances.get(type);
		} else {
			try {
				Object instance = type.newInstance();
				instances.put(type, instance);
				return instance;
			} catch (InstantiationException | IllegalAccessException e) {
				return null;
			}
		}
	}

	public Map<Class<?>, Object> getInstances() {
		return instances;
	}

}
