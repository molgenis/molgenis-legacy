package plugins.matrix.heatmap;

import matrix.DataMatrixInstance;
import plugins.matrix.manager.MatrixManagerModel;

public class MatrixHeatmapModel extends MatrixManagerModel{

	
	private Double lowestVal;
	private Double highestVal;
	private RGB start;
	private RGB stop;
	private DataMatrixInstance heatMatrix;
	private Boolean autoScale;
	private Double customStart;
	private Double customStop;
	
	
	public Double getCustomStart()
	{
		return customStart;
	}
	public void setCustomStart(Double customStart)
	{
		this.customStart = customStart;
	}
	public Double getCustomStop()
	{
		return customStop;
	}
	public void setCustomStop(Double customStop)
	{
		this.customStop = customStop;
	}
	public Boolean getAutoScale()
	{
		return autoScale;
	}
	public void setAutoScale(Boolean autoScale)
	{
		this.autoScale = autoScale;
	}
	public Double getLowestVal()
	{
		return lowestVal;
	}
	public void setLowestVal(Double lowestVal)
	{
		this.lowestVal = lowestVal;
	}
	public Double getHighestVal()
	{
		return highestVal;
	}
	public void setHighestVal(Double highestVal)
	{
		this.highestVal = highestVal;
	}
	public RGB getStart()
	{
		return start;
	}
	public void setStart(RGB start)
	{
		this.start = start;
	}
	public RGB getStop()
	{
		return stop;
	}
	public void setStop(RGB stop)
	{
		this.stop = stop;
	}
	public DataMatrixInstance getHeatMatrix()
	{
		return heatMatrix;
	}
	public void setHeatMatrix(DataMatrixInstance heatMatrix)
	{
		this.heatMatrix = heatMatrix;
	}
	
	
	
}
