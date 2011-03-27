package plugins.molgenisfile;

import org.molgenis.core.MolgenisFile;

public class MolgenisFileManagerModel
{

	private MolgenisFile molgenisFile;
	private String db_path;
	private boolean hasFile;
	private String uploadTextAreaContent;
	private String ipURl;
	
	

	public String getUploadTextAreaContent()
	{
		return uploadTextAreaContent;
	}

	public void setUploadTextAreaContent(String uploadTextAreaContent)
	{
		this.uploadTextAreaContent = uploadTextAreaContent;
	}

	public String getIpURl()
	{
		return ipURl;
	}

	public void setIpURl(String ipURl)
	{
		this.ipURl = ipURl;
	}

	public boolean isHasFile()
	{
		return hasFile;
	}

	public void setHasFile(boolean hasFile)
	{
		this.hasFile = hasFile;
	}

	public String getDb_path()
	{
		return db_path;
	}

	public void setDb_path(String dbPath)
	{
		db_path = dbPath;
	}

	public MolgenisFile getMolgenisFile()
	{
		return molgenisFile;
	}

	public void setMolgenisFile(MolgenisFile molgenisFile)
	{
		this.molgenisFile = molgenisFile;
	}

}
