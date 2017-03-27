package javarag.impl.reg;

import javarag.impl.TreeStructure;
import javarag.impl.inst.AttributeHandler;
import javarag.impl.inst.CircularityCheck;
import javarag.impl.inst.ClassMap;
import javarag.impl.inst.MethodInvoker;
import javarag.impl.inst.SynthesizedAttributeHandler;

public class SynthesizedAttribute extends BasicAttribute {

	public SynthesizedAttribute(String name) {
		super(name);
	}

	@Override
	public AttributeHandler createHandler(TreeStructure tree, Instantiator instantiator, boolean circularityCheck) {
		ClassMap<MethodInvoker> methodInvokers = MethodInvokerClassMaps.create(definitions, instantiator);
		SynthesizedAttributeHandler handler = new SynthesizedAttributeHandler(getName(), methodInvokers);
		if (circularityCheck) {
			return new CircularityCheck(handler);
		} else {
			return handler;
		}
	}

}
