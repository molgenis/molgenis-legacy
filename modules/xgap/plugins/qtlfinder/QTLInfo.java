package plugins.qtlfinder;

import java.util.List;
import java.util.Map;

import org.molgenis.data.Data;
import org.molgenis.xgap.Marker;

public class QTLInfo
{
	private String plot;
	private String peakMarker;
	private Double peakValue;
	private List<String> markers;
	private List<Double> valuesForMarkers;
	private Map<String, Marker> markerAnnotations;
	private Data matrix;
	
	public QTLInfo(Data matrix, String peakMarker, Double peakValue, List<String> markers, List<Double> valuesForMarkers)
	{
		super();
		this.matrix = matrix;
		this.peakMarker = peakMarker;
		this.peakValue = peakValue;
		this.markers = markers;
		this.valuesForMarkers = valuesForMarkers;
	}
	
	
	public void setPlot(String plot)
	{
		this.plot = plot;
	}
	
	


	public void setMarkerAnnotations(Map<String, Marker> markerAnnotations)
	{
		this.markerAnnotations = markerAnnotations;
	}
	public Data getMatrix()
	{
		return matrix;
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
	public Map<String, Marker> getMarkerAnnotations()
	{
		return markerAnnotations;
	}

}
