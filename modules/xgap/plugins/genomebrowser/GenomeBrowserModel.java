package plugins.genomebrowser;

import java.util.List;

import org.molgenis.xgap.InvestigationFile;

public class GenomeBrowserModel
{
	private String appUrl;
	private String release;
	private List<InvestigationFile> gffFiles;
	private Boolean filesAreVisible;

	public Boolean getFilesAreVisible()
	{
		return filesAreVisible;
	}

	public void setFilesAreVisible(Boolean filesAreVisible)
	{
		this.filesAreVisible = filesAreVisible;
	}

	public String getRelease()
	{
		return release;
	}

	public void setRelease(String release)
	{
		this.release = release;
	}

	public List<InvestigationFile> getGffFiles()
	{
		return gffFiles;
	}

	public void setGffFiles(List<InvestigationFile> gffFiles)
	{
		this.gffFiles = gffFiles;
	}

	public String getAppUrl()
	{
		return appUrl;
	}

	public void setAppUrl(String appUrl)
	{
		this.appUrl = appUrl;
	}
	
}
