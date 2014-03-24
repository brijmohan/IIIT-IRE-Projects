public class MutableInt {
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
}