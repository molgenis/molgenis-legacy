package plugins.genenetwork;

import java.util.ArrayList;

public class GeneNetworkModel {

	
	private String probeset = "98332_at";
	private String db = "bra08-03MAS5";
	private String probe= "119637";
	private String format = "col";
	
	private ArrayList<String> result = new ArrayList<String>();
	
	private String uploadResponse = "";
	
	
	
	public String getUploadResponse()
	{
		return uploadResponse;
	}
	public void setUploadResponse(String uploadResponse)
	{
		this.uploadResponse = uploadResponse;
	}
	public ArrayList<String> getResult()
	{
		return result;
	}
	public void setResult(ArrayList<String> result)
	{
		this.result = result;
	}
	public String getProbeset()
	{
		return probeset;
	}
	public void setProbeset(String probeset)
	{
		this.probeset = probeset;
	}
	public String getDb()
	{
		return db;
	}
	public void setDb(String db)
	{
		this.db = db;
	}
	public String getProbe()
	{
		return probe;
	}
	public void setProbe(String probe)
	{
		this.probe = probe;
	}
	public String getFormat()
	{
		return format;
	}
	public void setFormat(String format)
	{
		this.format = format;
	}
	
	
	
}
