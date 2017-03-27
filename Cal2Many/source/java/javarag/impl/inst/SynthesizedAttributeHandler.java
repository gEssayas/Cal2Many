package javarag.impl.inst;

import javarag.MissingDefinitionException;

public class SynthesizedAttributeHandler implements AttributeHandler {
	private final String name;
	private final ClassMap<MethodInvoker> methods;

	public SynthesizedAttributeHandler(String name, ClassMap<MethodInvoker> methods) {
		this.name = name;
		this.methods = methods;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object evaluate(Context context, Request request) {
		MethodInvoker invoker = methods.get(request.getNode().getClass());
		if (invoker == null) {
			throw new MissingDefinitionException(request);
		}
		Object[] args = Arrays.prependArray(request.getNode(), request.getArguments());
		return invoker.invoke(args);
	}

}
