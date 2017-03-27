package javarag.impl.inst;

import java.util.HashMap;
import java.util.Map;

public class CircularAttributeHandler implements AttributeHandler {
	private final AttributeHandler bottom;
	private final AttributeHandler attribute;
	private final Map<Request, Object> cache;

	public CircularAttributeHandler(AttributeHandler bottom, AttributeHandler attribute) {
		this.bottom = bottom;
		this.attribute = attribute;
		this.cache = new HashMap<>();
	}

	@Override
	public String getName() {
		return attribute.getName();
	}

	@Override
	public Object evaluate(Context context, Request request) {
		if (cache.containsKey(request)) {
			return cache.get(request);
		}
		FixPointContext fixPoingContext = context.getFixPointContext();
		if (!fixPoingContext.containsValue(request)) {
			fixPoingContext.putValue(request, bottom.evaluate(context, request));
		}
		if (!fixPoingContext.isInCycle()) {
			fixPoingContext.enterCycle();
			Object result;
			do {
				fixPoingContext.visit(request);
				fixPoingContext.setNotChanged();
				result = attribute.evaluate(context, request);
				fixPoingContext.putValue(request, result);
				fixPoingContext.incrementIteration();
			} while (fixPoingContext.isChanged());
			cache.put(request, result);
			cache.putAll(fixPoingContext.clearIntermediateValues());
			fixPoingContext.exitCycle();
			return result;
		} else if (!fixPoingContext.isVisited(request)) {
			fixPoingContext.visit(request);
			Object result = attribute.evaluate(context, request);
			fixPoingContext.putValue(request, result);
			context.getCachingContext().setIsIntermediate(true);
			return result;
		} else {
			context.getCachingContext().setIsIntermediate(true);
			return fixPoingContext.getValue(request);
		}
	}

}
