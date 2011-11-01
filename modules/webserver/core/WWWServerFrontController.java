package core;

import generic.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;

public class WWWServerFrontController extends Webserver implements Runnable
{
	private static final long serialVersionUID = 1L;

	public WWWServerFrontController(String variant) throws IOException
	{
		Utils.console("Starting server");
		Webserver.PathTreeDictionary aliases = new Webserver.PathTreeDictionary();

		//aliases
		aliases.put("/", new java.io.File("WebContent"));
		setMappingTable(aliases);

		//mapping
		addServlet(variant + "/", "app.servlet.FrontController");

	}

	public void run()
	{
		try
		{
			serve();
			return;
		}
		catch (Exception e)
		{
			log("ERROR [http server] ", e);
			e.printStackTrace();
			return;
		}
	}

}
