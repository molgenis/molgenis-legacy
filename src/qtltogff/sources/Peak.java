package qtltogff.sources;

public class Peak {
	
	public Peak(int startIndex, int stopIndex) {
		super();
		this.startIndex = startIndex;
		this.stopIndex = stopIndex;
	}
	
	private int startIndex;
	private int stopIndex;
	
	public int getStartIndex() {
		return startIndex;
	}

	public int getStopIndex() {
		return stopIndex;
	}
	
}
