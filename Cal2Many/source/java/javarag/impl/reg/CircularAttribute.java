package javarag.impl.reg;

import java.lang.reflect.Method;

import javarag.impl.TreeStructure;
import javarag.impl.inst.AttributeHandler;
import javarag.impl.inst.CircularAttributeHandler;

public class CircularAttribute extends Attribute {
	private BasicAttribute start;
	private BasicAttribute step;

	public CircularAttribute(BasicAttribute start, BasicAttribute step) {
		super(start == null ? step.getName() : start.getName());
		if (start != null && step != null && start.getClass() != step.getClass()) {
			throw new IllegalArgumentException();
		}
		this.start = start == null ? createOfSameKind(step) : start;
		this.step = step == null ? createOfSameKind(start) : step;
	}

	private BasicAttribute createOfSameKind(BasicAttribute attr) {
		if (attr instanceof InheritedAttribute) {
			return new InheritedAttribute(attr.getName());
		} else {
			return new SynthesizedAttribute(attr.getName());
		}
	}

	@Override
	public void addDefinition(Method def) {
		step.addDefinition(def);
	}

	@Override
	public AttributeHandler createHandler(TreeStructure tree, Instantiator instantiator, boolean circularityCheck) {
		AttributeHandler startHandler = start.createHandler(tree, instantiator, false);
		AttributeHandler stepHandler = step.createHandler(tree, instantiator, false);
		return new CircularAttributeHandler(startHandler, stepHandler);
	}

	public void addBottom(Method def) {
		start.addDefinition(def);
	}

}
