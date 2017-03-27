package javarag.impl.inst;

import javarag.impl.TreeStructure;

public class NonTerminalAttributeHandler implements AttributeHandler {
	private final TreeStructure treeStructure;
	private final AttributeHandler attribute;
	
	public NonTerminalAttributeHandler(AttributeHandler attribute, TreeStructure treeStructure) {
		this.treeStructure = treeStructure;
		this.attribute = attribute;
	}

	@Override
	public String getName() {
		return attribute.getName();
	}

	@Override
	public Object evaluate(Context context, Request request) {
		Object tree = attribute.evaluate(context, request);
		treeStructure.addTreeScope(tree, request.getNode());
		return tree;
	}

}
