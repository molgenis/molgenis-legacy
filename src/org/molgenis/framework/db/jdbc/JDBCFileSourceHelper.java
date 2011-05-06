package org.molgenis.framework.db.jdbc;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.FileSourceHelper;
import org.molgenis.util.DetectOS;
import org.molgenis.util.TableUtil;

public class JDBCFileSourceHelper implements FileSourceHelper
{

	private Database db;
	private String variantId;
	
	private String hasSystemSettingsTable;
	private HashMap<String, String> keyValsFromSettingsTable;
	private String mkDirSuccess;
	private String rwDirSuccess;
	private File fileDir;
	private Boolean verified;
	private Boolean folderExists;
	private Boolean folderHasContent;

	public static String systemTableName = "storagedirsettings_090527PBDB00QCGEXP4G";
	public static String fileDirField = "filedirpath";
	public static String verifiedField = "verified";

	public JDBCFileSourceHelper(Database db)
	{
		this.db = db;
	}

	public void setFilesource(String filesource) throws Exception
	{
		reset();

		if (hasSystemSettingsTable.equals("true"))
		{

			if (filesource == null || filesource.equals("") || filesource.equals("null")) throw new DatabaseException(
					"Empty path not allowed");

			filesource = addSepIfneeded(filesource);
			Object o = TableUtil.getFromTable(db, systemTableName, fileDirField);

			if (o != null)
			{
				throw new DatabaseException("Could not write field in system table: already present.");
			}
			else
			{
				boolean success = TableUtil.insertInTable(db, systemTableName, fileDirField, filesource);
				if (!success) throw new DatabaseException("Could not write field in system table.");
			}
		}
		else if (hasSystemSettingsTable.equals("false"))
		{
			System.out.println("getHasSystemSettingsTable: False");

			boolean success = TableUtil.addSystemSettingsTable(db, systemTableName, fileDirField);
			if (!success) throw new DatabaseException("Could not add system table.");

			if (filesource == null || filesource.equals("") || filesource.equals("null"))
			{
				throw new DatabaseException("Empty path not allowed");
			}
			filesource = addSepIfneeded(filesource);
			success = TableUtil.insertInTable(db, systemTableName, fileDirField, filesource);
			if (!success)
			{
				throw new DatabaseException("Could not write field in system table.");
			}

		}
		else
		{
			throw new DatabaseException("Error: unknown status for hasSystemSettingsTable");
		}

	}

	public File getFilesource() throws Exception
	{
		File storage = getFileStorageRoot();
		if(this.variantId == null){
			throw new Exception("Variant ID (app name) not set.");
		}
		return new File(storage.getAbsolutePath() + File.separator + this.variantId);
	}
	
	public void deleteFilesource() throws Exception
	{
		boolean success = TableUtil.removeTable(db, systemTableName);
		if (!success) {
			throw new Exception("Remove failed");
		}
		reset();
	}

	public void validateFileSource() throws Exception
	{
		// mk dir test
		if (hasSystemSettingsTable.equals("true"))
		{
			Object o = TableUtil.getFromTable(db, systemTableName, fileDirField);
			if (o != null)
			{

				String path = o.toString();

				File f = null;
				if (path.startsWith(File.separator))
				{
					f = new File(path);
				}
				else
				{
					f = new File(File.separator + path);
				}

				System.out.println("*** file ref " + f.getAbsolutePath());

				if (f.exists())
				{
					mkDirSuccess = "exists";
				}
				else
				{
					boolean success = f.mkdirs();
					mkDirSuccess = success ? "success" : "fail";
					if (success)
					{
						fileDir = f;
					}
				}
			}
			else
			{
				mkDirSuccess = "fail";
				throw new Exception("No field in system table");
			}
		}
		else
		{
			mkDirSuccess = "fail";
			throw new Exception("No system table");
		}

		// rw dir test
		if (hasSystemSettingsTable.equals("true"))
		{
			Object o = TableUtil.getFromTable(db, systemTableName, fileDirField);
			if (o != null)
			{
				String path = o.toString();

				File f = null;
				if (path.startsWith(File.separator))
				{
					f = new File(path);
				}
				else
				{
					f = new File(File.separator + path);
				}
				if (f.exists())
				{
					File tmp = new File(f.getAbsolutePath() + File.separator + "tmp.txt");
					boolean createSuccess = tmp.createNewFile();
					if (createSuccess)
					{
						System.out.println("*** created " + tmp.getAbsolutePath());
						FileOutputStream fos = new FileOutputStream(tmp);
						DataOutputStream dos = new DataOutputStream(fos);
						dos.writeChars("test");
						dos.close();
						fos.close();

						boolean deleteSuccess = tmp.delete();
						if (deleteSuccess)
						{
							rwDirSuccess = "success";
						}
						else
						{
							rwDirSuccess = "fail";
							throw new Exception("Could not delete file");
						}
					}
					else
					{
						rwDirSuccess = "fail";
						throw new Exception("Could not write to file");
					}
				}
				else
				{
					rwDirSuccess = "fail";
					throw new Exception("Path does not exist");
				}
			}
			else
			{
				rwDirSuccess = "fail";
				throw new Exception("No field in system table");
			}
		}
		else
		{
			rwDirSuccess = "fail";
			throw new Exception("No system table");
		}

	}

	public boolean hasValidFileSource() throws DatabaseException
	{
		// find out if there is a system table
		hasSystemSettingsTable = TableUtil.hasTable(db, systemTableName);

		// set to false to be strict
		verified = false;

		if (hasSystemSettingsTable.equals("true"))
		{
			Object o = TableUtil.getFromTable(db, systemTableName, fileDirField);
			if (o != null)
			{
				String dir = o.toString();
				if (keyValsFromSettingsTable == null)
				{
					HashMap<String, String> keyVals = new HashMap<String, String>();
					keyVals.put(fileDirField, dir);
					keyValsFromSettingsTable = keyVals;
				}
				else
				{
					keyValsFromSettingsTable.put(fileDirField, dir);
				}
				folderExists = folderExists(dir);
				if (folderExists)
				{
					folderHasContent = folderHasContent(dir);
				}
				if (mkDirSuccess != null && rwDirSuccess != null
						&& (mkDirSuccess.equals("success") || mkDirSuccess.equals("exists"))
						&& rwDirSuccess.equals("success"))
				{
					boolean success = TableUtil.updateInTable(db, systemTableName, verifiedField, "1", fileDirField
							+ "='" + dir + "'");
					if (!success)
					{
						throw new DatabaseException("Could not update system table.");
					}
				}
			}
			else
			{
				if (keyValsFromSettingsTable == null)
				{
					HashMap<String, String> keyVals = new HashMap<String, String>();
					keyVals.put(fileDirField, "NULL");
					keyValsFromSettingsTable = keyVals;
				}
				else
				{
					keyValsFromSettingsTable.put(fileDirField, "NULL");
				}
			}
			o = TableUtil.getFromTable(db, systemTableName, verifiedField);
			if (o != null)
			{
				if (o.toString().equals("1"))
				{
					verified = true;
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * ################### Helper functions ###################
	 */
	private void reset()
	{
		mkDirSuccess = null;
		rwDirSuccess = null;
		folderExists = null;
		folderHasContent = null;
	}

	private boolean folderExists(String path)
	{
		File f = new File(path);
		if (f.exists())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Ie. /data/xgap/
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws XGAPStorageException
	 * @throws DatabaseException 
	 * @throws Exception
	 */
	private File getFileStorageRoot() throws UnsupportedEncodingException, DatabaseException
	{
		URI loc = getURIStorageRoot();
		String decodedURI = URLDecoder.decode(loc.toString(), "UTF-8");
		File f = new File(decodedURI.toString());
		return f;
	}

	/**
	 * If OS is unix like, and path does not start with a separator, add
	 * separator in front of the path
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	private String addSepIfneeded(String path) throws Exception
	{
		if (!DetectOS.getOS().startsWith("windows") && !path.startsWith(File.separator))
		{
			path = File.separator + path;
		}
		return path;
	}
	
	public void setVariantId(String variantId){
		this.variantId = variantId;
	}

	private boolean folderHasContent(String path)
	{
		File f = new File(path);
		if (f.exists())
		{
			if (f.listFiles().length == 0)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		return false;
	}

	private URI getURIStorageRoot() throws UnsupportedEncodingException, DatabaseException
	{
		URI res = null;
		if (TableUtil.hasTable(db, systemTableName).equals("true"))
		{
			Object o = TableUtil.getFromTable(db, systemTableName, fileDirField);
			// if there is a file directory at all
			if (o != null)
			{
				String dir = o.toString();
				o = TableUtil.getFromTable(db, systemTableName, verifiedField);
				if (o != null)
				{
					// if the file directory has been verified
					if (o.toString().equals("true"))
					{
						// for non Windows OS's
						if (!DetectOS.getOS().startsWith("windows"))
						{
							// if there is no starting seperator (ie. /data)
							if (!dir.startsWith(File.separator))
							{
								// add one
								dir = File.separator + dir;
							}
						}
						String encodedToUrl = URLEncoder.encode(dir, "UTF-8");
						res = URI.create(encodedToUrl);
					}
				}
			}
		}
		return res;
	}

	public HashMap<String, String> getKeyValsFromSettingsTable()
	{
		return keyValsFromSettingsTable;
	}

	public Boolean getVerified()
	{
		return verified;
	}

	public String getMkDirSuccess()
	{
		return mkDirSuccess;
	}

	public String getRwDirSuccess()
	{
		return rwDirSuccess;
	}
	
	public Database getDb(){
		return db;
	}

//	public File getFileDir()
//	{
//		return fileDir;
//	}

	public Boolean getFolderHasContent()
	{
		return folderHasContent;
	}

}
