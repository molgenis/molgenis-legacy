import generic.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class GetServlets
{
	private HashMap<String,String> servletLocations = new HashMap<String,String>();
	private String servletBasePath = "servlets.";

	public HashMap<String, String> getServletLocations()
	{
		return servletLocations;
	}

	public GetServlets() throws IOException
	{
		URL decoratorOverrideURL = getClass().getResource("/servlets");
		
		URL decoratorOverrideFolder = null;
		
		if(decoratorOverrideURL != null){
			decoratorOverrideFolder = new URL("file://" + decoratorOverrideURL.getFile().replace("%20", " "));
			System.out.println("Servlet location '"+decoratorOverrideFolder+"' loaded.");
			servletLocations = recurseDir(decoratorOverrideFolder);
		}else{
			System.err.println("Servlet location '/servlets' could not be loaded.");
			return;
		}
	}

	public HashMap<String,String> recurseDir(URL u) throws IOException
	{
		File f = new File(u.getPath());
		if (f.isDirectory()){
			for (File file : f.listFiles())	{
				Utils.log("Found: servlet file ? " + f.getAbsolutePath(),System.err);
				String servletLocation = servletBasePath;
				if (file.getName().endsWith(".java") || file.getName().endsWith(".class")){
					String filename = file.getName();
					String servletName = filename.substring(0,filename.indexOf("."));
					String fullname = file.getAbsolutePath();
					String application = fullname.substring(fullname.indexOf(servletBasePath.replace(".",""))+(servletBasePath.length()-1),fullname.lastIndexOf(File.separator));
					
					application = application.replace(File.separator, ".");
					if(application.equals(File.separator)) application = "";
					if(application.startsWith(".")) application = application.substring(1);
					
					if(servletLocations.containsKey(servletName)){
						throw new IOException("Duplicate servlet: " + servletName);
					}
					Utils.console(servletLocation + (application.equals("")?"":application + ".") + servletName);
					servletLocations.put(servletName, servletLocation + (application.equals("")?"":application + ".") + servletName);
				}
				if (file.isDirectory()){
					recurseDir(file.toURL());
				}
			}
		}else{
			Utils.log("Not a directory: " + f.isDirectory() +" "+ f.isFile(),System.err);
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
