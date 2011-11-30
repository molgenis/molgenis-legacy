package csvtobin.sources;

public class IntegerWrapper {
	private int value;
	
	public IntegerWrapper(int value) {
		this.value = value;
	}
	public void set(int value) {
		this.value = value;
	}
	public int get() {
		return this.value;
	}
}
