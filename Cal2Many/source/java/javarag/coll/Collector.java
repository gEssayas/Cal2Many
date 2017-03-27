package javarag.coll;

public interface Collector<E> {
	void add(Object node, E element);
	void collectFrom(Object tree);
}
