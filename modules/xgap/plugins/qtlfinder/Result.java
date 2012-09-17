package plugins.qtlfinder;

import java.util.List;

import org.molgenis.util.Entity;

public class Result
{
	private String selectedName;
	private Entity result;
	private List<? extends Entity> disambiguate;
	private Boolean noResultsFound;
	private List<QTLInfo> qtlsFound;
	
	public String getSelectedName()
	{
		return selectedName;
	}
	public void setSelectedName(String selectedName)
	{
		this.selectedName = selectedName;
	}
	public Entity getResult()
	{
		return result;
	}
	public void setResult(Entity result)
	{
		this.result = result;
	}
	public List<? extends Entity> getDisambiguate()
	{
		return disambiguate;
	}
	public void setDisambiguate(List<? extends Entity> disambiguate)
	{
		this.disambiguate = disambiguate;
	}
	public Boolean getNoResultsFound()
	{
		return noResultsFound;
	}
	public void setNoResultsFound(Boolean noResultsFound)
	{
		this.noResultsFound = noResultsFound;
	}
	public List<QTLInfo> getQtlsFound()
	{
		return qtlsFound;
	}
	public void setQtlsFound(List<QTLInfo> qtlsFound)
	{
		this.qtlsFound = qtlsFound;
	}
}
