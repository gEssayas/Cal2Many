package javarag.impl.inst;

public class CircularityException extends RuntimeException {
	private final Request request;
	public CircularityException(Request request) {
		super("Circularity detected when trying to evaluate " + request);
		this.request = request;
	}
	
	public Request getRequest() {
		return request;
	}
}
