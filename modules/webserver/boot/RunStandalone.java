package boot;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;

import app.servlet.UsedMolgenisOptions;

/**
 * TJWS wrapper for Molgenis. See http://tjws.sourceforge.net/ -> Embedded usage
 * 
 * @author joerivandervelde
 * 
 */
public class RunStandalone
{
	public RunStandalone(int port) throws IOException
	{
		class MyServ extends Acme.Serve.Serve
		{
			private static final long serialVersionUID = -4687683036134257812L;

			public void setMappingTable(PathTreeDictionary mappingtable)
			{
				super.setMappingTable(mappingtable);
			}
		}
		;

		// enable log
		BasicConfigurator.configure();

		// server
		final MyServ srv = new MyServ();

		// set WebContent alias
		Acme.Serve.Serve.PathTreeDictionary aliases = new Acme.Serve.Serve.PathTreeDictionary();
		aliases.put("/*", new java.io.File("WebContent"));
		srv.setMappingTable(aliases);

		// set port
		java.util.Properties properties = new java.util.Properties();
		properties.put("port", port);
		properties.setProperty(Acme.Serve.Serve.ARG_NOHUP, "nohup");
		srv.arguments = properties;

		// add FrontController as the only servlet
		String variant = new UsedMolgenisOptions().appName;
		srv.addServlet(variant + "/*", "app.servlet.FrontController");

		// add shutdown hooks
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
		{
			public void run()
			{
				srv.notifyStop();
				srv.destroyAllServlets();
			}
		}));

		// run server in new thread
		(new Thread()
		{
			public void run()
			{
				srv.serve();
			}
		}).start();
		
		// display app location
		System.out.println("*********************************************************");
		System.out.println("APPLICATION IS RUNNING AT: http://localhost:" + port + "/" + variant + "/");
		System.out.println("*********************************************************");

	}

	public static void main(String[] args) throws IOException
	{
		int port;
		if (args.length == 0)
		{
			// check if the default 8080 port is free, if not, try the next 100
			port = Helper.getAvailablePort(8080, 100);
			new RunStandalone(port);
		}
		else if (args.length == 1)
		{
			// run the app the selected port, and on this port only
			port = Integer.valueOf(args[0]);

			// if not available, throw error
			if (!Helper.isAvailable(port))
			{
				throw new IOException("Port " + port + " already in use!");
			}
			new RunStandalone(port);
		}
		else
		{
			throw new IOException(
					"Use either no arguments to select the default port (plus portscan if it is unavailable), or 1 argument as the port. (no further portscan)");
		}
	}
}