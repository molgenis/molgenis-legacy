package org.molgenis.framework.db;

import java.io.File;
import java.util.HashMap;

public interface FileSourceHelper
{

	/**
	 * Set the path to the file directory that this database uses to store file
	 * attachments. In a MOLGENIS model these fields are specified as &lt;field
	 * type="file" ... &gt;
	 * 
	 * @param filesource
	 * @throws Exception
	 */
	public abstract void setFilesource(String filesource) throws Exception;

	/**
	 * Get the path to the file directory that this database uses to store file
	 * attachments. In a MOLGENIS model these fields are specified as &lt;field
	 * type="file" ... &gt;
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract File getFilesource() throws Exception;

	/**
	 * Remove the path to the file directory that this database uses to store
	 * file attachments.
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract void deleteFilesource() throws Exception;

	/**
	 * Validate the path to the file directory that this database uses to store
	 * file attachments. For example: test if a directory exists, if not try to
	 * create. If it then exists, try to write and read a file. If all succeeds,
	 * this directory is a valid path, and is flagged as such. The status should
	 * be reset after a new set() operation.
	 * 
	 * @throws Exception
	 */
	public abstract void validateFileSource() throws Exception;

	/**
	 * Ask whether the current file source has been succesfully validated as a
	 * suitable storage location.
	 * 
	 * @return
	 * @throws DatabaseException
	 */
	public abstract boolean hasValidFileSource() throws DatabaseException;

	/**
	 * Setter for the application name. (MOLGENIS variant id, see
	 * MolgenisServlet) it would be nice to not need this function here.
	 * 
	 * @param variantId
	 */
	public abstract void setVariantId(String variantId);

	/**
	 * Getter for file source admin plugin.
	 */
	public abstract HashMap<String, String> getKeyValsFromSettingsTable();

	/**
	 * Getter for file source admin plugin.
	 */
	public abstract Boolean getVerified();

	/**
	 * Getter for file source admin plugin.
	 */
	public abstract String getMkDirSuccess();

	/**
	 * Getter for file source admin plugin.
	 */
	public abstract String getRwDirSuccess();

	/**
	 * Getter for file source admin plugin.
	 */
	//public abstract File getFileDir();

	/**
	 * Getter for file source admin plugin.
	 */
	public abstract Boolean getFolderHasContent();

}