package javarag.impl.inst;

import java.util.HashSet;
import java.util.Set;

public class CircularityContext {
	private final Set<Request> activeRequests = new HashSet<>();
	private long depth = 0;
	private static final long THRESHOLD = 100;
	
	public boolean addRequest(Request r) {
		depth++;
		if (depth > THRESHOLD) {
			return activeRequests.add(r);
		} else {
			return true;
		}
	}
	
	public boolean remove(Request r) {
		depth--;
		if (depth >= THRESHOLD) {
			return activeRequests.remove(r);
		} else {
			return true;
		}
	}
}
