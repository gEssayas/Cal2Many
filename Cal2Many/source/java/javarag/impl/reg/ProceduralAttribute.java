package javarag.impl.reg;

import javarag.impl.TreeStructure;
import javarag.impl.inst.AttributeHandler;
import javarag.impl.inst.ClassMap;
import javarag.impl.inst.MethodInvoker;
import javarag.impl.inst.ProceduralAttributeHandler;

public class ProceduralAttribute extends BasicAttribute {

	public ProceduralAttribute(String name) {
		super(name);
	}

	@Override
	public AttributeHandler createHandler(TreeStructure tree, Instantiator instantiator, boolean circularityCheck) {
		ClassMap<MethodInvoker> methodInvokers = MethodInvokerClassMaps.create(definitions, instantiator);
		return new ProceduralAttributeHandler(getName(), methodInvokers);
	}

}
