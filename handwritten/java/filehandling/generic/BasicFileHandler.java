package filehandling.generic;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.molgenis.framework.db.Database;
import org.molgenis.util.DetectOS;

import plugins.system.settings.Settings;
import plugins.system.settings.TableUtil;
import app.JDBCDatabase;
import app.servlet.MolgenisServlet;

public class BasicFileHandler
{

	Database db = null;
	
	public Database getDb(){
		return db;
	}

	public BasicFileHandler(Database db)
	{
		this.db = db;
	}

	private URI getURIStorageRoot() throws XGAPStorageException, UnsupportedEncodingException
	{
		URI res = null;
		if (TableUtil.hasTable(db, Settings.systemTableName).equals("true"))
		{
			Object o = TableUtil.getFromTable(db, Settings.systemTableName, Settings.fileDirField);
			// if there is a file directory at all
			if (o != null)
			{
				String dir = o.toString();
				o = TableUtil.getFromTable(db, Settings.systemTableName, Settings.verifiedField);
				if (o != null)
				{
					// if the file directory has been verified
					if (o.toString().equals("true"))
					{
						// System.out.println("verified = true, dir = " + dir);
						// for non Windows OS's
						if (!DetectOS.getOS().startsWith("windows"))
						{
							// if there is no starting seperator (ie. /data)
							if (!dir.startsWith(File.separator))
							{
								// add one
								dir = File.separator + dir;
								// System.out.println("dir is now = " + dir);
							}
						}
						String encodedToUrl = URLEncoder.encode(dir, "UTF-8");
						// System.out.println("encodedToUrl = " + encodedToUrl);
						res = URI.create(encodedToUrl);
					}
				}
			}
		}
		// System.out.println("getValidatedFileStorageLocation called. result: "+
		// res.toString());
		return res;
	}

	public boolean hasFileStorage() throws UnsupportedEncodingException, XGAPStorageException
	{
		try
		{
			getFileStorage();
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	/**
	 * Ie. /data/xgap/mydeployname/
	 * 
	 * @return
	 * @throws XGAPStorageException
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	public File getFileStorage() throws UnsupportedEncodingException, XGAPStorageException
	{
		File storage = getFileStorageRoot();
		String deploy = MolgenisServlet.getMolgenisVariantID();
		return new File(storage.getAbsolutePath() + File.separator + deploy);
	}

	/**
	 * Ie. /data/xgap/
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws XGAPStorageException
	 * @throws Exception
	 */
	private File getFileStorageRoot() throws UnsupportedEncodingException, XGAPStorageException
	{
		URI loc = getURIStorageRoot();
		// System.out.println("loc = " + loc.toString());
		String decodedURI = URLDecoder.decode(loc.toString(), "UTF-8");
		// System.out.println("decodedURI = " + decodedURI);

		File f = new File(decodedURI.toString());
		// System.out.println("f path = " + f.getAbsolutePath());

		return f;
	}

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception
	{
		Database db = new JDBCDatabase("xgap.properties");
		/*BasicFileHandler sfh = */new BasicFileHandler(db);
		// System.out.println(sfh.getValidatedFileStorageLocationToFile().getAbsolutePath());

		// ie..

		/*File storageDir = */new BasicFileHandler(db).getFileStorageRoot();
	}

	public class XGAPStorageException extends Exception
	{
		private static final long serialVersionUID = 7026641720938935426L;

		public XGAPStorageException(String e)
		{
			super(e);
		}
	}

}
