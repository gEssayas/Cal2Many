package javarag.impl;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javarag.TreeTraverser;

/**
 * A tree structure describes the tree on which the attributes are defined.
 * The tree structure may expand by adding new scopes. Collected attributes
 * are limited by these scope boundaries by only allowing a node to contribute
 * values to attributes of nodes in the same tree scope.
 */
public class TreeStructure {
	private final IdentityHashMap<Object, TreeScope> treeScopes;
	private final IdentityHashMap<Object, Object> parents;
	private final TreeTraverser traverser;
	private final Object root;
	
	public TreeStructure(Object root, TreeTraverser traverser) {
		this.treeScopes = new IdentityHashMap<>();
		this.parents = new IdentityHashMap<>();
		this.traverser = traverser;
		this.root = root;
		addTreeScope(root, null);
	}
	
	/** Returns the root of the original tree */
	public Object getRoot() {
		return root;
	}
	
	/** Returns the parent of node or null if node is the root. Throws IllegalArgumentException if node is not part of the tree. */
	public Object getParent(Object node) {
		if (parents.containsKey(node)) {
			return parents.get(node);
		} else {
			throw new IllegalArgumentException("The node does not belong to the tree.");
		}
	}
	
	/** Returns the tree scope that node belongs to or null if the node is not part of the tree. */
	public TreeScope getTreeScopeOf(Object node) {
		return treeScopes.get(node);
	}
	
	/** Adds a new tree scope starting in root where the parent of the root is parent. */
	public void addTreeScope(Object root, Object parent) {
		TreeScope treeScope = new TreeScope();
		Map<Object, Object> parentMap = new IdentityHashMap<>();
		Queue<Object> queue = new ArrayDeque<>();

		addParentLink(parent, root, parentMap);
		queue.add(root);
		while (!queue.isEmpty()) {
			Object node = queue.remove();
			treeScope.addNode(node);
			if (treeScopes.containsKey(node)) {
				throw new Error("The node already belongs to the tree.");
			}
			for (Object child : traverser.getChildren(node)) {
				addParentLink(node, child, parentMap);
				queue.add(child);
			}
		}
		treeScopes.putAll(treeScope.getScopeMap());
		parents.putAll(parentMap);
	}
	
	private void addParentLink(Object parent, Object child, Map<Object, Object> parentMap) {
		if (parents.containsKey(child)) {
			throw new Error("The node already belongs to the tree.");
		}
		parentMap.put(child, parent);
	}
	
	/**
	 * A tree socpe is a connected part of the whole tree structure.
	 */
	public static class TreeScope {
		private final List<Object> nodes = new ArrayList<>();
		private final List<Object> unmodifiableView = Collections.unmodifiableList(nodes);
		private final Map<Object, TreeScope> scopeMap = new IdentityHashMap<>();
		public List<Object> getNodes() {
			return unmodifiableView;
		}
		private void addNode(Object o) {
			nodes.add(o);
			scopeMap.put(o, this);
		}
		private Map<Object, TreeScope> getScopeMap() {
			return scopeMap;
		}
	}
}
