package qtltogff.sources;

public class Peak {
	
	public Peak(int startIndex, int stopIndex, String peakMarker,
			Double peakValue) {
		super();
		this.startIndex = startIndex;
		this.stopIndex = stopIndex;
		this.peakMarker = peakMarker;
		this.peakValue = peakValue;
	}
	
	private int startIndex;
	private int stopIndex;
	private String peakMarker;
	private Double peakValue;
	
	public int getStartIndex() {
		return startIndex;
	}
	public int getStopIndex() {
		return stopIndex;
	}
	public String getPeakMarker() {
		return peakMarker;
	}
	public Double getPeakValue() {
		return peakValue;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public void setStopIndex(int stopIndex) {
		this.stopIndex = stopIndex;
	}
	public void setPeakMarker(String peakMarker) {
		this.peakMarker = peakMarker;
	}
	public void setPeakValue(Double peakValue) {
		this.peakValue = peakValue;
	}
	
	

}
