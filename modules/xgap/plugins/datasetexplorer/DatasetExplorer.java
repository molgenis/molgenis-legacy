/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.datasetexplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import plugins.rplot.MakeRPlot;
import app.JDBCMetaDatabase;

public class DatasetExplorer extends PluginModel
{

	private DatasetExplorerModel model = new DatasetExplorerModel();
	private DataMatrixHandler dmh = null;

	public DatasetExplorerModel getMyModel()
	{
		return model;
	}

	public DatasetExplorer(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "DatasetExplorer";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/datasetexplorer/DatasetExplorer.ftl";
	}

	public void handleRequest(Database db, Tuple request)
	{
		if (request.getString("__action") != null)
		{

			String action = request.getString("__action");

			try
			{

				if (action.equals("buildReport"))
				{
				
					
				}
				else if(action.startsWith("disambig_"))
				{
				
				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			}
		}
	}

	



	@Override
	public void reload(Database db)
	{

		try
		{
			
			
			

		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
		}

	}

}
