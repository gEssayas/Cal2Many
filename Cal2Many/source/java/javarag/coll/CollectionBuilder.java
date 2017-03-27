package javarag.coll;

import java.util.Collection;

public class CollectionBuilder<C extends Collection<E>, E> implements Builder<C, E> {
	private C coll;

	public CollectionBuilder(C coll) {
		if (coll == null) {
			throw new NullPointerException();
		}
		this.coll = coll;
	}

	@Override
	public void add(E element) {
		if (coll == null) {
			throw new IllegalStateException("Collection is already built.");
		}
		coll.add(element);
	}

	@Override
	public C build() {
		C result = coll;
		coll = null;
		return result;
	}
}
