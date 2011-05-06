package plugin.search;

import java.util.List;

import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.SimpleScreenModel;

public class SimpleWholeDatabaseSearchModel extends SimpleScreenModel
{

	public SimpleWholeDatabaseSearchModel(ScreenController controller)
	{
		super(controller);
		// TODO Auto-generated constructor stub
	}

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

	@Override
	public boolean isVisible()
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
