package javarag.impl.reg;

import java.lang.reflect.Method;

import javarag.impl.TreeStructure;
import javarag.impl.inst.AttributeHandler;

public abstract class Attribute {
	private final String name;

	protected Attribute(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public abstract void addDefinition(Method def);
	
	public abstract AttributeHandler createHandler(TreeStructure tree, Instantiator instantiator, boolean circularityCheck);

}
