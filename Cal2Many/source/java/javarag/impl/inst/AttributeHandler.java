package javarag.impl.inst;

public interface AttributeHandler {
	public String getName();

	public Object evaluate(Context context, Request request);
}
