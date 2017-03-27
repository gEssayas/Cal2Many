package javarag.impl.inst;

import javarag.MissingDefinitionException;
import javarag.impl.TreeStructure;

public class InheritedAttributeHandler implements AttributeHandler {
	private final String name;
	private final TreeStructure tree;
	private final ClassMap<MethodInvoker> methods;

	public InheritedAttributeHandler(String name, ClassMap<MethodInvoker> methods, TreeStructure tree) {
		this.name = name;
		this.methods = methods;
		this.tree = tree;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object evaluate(Context context, Request request) {
		Object node = tree.getParent(request.getNode());
		while (node != null) {
			MethodInvoker invoker = methods.get(node.getClass());
			if (invoker != null) {
				return invoker.invoke(Arrays.prependArray(node, request.getArguments()));
			}
			node = tree.getParent(node);
		}
		throw new MissingDefinitionException(request);
	}

}
