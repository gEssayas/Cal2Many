package javarag;

/**
 * An interface for traversing trees.
 * 
 * @author Gustav Cedersjo
 * 
 * @param <T> type of the tree nodes
 */
public interface TreeTraverser<T> {
	/**
	 * Returns the children of the tree or subtree.
	 * 
	 * @param root the root of the tree.
	 * @return the children
	 */
	public Iterable<? extends T> getChildren(T root);
}
