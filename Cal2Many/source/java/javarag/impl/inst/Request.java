package javarag.impl.inst;

import java.util.Arrays;

public class Request {
	private final String attribute;
	private final Object node;
	private final Object[] arguments;

	public Request(String attribute, Object node, Object[] arguments) {
		this.attribute = attribute;
		this.node = node;
		this.arguments = arguments;
	}

	public String getAttribute() {
		return attribute;
	}

	public Object getNode() {
		return node;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public int calculateHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(arguments);
		result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
		result = prime * result + ((node == null) ? 0 : System.identityHashCode(node));
		return result;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(arguments);
		result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
		result = prime * result + ((node == null) ? 0 : System.identityHashCode(node));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Request)) {
			return false;
		}
		Request other = (Request) obj;
		if (!Arrays.equals(arguments, other.arguments)) {
			return false;
		}
		if (attribute == null) {
			if (other.attribute != null) {
				return false;
			}
		} else if (!attribute.equals(other.attribute)) {
			return false;
		}
		if (node != other.node) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Request [attribute=" + attribute + ", node=" + node 
				+ ", node class=" + node.getClass().getSimpleName() 
				+ ", arguments=" + Arrays.toString(arguments) + "]";
	}

}
