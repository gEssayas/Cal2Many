package javarag.impl.inst;

public class CircularityCheck implements AttributeHandler {

	private final AttributeHandler handler;

	public CircularityCheck(AttributeHandler handler) {
		this.handler = handler;
	}

	@Override
	public String getName() {
		return handler.getName();
	}

	@Override
	public Object evaluate(Context context, Request request) {
		CircularityContext circularityContext = context.getCircularityContext();
		if (!circularityContext.addRequest(request)) {
			throw new CircularityException(request);
		}
		try {
			return handler.evaluate(context, request);
		} finally {
			circularityContext.remove(request);
		}
	}

}
