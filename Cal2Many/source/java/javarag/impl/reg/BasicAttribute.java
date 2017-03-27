package javarag.impl.reg;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class BasicAttribute extends Attribute {
	protected final Map<Class<?>, Method> definitions;

	protected BasicAttribute(String name) {
		super(name);
		this.definitions = new HashMap<>();
	}

	@Override
	public void addDefinition(Method m) {
		Class<?> nodeType = m.getParameterTypes()[0];
		definitions.put(nodeType, m);
	}
	
}
