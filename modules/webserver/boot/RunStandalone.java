package boot;

import java.awt.HeadlessException;
import java.io.IOException;

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
				System.out.println("No GUI available going into commandline mode");
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
	public static void main(String[] args)
	{
		if(args.length == 0){
			// null construct: start on port 8080
			new RunStandalone(null);
		}else if(args.length == 1){
			//int constructor: start on custom port
			new RunStandalone(Integer.parseInt(args[0]));
		}
		
	}
}
