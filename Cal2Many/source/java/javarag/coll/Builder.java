package javarag.coll;

public interface Builder<C, E> {
	void add(E element);
	C build();
}
