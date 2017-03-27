package javarag.impl.reg;

import javarag.impl.TreeStructure;
import javarag.impl.inst.AttributeHandler;
import javarag.impl.inst.CircularityCheck;
import javarag.impl.inst.ClassMap;
import javarag.impl.inst.InheritedAttributeHandler;
import javarag.impl.inst.MethodInvoker;

public class InheritedAttribute extends BasicAttribute {

	public InheritedAttribute(String name) {
		super(name);
	}

	@Override
	public AttributeHandler createHandler(TreeStructure tree, Instantiator instantiator, boolean circularityCheck) {
		ClassMap<MethodInvoker> methodInvokers = MethodInvokerClassMaps.create(definitions, instantiator);
		InheritedAttributeHandler handler = new InheritedAttributeHandler(getName(), methodInvokers, tree);
		if (circularityCheck) {
			return new CircularityCheck(handler);
		} else {
			return handler;
		}
	}

}
