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
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;




public class InvestigationReport extends PluginModel<Entity>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4913176206503894738L;

	// selected investigation (in Select_Investigation mode)
	Investigation selectedInvestigation = null;

	// current investigation data
	List<Measurement> features = new ArrayList<Measurement>();
	List<ObservationTarget> targets = new ArrayList<ObservationTarget>();
	Map<String, String> values = new LinkedHashMap<String, String>();

	public InvestigationReport(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugin_report_InvestigationReport";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/report/InvestigationReport.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		//
	}

	@Override
	public void reload(Database db)
	{
		values.clear();
		features.clear();
		targets.clear();
		
		List<String> featureNames = new ArrayList<String>();
		List<String> targetNames = new ArrayList<String>();
		try
		{
			//FIXME Code smell shouldn't this be a static function, seeing as everyone uses it in their plugins
			FormModel<Investigation> parentForm = (FormModel<Investigation>)getParent().getParent();
			selectedInvestigation = parentForm.getRecords().get(0);
			
			List<ObservedValue> result = db.query(ObservedValue.class).equals("investigation",
					selectedInvestigation.getId()).find();
			for (ObservedValue v : result)
			{
				String f = v.getFeature_Name();
				if (!featureNames.contains(f)) featureNames.add(f);
				String t = v.getTarget_Name();
				if (!targetNames.contains(t)) targetNames.add(t);
				values.put(f + "_" + t, v.getValue());
			}
			
			features = db.query(Measurement.class).in("name", featureNames).equals("investigation", selectedInvestigation.getId()).find();
			targets = db.query(ObservationTarget.class).in("name", targetNames).equals("investigation", selectedInvestigation.getId()).find();
		}
		catch (Exception e)
		{
			this.setMessages(new ScreenMessage(e.getMessage(), false));
			e.printStackTrace();
		}
	}

	public String getValue(String feature, String target)
	{
		String val = values.get(feature + "_" + target);
		if (val == null) return "&nbsp;";
		return val;
	}

	public Investigation getSelectedInvestigation()
	{
		return selectedInvestigation;
	}

	public List<Measurement> getFeatures()
	{
		return features;
	}

	public List<ObservationTarget> getTargets()
	{
		return targets;
	}
}
