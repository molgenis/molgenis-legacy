package plugins.molgenisfile.plink;

import java.util.List;

import org.molgenis.organization.Investigation;


public class PlinkFileManagerModel
{
	private String uploadMode = null;
	private List<Investigation> investigations;
	private Integer selectedInv;
	
	public String getUploadMode()
	{
		return uploadMode;
	}

	public void setUploadMode(String uploadMode)
	{
		this.uploadMode = uploadMode;
	}

	public List<Investigation> getInvestigations()
	{
		return investigations;
	}

	public void setInvestigations(List<Investigation> investigations)
	{
		this.investigations = investigations;
	}

	public Integer getSelectedInv()
	{
		return selectedInv;
	}

	public void setSelectedInv(Integer selectedInv)
	{
		this.selectedInv = selectedInv;
	}
	
	
	

}
