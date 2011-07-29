/* Date:        August 7, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.screen.PluginScreenJavaTemplateGen 3.3.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.report;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

//import app.JDBCDatabase;

public class InvestigationOverview extends PluginModel<Entity>
{

	private static final long serialVersionUID = -3994512440239645094L;

	public enum Action
	{
		Show_All_Investigations, Select_Investigation, Refresh
	};

	// viewtype
	Action state = Action.Show_All_Investigations;

	// all investigations with counts
	List<Tuple> investigations = new ArrayList<Tuple>();
	// selected investigation (in Select_Investigation mode)
	Investigation selectedInvestigation = null;

	// current investigation data
	List<String> features = new ArrayList<String>();
	List<String> targets = new ArrayList<String>();
	Map<String, String> values = new LinkedHashMap<String, String>();

	public InvestigationOverview(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugin_report_InvestigationOverview";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/report/InvestigationOverview.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		Action a = Action.valueOf(request.getAction());

		try
		{
			switch (a)
			{
				case Refresh:
					this.refreshInvestigationReport(db);
					this.state = Action.Show_All_Investigations;
					break;
				case Select_Investigation:

					Integer id = request.getInt("investigationId");
					selectedInvestigation = db.query(Investigation.class).equals("id", id).find().get(0);
					this.state = Action.Select_Investigation;
					break;
				case Show_All_Investigations:
					// default so no breal
				default:
					this.state = Action.Show_All_Investigations;
			}
		}
		catch (Exception e)
		{
			this.setMessages(new ScreenMessage(e.getMessage(), false));
			e.printStackTrace();
			this.state = Action.Show_All_Investigations;
		}
	}

	@Override
	public void reload(Database db)
	{
		try
		{
			switch (state)
			{
				case Show_All_Investigations:
					// only reload on request of 'refresh'
					if (investigations.size() == 0) this.refreshInvestigationReport(db);

					break;
				case Select_Investigation:
					List<ObservedValue> result = db.query(ObservedValue.class).equals("investigation",
							selectedInvestigation.getId()).find();
					for (ObservedValue v : result)
					{
						String f = v.getFeature_Name();
						if (!features.contains(f)) features.add(f);
						String t = v.getTarget_Name();
						if (!targets.contains(t)) targets.add(t);
						values.put(f + "_" + t, v.getValue());
					}
			}
		}
		catch (Exception e)
		{
			this.setMessages(new ScreenMessage(e.getMessage(), false));
			e.printStackTrace();
		}
	}

	private void refreshInvestigationReport(Database db) throws DatabaseException
	{
		// count all investigations and list them
		investigations = db
				.sql("select i.id, i.name as investigation, i.description as description, count(distinct v.observationTarget) as targets, count(distinct v.Measurement) as features from investigation i, observedvalue v where i.id = v.investigation group by i.id, i.name order by i.name;");
		
//		.sql("select i.id as id, i.name as investigation, i.description, t.targets, f.features "
//				+ "from investigation i left join "
//				+ "(select distinct investigation, count(observationTarget) as targets "
//				+ "from observedvalue group by investigation) as t on (i.id = t.investigation) "
//				+ "left join (select distinct investigation, count(Measurement) as features "
//				+ "from observedvalue group by investigation) as f on (i.id = f.investigation) "
//				+ "order by investigation;");

		// clean up 'nulls'
		for (Tuple t : investigations)
		{
			if (t.isNull("description")) t.set("description", "");
			if (t.isNull("targets")) t.set("targets", 0);
			if (t.isNull("features")) t.set("features", 0);
		}
	}

	public String getValue(String feature, String target)
	{
		String val = values.get(feature + "_" + target);
		if (val == null) return "&nbsp;";
		return val;
	}

	public List<Tuple> getInvestigations()
	{
		return investigations;
	}

	public String getState()
	{
		return state.toString();
	}

	public Investigation getSelectedInvestigation()
	{
		return selectedInvestigation;
	}

	public List<String> getFeatures()
	{
		return features;
	}

	public List<String> getTargets()
	{
		return targets;
	}
}
