package plugin.search;

import java.util.List;

import org.molgenis.util.Entity;

public class SimpleWholeDatabaseSearchModel
{
	String searchThis;
	List<Entity> results;
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

	public List<Entity> getResults()
	{
		return results;
	}

	public void setResults(List<Entity> results)
	{
		this.results = results;
	}

}
