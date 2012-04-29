package qtltogff.sources;

public class Peak {
	
	
	
	public Peak(String leftFlankMarker, String rightFlankMarker, String peakMarker, Double peakValue)
	{
		super();
		this.leftFlankMarker = leftFlankMarker;
		this.rightFlankMarker = rightFlankMarker;
		this.peakMarker = peakMarker;
		this.peakValue = peakValue;
	}
	
	private String leftFlankMarker;
	private String rightFlankMarker;
	private String peakMarker;
	private Double peakValue;
	
	public String getLeftFlankMarker()
	{
		return leftFlankMarker;
	}
	public String getRightFlankMarker()
	{
		return rightFlankMarker;
	}
	public String getPeakMarker()
	{
		return peakMarker;
	}
	public Double getPeakValue()
	{
		return peakValue;
	}
	
	
	

}
