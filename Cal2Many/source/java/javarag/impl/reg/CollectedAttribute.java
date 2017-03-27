package javarag.impl.reg;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javarag.impl.TreeStructure;
import javarag.impl.inst.AttributeHandler;
import javarag.impl.inst.ClassMap;
import javarag.impl.inst.ClassMultiMap;
import javarag.impl.inst.CollectedAttributeHandler;
import javarag.impl.inst.MethodInvoker;

public class CollectedAttribute extends Attribute {
	private final Map<Class<?>, Method> collections;
	private final Map<Class<?>, List<Method>> contributions;

	public CollectedAttribute(String name) {
		super(name);
		collections = new HashMap<>();
		contributions = new HashMap<>();
	}

	@Override
	public void addDefinition(Method def) {
		collections.put(getNodeType(def), def);
	}

	public void addContribution(Method def) {
		getContributionList(getNodeType(def)).add(def);
	}

	private Class<?> getNodeType(Method def) {
		return def.getParameterTypes()[0];
	}

	private List<Method> getContributionList(Class<?> nodeType) {
		List<Method> result = contributions.get(nodeType);
		if (result == null) {
			result = new ArrayList<>();
			contributions.put(nodeType, result);
		}
		return result;
	}

	@Override
	public AttributeHandler createHandler(TreeStructure tree, Instantiator instantiator, boolean circularityCheck) {
		ClassMap<MethodInvoker> coll = MethodInvokerClassMaps.create(collections, instantiator);
		ClassMultiMap<MethodInvoker> cont = MethodInvokerClassMaps.createMulti(contributions, instantiator);
		return new CollectedAttributeHandler(getName(), coll, cont, tree);
	}

}
