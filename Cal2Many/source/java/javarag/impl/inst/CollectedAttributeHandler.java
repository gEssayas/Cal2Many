package javarag.impl.inst;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javarag.coll.Builder;
import javarag.impl.TreeStructure;
import javarag.impl.TreeStructure.TreeScope;

public class CollectedAttributeHandler implements AttributeHandler {
	private final String name;
	private final ClassMap<MethodInvoker> collections;
	private final ClassMultiMap<MethodInvoker> contributions;
	private final TreeStructure tree;
	private Map<Object, Object> cache;

	public CollectedAttributeHandler(String name, ClassMap<MethodInvoker> collections,
			ClassMultiMap<MethodInvoker> contributions,
			TreeStructure tree) {
		this.name = name;
		this.collections = collections;
		this.contributions = contributions;
		this.tree = tree;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object evaluate(Context context, Request request) {
		if (cache == null) {
			IdentityHashMap<Object, Object> cache = new IdentityHashMap<>();
			Collector<Object, Object> collector = new Collector<>();
			buildCollection(collector);
			for (Entry<Object, Builder<Object, Object>> entry : collector.builders.entrySet()) {
				cache.put(entry.getKey(), entry.getValue().build());
			}
			this.cache = cache;
		}
		Object node = request.getNode();
		if (cache.containsKey(node)) {
			return cache.get(node);
		} else {
			Object result = createBuilder(node).build();
			cache.put(node, result);
			return result;
		}
	}

	private void buildCollection(Collector<Object, Object> collector) {
		collector.collectFrom(tree.getRoot());
		while (!collector.treeScopeQueue.isEmpty()) {
			TreeScope nodes = collector.treeScopeQueue.remove();
			for (Object node : nodes.getNodes()) {
				Set<MethodInvoker> invokers = contributions.get(node.getClass());
				if (invokers != null) {
					for (MethodInvoker invoker : invokers) {
						invoker.invoke(node, collector);
					}
				}
			}
		}
	}

	private <C, E> Builder<C, E> createBuilder(Object node) {
		MethodInvoker invoker = collections.get(node.getClass());
		if (invoker == null) {
			throw new Error("Missing builder for type \"" + node.getClass().getSimpleName() + "\" for collected attribute \"" + getName() + "\".");
		}
		@SuppressWarnings("unchecked")
		Builder<C, E> builder = (Builder<C, E>) invoker.invoke(node);
		return builder;
	}

	private class Collector<C, E> implements javarag.coll.Collector<E> {
		private final IdentityHashMap<Object, Builder<C, E>> builders;
		private final Set<TreeScope> addedTreeScopes;
		private final Deque<TreeScope> treeScopeQueue;

		public Collector() {
			builders = new IdentityHashMap<>();
			addedTreeScopes = new HashSet<>();
			treeScopeQueue = new ArrayDeque<>();
		}

		@Override
		public void add(Object node, E element) {
			Builder<C, E> builder = getBuilder(node);
			builder.add(element);
		}

		private Builder<C, E> getBuilder(Object node) {
			if (builders.containsKey(node)) {
				return builders.get(node);
			}
			Builder<C, E> builder = createBuilder(node);
			builders.put(node, builder);
			return builder;
		}

		@Override
		public void collectFrom(Object treeNode) {
			TreeScope scope = tree.getTreeScopeOf(treeNode);
			if (!addedTreeScopes.contains(scope)) {
				treeScopeQueue.add(scope);
			}
		}

	}

}
