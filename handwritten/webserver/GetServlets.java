import generic.Utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class GetServlets
{
	private HashMap<String,String> servletLocations = new HashMap<String,String>();
	private File servletDir = new File("handwritten"+File.separator+"java"+File.separator+"servlets");
	private String servletBasePath = "servlets.";

	public HashMap<String, String> getServletLocations()
	{
		return servletLocations;
	}

	public GetServlets() throws IOException
	{
		this.servletLocations = recurseDir(servletDir);
	}

	public HashMap<String,String> recurseDir(File f) throws IOException
	{
		if (f.isDirectory())
		{
			for (File file : f.listFiles())
			{
				if (file.getName().endsWith(".java"))
				{
					String servletName = file.getName().substring(0, file.getName().length()-5);
				
					//getting servlet location - start with the base path
					String servletLocation = servletBasePath;
					
					//add servlet name - using absolute path because of package nesting
					servletLocation += file.getAbsolutePath().substring(servletDir.getAbsolutePath().length()+1, file.getAbsolutePath().length());
				
					//remove '.java' and replace '/' leftover from getting absolute path with '.'
					servletLocation = servletLocation.substring(0, servletLocation.length()-5).replace(File.separator, ".");
					
					if(servletLocations.containsKey(servletName)){
						throw new IOException("Duplicate servlet: " + servletName);
					}
					Utils.log(servletLocation,System.err);
					servletLocations.put(servletName, servletLocation);
				}
				if (file.isDirectory())
				{
					recurseDir(file);
				}
			}
		}
		return servletLocations;
	}

	public static void main(String[] args) throws IOException
	{
		HashMap<String, String> res = new GetServlets().getServletLocations();
		
		for (String s : res.keySet())
		{
			System.out.println(s + " @ " + res.get(s));
		}

	}

}
