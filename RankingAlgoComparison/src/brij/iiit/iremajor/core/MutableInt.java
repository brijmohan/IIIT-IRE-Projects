package brij.iiit.iremajor.core;
public class MutableInt implements Comparable<MutableInt> {
	int value = 1;
	
	public MutableInt(int val) {
		value = val;
	}

	public void increment() {
		++value;
	}
	
	public void incrementBy(int val) {
		value += val;
	}

	public int get() {
		return value;
	}

	@Override
	public int compareTo(MutableInt o) {
		return (this.value - o.value);
	}
}