package plugin.search;

import java.util.List;

public class SimpleWholeDatabaseSearchModel
{

	String searchThis;
	List<org.molgenis.util.Entity> results;
	double time;

	public double getTime()
	{
		return time;
	}

	public void setTime(double time)
	{
		this.time = time;
	}

	public String getSearchThis()
	{
		return searchThis;
	}

	public void setSearchThis(String searchThis)
	{
		this.searchThis = searchThis;
	}

	public List<org.molgenis.util.Entity> getResults()
	{
		return results;
	}

	public void setResults(List<org.molgenis.util.Entity> results)
	{
		this.results = results;
	}
	
	

}
