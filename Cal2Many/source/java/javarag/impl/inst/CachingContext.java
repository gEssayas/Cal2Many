package javarag.impl.inst;

public class CachingContext {
	private boolean intermediateValue;
	
	public CachingContext() {
		intermediateValue = false;
	}
	
	public boolean setIsIntermediate(boolean value) {
		boolean iv = intermediateValue;
		intermediateValue = value;
		return iv;
	}
	
	public boolean isIntermediate() {
		return intermediateValue;
	}

}
