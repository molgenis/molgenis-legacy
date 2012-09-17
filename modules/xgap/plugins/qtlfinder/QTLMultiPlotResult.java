package plugins.qtlfinder;

import java.util.HashMap;

import org.molgenis.util.Entity;

public class QTLMultiPlotResult
{
	private String plot;
	private String cisTransPlot;
	private String regularPlot;
	private String srcData;
	private String cytoNetwork;
	private String cytoNodes;
	private HashMap<String, Entity> matches;
	private HashMap<String, Entity> datasets;

	
	
	public String getRegularPlot()
	{
		return regularPlot;
	}

	public void setRegularPlot(String regularPlot)
	{
		this.regularPlot = regularPlot;
	}

	public String getCytoNetwork()
	{
		return cytoNetwork;
	}

	public void setCytoNetwork(String cytoNetwork)
	{
		this.cytoNetwork = cytoNetwork;
	}

	public String getCytoNodes()
	{
		return cytoNodes;
	}

	public void setCytoNodes(String cytoNodes)
	{
		this.cytoNodes = cytoNodes;
	}

	public String getSrcData()
	{
		return srcData;
	}

	public void setSrcData(String srcData)
	{
		this.srcData = srcData;
	}

	public HashMap<String, Entity> getDatasets()
	{
		return datasets;
	}

	public void setDatasets(HashMap<String, Entity> datasets)
	{
		this.datasets = datasets;
	}

	public HashMap<String, Entity> getMatches()
	{
		return matches;
	}

	public void setMatches(HashMap<String, Entity> matches)
	{
		this.matches = matches;
	}

	public String getPlot()
	{
		return plot;
	}

	public void setPlot(String plot)
	{
		this.plot = plot;
	}

	public String getCisTransPlot()
	{
		return cisTransPlot;
	}

	public void setCisTransPlot(String cisTransPlot)
	{
		this.cisTransPlot = cisTransPlot;
	}
	
	
	
}
