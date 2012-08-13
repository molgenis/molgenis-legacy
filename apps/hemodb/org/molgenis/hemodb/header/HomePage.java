/* Date:        October 5, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.hemodb.header;

import java.io.File;

import matrix.general.DataMatrixHandler;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Tuple;

import plugins.system.database.Settings;
import app.ExcelImport;

public class HomePage extends plugins.cluster.demo.ClusterDemo
{

	private static final long serialVersionUID = -3744678801173089268L;

	public HomePage(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_hemodb_header_HomePage";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/hemodb/header/HomePage.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		String action = request.getString("__action");
		if (action.equals("setPathAndLoad"))
		{
			setupStorageAndLoadExample(db, request.getString("fileDirPath"));
			addHemoPermissionsAndTryToImportData(db);
		}
	}

	private void addHemoPermissionsAndTryToImportData(Database db)
	{
		try
		{

			DataMatrixHandler dmh = new DataMatrixHandler(db);

			if (dmh.hasValidFileStorage(db))
			{
				String path = dmh.getFileStorage(true, db).getAbsolutePath();

				// import WormQTL annotations from 'imports' location
				String importDir = path + File.separator + "imports";

				// excel with everything minus USA probes
				File hemoAnnotations = new File(importDir + File.separator + "hemodbAnnotations.xls");
				if (!hemoAnnotations.exists())
				{
					throw new Exception("Annotation Excel file" + hemoAnnotations.getAbsolutePath() + " is missing!");
				}

				ExcelImport.importAll(hemoAnnotations, db, null);

				// relink datasets
				relinkDatasets(db, dmh);

				// remove clusterdemo example investigation
				Settings.deleteExampleInvestigation("ClusterDemo", db);

				// all done
				this.setMessages(new ScreenMessage("HemoDb specific annotation import and data relink succeeded", true));
			}
			else
			{
				this.setMessages(new ScreenMessage(
						"HemoDb permissions loaded, but could not import annotations because storagedir setup failed",
						false));
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage(), false));
		}
	}

	/**
	 * Relink datasets if needed: but expected is that ALL are relinked when the
	 * function ends, or else error
	 * 
	 * @param db
	 * @param dmh
	 * @throws Exception
	 */
	private void relinkDatasets(Database db, DataMatrixHandler dmh) throws Exception
	{
		for (Data data : db.find(Data.class))
		{
			// find out if the 'Data' has a proper backend
			boolean hasLinkedStorage = dmh.isDataStoredIn(data, data.getStorage(), db);

			// if not, it doesn't mean the source file is not there! e.g. after
			// updating your database
			if (!hasLinkedStorage)
			{
				// attempt to relink
				boolean relinked = dmh.attemptStorageRelink(data, data.getStorage(), db);

				if (!relinked)
				{
					throw new Exception("Could not relink data matrix '" + data.getName() + "'");
				}

				if (!dmh.isDataStoredIn(data, data.getStorage(), db))
				{
					throw new Exception("SEVERE: Data matrix '" + data.getName()
							+ "' is supposed to be relinked, but the isDataStoredIn check failed!");
				}

			}
		}

	}

}
