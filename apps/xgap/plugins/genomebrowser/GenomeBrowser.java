/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.genomebrowser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.model.elements.Field;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;
import org.molgenis.xgap.Locus;
import org.molgenis.xgap.Marker;
import org.molgenis.xgap.Probe;

import plugins.qtlfinder.QTLInfo;
import plugins.qtlfinder.QTLMultiPlotResult;
import plugins.reportbuilder.Report;
import plugins.reportbuilder.ReportBuilder;
import plugins.reportbuilder.Statistics;
import plugins.rplot.MakeRPlot;
import app.JDBCMetaDatabase;

public class GenomeBrowser extends PluginModel<Entity>
{

	private static final long serialVersionUID = 1L;

	private GenomeBrowserModel model = new GenomeBrowserModel();
	
	static String __ALL__DATATYPES__SEARCH__KEY = "__ALL__DATATYPES__SEARCH__KEY";
	
	private int plotWidth = 1024;
	private int plotHeight = 768;

	public GenomeBrowserModel getMyModel()
	{
		return model;
	}

	public GenomeBrowser(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "GenomeBrowser";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/genomebrowser/GenomeBrowser.ftl";
	}
	
	public void handleRequest(Database db, Tuple request)
	{
		if (request.getString("__action") != null)
		{
			String action = request.getString("__action");
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
