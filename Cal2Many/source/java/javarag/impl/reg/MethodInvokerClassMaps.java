package javarag.impl.reg;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javarag.impl.inst.ClassMap;
import javarag.impl.inst.ClassMultiMap;
import javarag.impl.inst.MethodInvoker;

public class MethodInvokerClassMaps {
	public static ClassMap<MethodInvoker> create(Map<Class<?>, Method> definitions, Instantiator instantiator) {
		ClassMap.Builder<MethodInvoker> invokers = new ClassMap.Builder<>();
		for (Entry<Class<?>, Method> entry : definitions.entrySet()) {
			invokers.put(entry.getKey(), createInvoker(instantiator, entry.getValue()));
		}
		return invokers.build();
	}

	public static ClassMultiMap<MethodInvoker> createMulti(Map<Class<?>, List<Method>> definitions,
			Instantiator instantiator) {
		ClassMultiMap.Builder<MethodInvoker> invokers = new ClassMultiMap.Builder<>();
		for (Entry<Class<?>, List<Method>> entry : definitions.entrySet()) {
			for (Method m : entry.getValue()) {
				invokers.put(entry.getKey(), createInvoker(instantiator, m));
			}
		}
		return invokers.build();
	}

	private static MethodInvoker createInvoker(Instantiator instantiator, Method method) {
		Class<?> moduleType = method.getDeclaringClass();
		Object instance = instantiator.getInstance(moduleType);
		return new MethodInvoker(instance, method);
	}
}
