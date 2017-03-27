package javarag.impl.inst;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FixPointContext {
	private Map<Request, Object> values;
	private final Map<Request, Integer> visitedInIteration;
	private boolean inCycle;
	private boolean changed;
	private int currentIteration;

	public FixPointContext() {
		values = new HashMap<>();
		visitedInIteration = new HashMap<>();
		inCycle = false;
		changed = false;
	}

	public boolean isInCycle() {
		return inCycle;
	}

	public void enterCycle() {
		inCycle = true;
	}

	public void exitCycle() {
		inCycle = false;
	}

	public boolean isVisited(Request r) {
		if (visitedInIteration.containsKey(r)) {
			return visitedInIteration.get(r) == currentIteration;
		} else {
			return false;
		}
	}

	public void visit(Request r) {
		visitedInIteration.put(r, currentIteration);
	}

	public boolean containsValue(Request r) {
		return values.containsKey(r);
	}

	public Object getValue(Request r) {
		return values.get(r);
	}

	public void putValue(Request r, Object v) {
		Object old = values.put(r, v);
		if (!Objects.equals(v, old)) {
			changed = true;
		}
	}

	public void setNotChanged() {
		changed = false;
	}

	public boolean isChanged() {
		return changed;
	}

	public Map<? extends Request, ? extends Object> clearIntermediateValues() {
		Map<Request, Object> result = values;
		values = new HashMap<>();
		return result;
	}

	public void incrementIteration() {
		this.currentIteration = currentIteration + 1;
	}

}
