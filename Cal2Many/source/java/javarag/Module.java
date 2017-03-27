package javarag;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;

public class Module<T> {
	private final T proxy;
	private AttributeEvaluator evaluator;

	@SuppressWarnings("unchecked")
	public Module() {
		Class<?> klass = this.getClass();
		Class<?>[] impls = extractModuleImpls(klass);
		proxy = (T) Proxy.newProxyInstance(klass.getClassLoader(), impls, new AttributeInvoker());
	}

	private Class<?>[] extractModuleImpls(Class<?> klass) {
		Type module = Types.findSuperclass(klass, Module.class);
		Type[] params = Types.getTypeParameters(module);
		return Types.getTypeAsInterfaces(params[0]);
	}

	private class AttributeInvoker implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return evaluator.evaluate(method.getName(), args[0], Arrays.copyOfRange(args, 1, args.length));
		}
	}
	
	/**
	 * Returns an attribute evaluator with the interface specified by T.
	 * 
	 * This method is equivalent to evaluator(), but with a shorter name.
	 * 
	 * @return an attribute evaluator
	 */
	protected T e() {
		return proxy;
	}
	
	/**
	 * Returns an attribute evaluator with the interface specified by T.
	 * 
	 * @return an attribute evaluator
	 */
	protected T evaluator() {
		return proxy;
	}

	/**
	 * Called on the instantiation of {@link AttributeEvaluator}s.
	 * 
	 * @param evaluator
	 */
	public final void setEvaluator(AttributeEvaluator evaluator) {
		if (this.evaluator != null && this.evaluator != evaluator) {
			throw new Error("CompilerError: Can not change AttributeSystem.");
		}
		this.evaluator = evaluator;
	}
}
