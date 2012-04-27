package boot;

import java.awt.HeadlessException;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;

import core.Helper;
import core.Webserver;


public class RunStandalone
{

	public RunStandalone(Integer port)
	{
		new RunStandalone(port, false);
	}
	
	public RunStandalone(Integer port, boolean headless)
	{
		try
		{
			try
			{
				if(headless)
				{
					throw new HeadlessException("NOTE: Headless mode was forced by user");
				}
				new WebserverGui(port);
			}
			catch (HeadlessException e)
			{
				System.out.println(e.getMessage() + "\nNo GUI available - going into headless mode");
				new WebserverCmdLine(port);
			}
		}
		catch (IOException e)
		{
			System.out.println("IO exception bubbled up to main\nSomething went wrong: " + e.getMessage());
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		// enable log
		BasicConfigurator.configure();
		
		if (args.length == 0)
		{

			// get the default port
			int port = Webserver.DEF_PORT;

			// check if the port is free, if not, try the next 100
			int freePort = Helper.getAvailablePort(port, 100);

			// run the app on a free port
			new RunStandalone(freePort);

		}
		else if (args.length == 1)
		{

			// run the app the selected port, and on this port only
			int port = Integer.valueOf(args[0]);
			
			if (Helper.isAvailable(port))
			{
				new RunStandalone(port);
			}
			else
			{
				throw new IOException("Port " + port + " already in use!");
			}
		}
		else
		{
			throw new IOException(
					"Use either no arguments to select the default port (plus portscan if it is unavailable), or 1 argument as the port. (no further portscan)");
		}
	}
}
