/* Date:        October 5, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.xgap.other.panacea.header;

import org.molgenis.auth.MolgenisPermission;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Tuple;

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
		return "org_molgenis_xgap_other_panacea_header_HomePage";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/xgap/other/panacea/header/HomePage.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		String action = request.getString("__action");
		if (action.equals("setPathAndLoad"))
		{
			setupStorageAndLoadExample(db, request.getString("fileDirPath"));
			addPanaceaPermissions(db);
		}
	}

	private void addPanaceaPermissions(Database db)
	{
		try
		{

			String[] qtlFinderPerms = new String[]
			{

					// allow to see the QTL finder
					"app.ui.QtlFinderPublicPlugin",

					// allow to find genes and probes
					"org.molgenis.xgap.Gene", "org.molgenis.xgap.Probe",

					// allow to see marker info and plotting
					"org.molgenis.xgap.Marker",

					// allow reading datasets
					"org.molgenis.data.Data", "org.molgenis.data.BinaryDataMatrix", "org.molgenis.data.CSVDataMatrix",
					"org.molgenis.data.DecimalDataElement", "org.molgenis.data.TextDataElement",

					// allow reading dataset backend files
					"org.molgenis.core.MolgenisFile", "org.molgenis.core.RuntimeProperty",

					// allow to see how uploaded this dataset
					"org.molgenis.protocol.ProtocolApplication_Performer"

			};

			for (String e : qtlFinderPerms)
			{
				MolgenisPermission mp = new MolgenisPermission();
				mp.setEntity_ClassName(e);
				mp.setRole_Name("anonymous");
				mp.setPermission("read");
				db.add(mp);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage(), false));
		}
	}

}
