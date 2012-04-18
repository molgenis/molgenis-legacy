package plugins.genomebrowser;

import java.util.List;

import org.molgenis.core.MolgenisFile;

public class GenomeBrowserModel
{
	private String appUrl;
	private String release;
	private List<MolgenisFile> gffFiles;
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

	public List<MolgenisFile> getGffFiles()
	{
		return gffFiles;
	}

	public void setGffFiles(List<MolgenisFile> gffFiles)
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
