/*
 * Date: April 8, 2011 Template: PluginScreenJavaTemplateGen.java.ftl generator:
 * org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.system.database;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import matrix.general.DataMatrixHandler;

import org.molgenis.MolgenisOptions;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.cluster.DataValue;
import org.molgenis.core.OntologyTerm;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Panel;
import org.molgenis.pheno.Species;
import org.molgenis.util.Tuple;
import org.molgenis.xgap.Chromosome;
import org.molgenis.xgap.InvestigationFile;
import org.molgenis.xgap.Marker;
import org.molgenis.xgap.Metabolite;
import org.molgenis.xgap.xqtlworkbench.ResetXgapDb;

import plugins.cluster.demo.ClusterDemo;
import regressiontest.cluster.DataLoader;
import app.servlet.UsedMolgenisOptions;

public class Settings extends PluginModel
{
	private String console = "";
	private Map<String, String> info = new LinkedHashMap<String, String>();

	public Settings(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_system_database_Settings";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/system/database/Settings.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{
			this.console = "";

			ArrayList<String> result = new ArrayList<String>();

			if ("loadExampleData".equals(request.getAction()))
			{

				if (db.find(MolgenisUser.class).size() == 2)
				{
					console += "No existing users in db (except admin/anonymous), adding example users..<br>";
					ClusterDemo.addExampleUsers(db);
					console += "Giving extra needed permissions to example users..<br>";
					ClusterDemo.giveExtraNeededPermissions(db);
				}
				else
				{
					console += "BEWARE: Existing users found, skipping adding example users!<br>";
				}

				result = DataLoader.load(db, false);
			}
			else if ("resetDatabase".equals(request.getAction()))
			{
				result.add(ResetXgapDb.reset(db, true));
			}
			else if ("resetDatabaseSoft".equals(request.getAction()))
			{
				result.add(ResetXgapDb.reset(db, false));
			}
			else if ("removeExampleData".equals(request.getAction()))
			{
				result = deleteExampleInvestigation("ClusterDemo", db);
			}
			if (result.size() > 0) for (String line : result)
			{
				console += line + "<br>";
			}
			else
				console = null;

		}
		catch (Exception e)
		{
			this.setMessages(new ScreenMessage(e.getMessage(), false));
		}
	}
	
	public static ArrayList<String> deleteExampleInvestigation(String name, Database db)
	{
		
		ArrayList<String> report = new ArrayList<String>();
		report.add("Starting to delete example investigation '"+name+"'..");
	
		try{

			List<Investigation> invList = db.find(Investigation.class, new QueryRule(Investigation.NAME, Operator.EQUALS, name));
			if(invList.size() == 0)
			{
				throw new Exception("Investigation named '" + name + "' has NOT been found!! aborting..");
			}
			report.add("Investigation '" + name + "' has been found..");
			
			Investigation inv = invList.get(0);
			String invName = inv.getName(); //not really needed but whatever :)
			
			List<Panel> panels = db.find(Panel.class, new QueryRule(Panel.INVESTIGATION_NAME, Operator.EQUALS, invName));
			report.add(db.remove(panels) + " panels deleted");
			
			List<Marker> markers = db.find(Marker.class, new QueryRule(Marker.INVESTIGATION_NAME, Operator.EQUALS, invName));
			report.add(db.remove(markers) + " markers deleted");
			
			List<Chromosome> chromosomes = db.find(Chromosome.class, new QueryRule(Chromosome.INVESTIGATION_NAME, Operator.EQUALS, invName));
			report.add(db.remove(chromosomes) + " chromosomes deleted");
			
			List<Individual> individuals = db.find(Individual.class, new QueryRule(Individual.INVESTIGATION_NAME, Operator.EQUALS, invName));
			report.add(db.remove(individuals) + " individuals deleted");
			
			List<Metabolite> metabolites = db.find(Metabolite.class, new QueryRule(Metabolite.INVESTIGATION_NAME, Operator.EQUALS, invName));
			report.add(db.remove(metabolites) + " metabolites deleted");
			
			List<Data> data = db.find(Data.class, new QueryRule(Data.INVESTIGATION_NAME, Operator.EQUALS, invName));
			
			DataMatrixHandler dmh = new DataMatrixHandler(db);
			for(Data d : data){
				
				//this is the only link between "cluster metadata" and the example that we need to break
				//re-importing the example dataset will fail on the last step where the "cluster metadata" is added:
				//to restore to old situation, add the 2 'DataValue' records manually
				report.add("Deleting tag for '"+d.getName()+"' first..");
				List<DataValue> dvlist = db.find(DataValue.class, new QueryRule(DataValue.VALUE_NAME, Operator.EQUALS, d.getName()));
				report.add(db.remove(dvlist) + " datavalues deleted");
				
				try{
				dmh.deleteDataMatrixSource(d, db);
				report.add("Data source for '"+d.getName()+"' deleted..");
				}catch(Exception e){
					report.add("Data source for '"+d.getName()+"' not deleted due to: " + e.getMessage());
					report.add("Continueing...");
				}
				db.remove(d);
				report.add("Data matrix '"+d.getName()+"' deleted");
			}

			List<OntologyTerm> onto = db.find(OntologyTerm.class, new QueryRule(OntologyTerm.NAME, Operator.LIKE, "xgap_rqtl_straintype_"));
			report.add(db.remove(onto) + " ontologyterms (containing 'xgap_rqtl_straintype_') deleted");
			
			List<OntologyTerm> onto2 = db.find(OntologyTerm.class, new QueryRule(OntologyTerm.NAME, Operator.LIKE, "_matrix"));
			report.add(db.remove(onto2) + " ontologyterms (containing '_matrix') deleted");
			
			List<Species> spec = db.find(Species.class, new QueryRule(Species.NAME, Operator.EQUALS, "Arabidopsis_thaliana"));
			report.add(db.remove(spec) + " species (named 'Arabidopsis_thaliana') deleted");
			
			List<InvestigationFile> files = db.find(InvestigationFile.class, new QueryRule(InvestigationFile.INVESTIGATION_NAME, Operator.EQUALS, invName));
			report.add(db.remove(files) + " files removed (Plink example data)");
			
			report.add(db.remove(inv) + " investigations deleted");
			
			report.add("All done!");
			
		}
		catch(Exception e)
		{
			report.add("ERROR: " + e.getMessage());
		}
		
		return report;
		
	}

	@Override
	public void reload(Database db)
	{
		MolgenisOptions mo = new UsedMolgenisOptions();
		String models = "";
		for (String modelXml : mo.getModelDatabase())
		{
			modelXml = modelXml.replace("modules/datamodel/", "");
			models += modelXml + "<br>";
		}

		info.put("db_user", mo.db_user);
		info.put("db_driver", mo.db_driver);
		info.put("db_uri", mo.db_uri);
		info.put("db_mode", mo.db_mode);
		info.put("object_relational_mapping", mo.object_relational_mapping);
		info.put("mapper_implementation", mo.mapper_implementation.toString());
		info.put("model_database", models);
		info.put("model_userinterface", mo.model_userinterface);
		info.put("auth_loginclass", mo.auth_loginclass);
		info.put("decorator_overriders", mo.decorator_overriders);
		info.put("render_decorator", mo.render_decorator);

	}

	public Map<String, String> getInfo()
	{
		return info;
	}

	public String getConsole()
	{
		return console;
	}

	public void setConsole(String console)
	{
		this.console = console;
	}

}
