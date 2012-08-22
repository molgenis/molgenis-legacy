package plugins.qtlfinder;

import java.util.List;
import java.util.Map;

import org.molgenis.data.Data;

public class QtlFinderModel{

	private Map<String, Result> resultSet;
	private List<Data> dataSets;
	private String query;
	private Double threshold;
	private List<String> tickedDataSets;
	private QTLMultiPlotResult qmpr;

	
	
	public QTLMultiPlotResult getQmpr()
	{
		return qmpr;
	}
	public void setQmpr(QTLMultiPlotResult qmpr)
	{
		this.qmpr = qmpr;
	}
	public List<String> getTickedDataSets()
	{
		return tickedDataSets;
	}
	public void setTickedDataSets(List<String> tickedDataSets)
	{
		this.tickedDataSets = tickedDataSets;
	}
	public Double getThreshold()
	{
		return threshold;
	}
	public void setThreshold(Double threshold)
	{
		this.threshold = threshold;
	}
	public Map<String, Result> getResultSet()
	{
		return resultSet;
	}
	public void setResultSet(Map<String, Result> resultSet)
	{
		this.resultSet = resultSet;
	}
	public List<Data> getDataSets()
	{
		return dataSets;
	}
	public void setDataSets(List<Data> dataSets)
	{
		this.dataSets = dataSets;
	}
	public String getQuery()
	{
		return query;
	}
	public void setQuery(String query)
	{
		this.query = query;
	}
	

}
