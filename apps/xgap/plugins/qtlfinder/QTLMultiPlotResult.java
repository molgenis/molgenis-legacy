package plugins.qtlfinder;

import java.util.HashMap;

import org.molgenis.util.Entity;

public class QTLMultiPlotResult
{
	private String plot;
	private HashMap<String, Entity> matches;

	
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
	
}
