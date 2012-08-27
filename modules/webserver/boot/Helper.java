package boot;

import java.io.IOException;
import java.net.ServerSocket;

public class Helper
{
	/**
	 * check if port is already used
	 * 
	 * @throws IOException
	 */
	public static boolean isAvailable(int port) throws IOException
	{
		ServerSocket socket = null;
		try
		{
			socket = new ServerSocket(port);
		}
		catch (IOException e)
		{
			return false;
		}
		finally
		{
			if (socket != null)
			{
				socket.close();
			}
		}
		return true;
	}

	/**
	 * Return the initial port number if it was available. Otherwise, increase
	 * with 1 over a given range until a free port was found. If none are found,
	 * throws IOException.
	 * 
	 * @param initialPort
	 * @param range
	 * @return
	 * @throws IOException
	 */
	public static int getAvailablePort(int initialPort, int range) throws IOException
	{
		for (int port = initialPort; port < (initialPort + range); port++)
		{
			boolean portTaken = false;
			ServerSocket socket = null;
			try
			{
				socket = new ServerSocket(port);
			}
			catch (IOException e)
			{
				portTaken = true;
			}
			finally
			{
				if (socket != null)
				{
					socket.close();
				}
			}
			if (!portTaken)
			{
				return port;
			}
		}
		throw new IOException("All ports in the range " + initialPort + "-" + (initialPort + range)
				+ " were unavailable. Select a different initial port or increase the scanning range.");
	}

}
