package core;

import generic.Utils;

import java.io.IOException;

public class WWWServer extends Webserver implements Runnable
{
	private static final long serialVersionUID = 1L;

	public WWWServer(String variant) throws IOException
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
