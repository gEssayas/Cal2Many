package javarag.impl.inst;

public class Context {
	private final FixPointContext fixPointContext;
	private final CachingContext cachingContext;
	private final CircularityContext circularityContext;

	public Context() {
		fixPointContext = new FixPointContext();
		cachingContext = new CachingContext();
		circularityContext = new CircularityContext();
	}

	public FixPointContext getFixPointContext() {
		return fixPointContext;
	}

	public CachingContext getCachingContext() {
		return cachingContext;
	}
	
	public CircularityContext getCircularityContext() {
		return circularityContext;
	}

}
