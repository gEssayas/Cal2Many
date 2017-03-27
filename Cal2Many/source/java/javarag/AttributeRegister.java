package javarag;

/**
 * Register for attribute modules.
 * 
 * The typical use of the attribute register is to register all
 * {@link SimpleModule attribute modules} once and instantiate an
 * {@link AttributeEvaluator evaluator} for each tree that uses the attributes.
 * 
 * @author Gustav Cedersjo
 * 
 */
public interface AttributeRegister {
	/**
	 * Adds attribute modules to the register. The attribute modules must be
	 * {@link java.lang.Class} objects of {@link SimpleModule}s. The modules
	 * are checked for semantic errors when added to the register.
	 * 
	 * @param attributeModules modules to be registered
	 */
	public void register(Class<?>... attributeModules);

	/**
	 * Returns an {@link AttributeEvaluator} for {@code tree}, as it is
	 * traversed by {@code traverser}.
	 * 
	 * @param <T> the type of the tree nodes
	 * @param tree root of the tree
	 * @param traverser to get the children of a tree node
	 * @return an attribute evaluator
	 */
	public <T> AttributeEvaluator getEvaluator(T tree, TreeTraverser<T> traverser);
}
