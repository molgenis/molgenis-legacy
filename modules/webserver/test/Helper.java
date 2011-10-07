package test;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

import org.molgenis.framework.db.Database;
import org.molgenis.util.TarGz;

import app.DatabaseFactory;
import app.servlet.MolgenisServlet;
import filehandling.storage.StorageHandler;

public class Helper
{

	public static void deleteDatabase() throws Exception
	{
		File dbDir = new File("hsqldb");
		if (dbDir.exists())
		{
			TarGz.recursiveDeleteContentIgnoreSvn(dbDir);
		}
		else
		{
			throw new Exception("HSQL database directory does not exist");
		}

		if (dbDir.list().length != 1)
		{
			throw new Exception("HSQL database directory does not contain 1 file (.svn) after deletion! it contains: "
					+ dbDir.list().toString());
		}
	}

	public static void deleteStorage() throws Exception
	{
		// get storage folder and delete it completely
		// throws exceptions if anything goes wrong
		Database db = DatabaseFactory.create();
		int appNameLength = MolgenisServlet.getMolgenisVariantID().length();
		String storagePath = new StorageHandler(db).getFileStorage(true).getAbsolutePath();
		File storageRoot = new File(storagePath.substring(0, storagePath.length() - appNameLength));
		System.out.println("Removing content of " + storageRoot);
		TarGz.recursiveDeleteContent(new File(storagePath));
		System.out.println("Removing folder " + storageRoot);
		TarGz.delete(storageRoot, false);
	}

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

	public static void main(String[] args) throws Exception
	{
		deleteDatabase();

	}

}
