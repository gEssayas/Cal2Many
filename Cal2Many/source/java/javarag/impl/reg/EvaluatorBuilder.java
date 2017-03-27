package javarag.impl.reg;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javarag.AttributeEvaluator;
import javarag.Module;
import javarag.impl.TreeStructure;
import javarag.impl.inst.AttributeHandler;
import javarag.impl.inst.BasicAttributeEvaluator;

public class EvaluatorBuilder {
	private final Map<String, Attribute> attributes;

	public EvaluatorBuilder() {
		attributes = new HashMap<>();
	}

	public void createSynthesized(String name) {
		assertNotCreated(name);
		attributes.put(name, new SynthesizedAttribute(name));
	}

	public void createProcedural(String name) {
		assertNotCreated(name);
		attributes.put(name, new ProceduralAttribute(name));
	}

	public void createInherited(String name) {
		assertNotCreated(name);
		attributes.put(name, new InheritedAttribute(name));
	}

	public void createCollected(String name) {
		assertNotCreated(name);
		attributes.put(name, new CollectedAttribute(name));
	}

	public void setCircular(String name) {
		Attribute attr = attributes.get(name);
		if (attr instanceof InheritedAttribute || attr instanceof SynthesizedAttribute) {
			BasicAttribute step = (BasicAttribute) attr;
			Attribute circ = new CircularAttribute(null, step);
			attributes.put(name, circ);
		} else {
			throw new IllegalStateException("Could not set attribute " + name + "to circular");
		}
	}

	public void setCached(String name) {
		Attribute attr = attributes.get(name);
		if (attr instanceof InheritedAttribute || attr instanceof SynthesizedAttribute) {
			CachedAttribute cached = new CachedAttribute((BasicAttribute) attr);
			attributes.put(name, cached);
		} else {
			throw new IllegalStateException("Could not enable cache for " + name);
		}
	}

	public void setNonTerminal(String name) {
		Attribute attr = attributes.get(name);
		if (attr instanceof InheritedAttribute || attr instanceof SynthesizedAttribute) {
			NonTerminalAttribute nta = new NonTerminalAttribute((BasicAttribute) attr);
			attributes.put(name, nta);
		} else {
			throw new IllegalStateException("Could make " + name + " a non terminal attribute");
		}
	}


	public void addDefinition(String name, Method definition) {
		Attribute attribute = attributes.get(name);
		if (attribute == null) {
			throw new IllegalStateException("Attribute " + name + " is not created.");
		} else {
			attribute.addDefinition(definition);
		}
	}

	public void addContribution(String name, Method contribution) {
		Attribute attribute = attributes.get(name);
		if (attribute instanceof CollectedAttribute) {
			((CollectedAttribute) attribute).addContribution(contribution);
		} else {
			throw new IllegalStateException(name + " is not a collected attribute.");
		}
	}

	public void addBottom(String name, Method bottom) {
		Attribute attribute = attributes.get(name);
		if (attribute instanceof CircularAttribute) {
			((CircularAttribute) attribute).addBottom(bottom);
		} else {
			throw new IllegalStateException(name + " is not a circular attribute.");
		}
	}

	public AttributeEvaluator build(TreeStructure tree) {
		BasicAttributeEvaluator evaluator = new BasicAttributeEvaluator();
		Instantiator instantiator = new Instantiator();
		for (Attribute a : attributes.values()) {
			AttributeHandler handler = a.createHandler(tree, instantiator, true);
			evaluator.addAttribute(handler);
		}
		for (Object o : instantiator.getInstances().values()) {
			((Module<?>) o).setEvaluator(evaluator);
		}
		return evaluator;
	}

	private void assertNotCreated(String name) {
		if (attributes.containsKey(name)) {
			throw new IllegalStateException("Attribute " + name + " is already created");
		}
	}

}
