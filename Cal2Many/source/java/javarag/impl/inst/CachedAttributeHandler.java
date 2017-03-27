package javarag.impl.inst;

import java.util.HashMap;
import java.util.Map;

public class CachedAttributeHandler implements AttributeHandler {
	private final AttributeHandler handler;
	private final Map<Request, Object> cache;

	public CachedAttributeHandler(AttributeHandler handler) {
		this.handler = handler;
		this.cache = new HashMap<>();
	}

	@Override
	public String getName() {
		return handler.getName();
	}

	@Override
	public Object evaluate(Context context, Request request) {
		if (cache.containsKey(request)) {
			return cache.get(request);
		} else {
			CachingContext cachingContext = context.getCachingContext();
			boolean temp = cachingContext.setIsIntermediate(false);
			Object result = handler.evaluate(context, request);
			if (!cachingContext.isIntermediate()) {
				cache.put(request, result);
			}
			cachingContext.setIsIntermediate(temp);
			return result;
		}
	}

}
