package boot;

import java.awt.HeadlessException;
import java.io.IOException;

import core.Webserver;

import test.Helper;

public class RunStandalone
{

	public RunStandalone(Integer port)
	{
		try
		{
			try
			{
				new WebserverGui(port);
			}
			catch (HeadlessException e)
			{
				System.out.println("No GUI available - going into commandline mode");
				new Thread(new WebserverCmdLine(port)).start();
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
			new RunStandalone(Integer.valueOf(args[0]));
						
		}
		else
		{
			throw new IOException(
					"Use either no arguments to select the default port (plus portscan if it is unavailable), or 1 argument as the port. (no further portscan)");
		}
	}
}
