package javarag.impl.reg;

import java.lang.reflect.Method;

import javarag.impl.TreeStructure;
import javarag.impl.inst.AttributeHandler;
import javarag.impl.inst.CachedAttributeHandler;
import javarag.impl.inst.NonTerminalAttributeHandler;

public class NonTerminalAttribute extends Attribute {

	private BasicAttribute attribute;

	public NonTerminalAttribute(BasicAttribute attribute) {
		super(attribute.getName());
		this.attribute = attribute;
	}

	@Override
	public void addDefinition(Method def) {
		attribute.addDefinition(def);
	}

	@Override
	public AttributeHandler createHandler(TreeStructure tree, Instantiator instantiator, boolean circularityCheck) {
		return new CachedAttributeHandler(new NonTerminalAttributeHandler(attribute.createHandler(tree, instantiator, circularityCheck), tree));
	}


}
