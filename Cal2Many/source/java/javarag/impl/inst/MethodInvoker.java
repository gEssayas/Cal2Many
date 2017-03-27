package javarag.impl.inst;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodInvoker {
	private final Object target;
	private final Method method;

	public MethodInvoker(Object target, Method method) {
		this.target = target;
		this.method = method;
	}

	public Object invoke(Object... args) throws IllegalArgumentException {
		try {
			return method.invoke(target, args);
		} catch (IllegalAccessException e) {
			// FIXME
			throw new Error(e);
		} catch (InvocationTargetException e) {
			Throwable c = e.getCause();
			if (c instanceof Error) {
				throw (Error) c;
			}
			if (c instanceof RuntimeException) {
				throw (RuntimeException) c;
			}
			throw new Error(c);
		}
	}
}
