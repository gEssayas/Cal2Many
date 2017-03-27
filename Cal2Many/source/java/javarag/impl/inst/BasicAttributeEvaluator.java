package javarag.impl.inst;

import java.util.HashMap;
import java.util.Map;

import javarag.AttributeEvaluator;
import javarag.MissingDefinitionException;

public class BasicAttributeEvaluator implements AttributeEvaluator {
	private final Map<String, AttributeHandler> handlers;
	private final ThreadLocal<Context> threadLocalContext;

	public BasicAttributeEvaluator() {
		this.handlers = new HashMap<>();
		this.threadLocalContext = new ThreadLocal<Context>() {
			@Override
			public Context initialValue() {
				return new Context();
			}
		};
	}
	
	public void addAttribute(AttributeHandler handler) {
		handlers.put(handler.getName(), handler);
	}

	@Override
	public <T> T evaluate(String name, Object node, Object... args) {
		Request request = new Request(name, node, args);
		Context context = threadLocalContext.get();
		AttributeHandler handler = handlers.get(name);
		if (handler != null) {
			@SuppressWarnings("unchecked")
			T value = (T) handler.evaluate(context, request);
			return value;
		} else {
			throw new MissingDefinitionException(request);
		}
	}

	@Override
	public <T> T evaluate(Class<T> type, String name, Object node, Object... args) {
		return type.cast(evaluate(name, node, args));
	}

}
