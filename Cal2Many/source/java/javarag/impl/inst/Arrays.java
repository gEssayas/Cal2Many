package javarag.impl.inst;

public class Arrays {
	public static Object[] prependArray(Object head, Object[] tail) {
		Object[] result = new Object[tail.length + 1];
		result[0] = head;
		System.arraycopy(tail, 0, result, 1, tail.length);
		return result;
	}

}
