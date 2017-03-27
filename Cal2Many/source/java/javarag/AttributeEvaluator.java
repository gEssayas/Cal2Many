package javarag;

/**
 * Evaluates attributes.
 * 
 * @author Gustav Cedersjo
 */
public interface AttributeEvaluator {
	/**
	 * Evaluates the attribute {@code name} on {@code node} with arguments
	 * {@code args}.
	 * 
	 * @param name the name of the attribute
	 * @param node the node to evaluate the attribute on
	 * @param args the arguments of the attribute
	 * @return the value of the attribute
	 */
	public <T> T evaluate(String name, Object node, Object... args);

	/**
	 * Evaluates the attribute {@code name} on {@code node} with arguments
	 * {@code args} and converts the result to {@code T}.
	 * 
	 * @param <T> the type of the attribute
	 * @param type a class object of the return type
	 * @param name the name of the attribute
	 * @param node the node to evaluate the attribute on
	 * @param args the arguments of the attribute
	 * @return the value of the attribute
	 */
	public <T> T evaluate(Class<T> type, String name, Object node, Object... args);

}
