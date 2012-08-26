package plugins.xgapwizard;

import java.util.List;

import org.molgenis.data.Data;

public class DataInfo
{
	
	public DataInfo(Data data, String existingDataSource, List<String> tags){
		this.data = data;
		this.existingDataSource = existingDataSource;
		this.tags = tags;
	}

	private Data data;
	private List<String> tags;
	private String existingDataSource;
	
	
	
	public List<String> getTags()
	{
		return tags;
	}
	public Data getData()
	{
		return data;
	}
	public void setData(Data data)
	{
		this.data = data;
	}
	public String getExistingDataSource()
	{
		return existingDataSource;
	}
	public void setExistingDataSource(String existingDataSource)
	{
		this.existingDataSource = existingDataSource;
	}
	
	
	
}
