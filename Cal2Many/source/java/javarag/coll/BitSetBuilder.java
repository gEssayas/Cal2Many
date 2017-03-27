package javarag.coll;

import java.util.BitSet;

public class BitSetBuilder implements Builder<BitSet, Integer> {
	private final BitSet bitSet = new BitSet();

	@Override
	public void add(Integer element) {
		bitSet.set(element);
	}

	@Override
	public BitSet build() {
		return bitSet;
	}
}