package plugins.qtlfinder;

import java.util.List;

import org.molgenis.xgap.Marker;

public class QTLInfo
{
	private String plot;
	private String peakMarker;
	private Double peakValue;
	private List<String> markers;
	private List<Double> valuesForMarkers;
	
	public QTLInfo(String peakMarker, Double peakValue, List<String> markers, List<Double> valuesForMarkers)
	{
		super();
		this.peakMarker = peakMarker;
		this.peakValue = peakValue;
		this.markers = markers;
		this.valuesForMarkers = valuesForMarkers;
	}
	
	
	public void setPlot(String plot)
	{
		this.plot = plot;
	}


	public String getPlot()
	{
		return plot;
	}
	public String getPeakMarker()
	{
		return peakMarker;
	}
	public Double getPeakValue()
	{
		return peakValue;
	}
	public List<String> getMarkers()
	{
		return markers;
	}
	public List<Double> getValuesForMarkers()
	{
		return valuesForMarkers;
	}

	
	
}
