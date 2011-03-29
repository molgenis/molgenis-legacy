/* Date:        September 14, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.animal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.pheno.Code;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class RemAnimalPlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = 6730055654508843657L;
	private List<Integer> animalIdList;
	private Map<Integer, String> animalMap;
	private List<Code> removalCodeList;
	private CommonService ct = CommonService.getInstance();

	public RemAnimalPlugin(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
	}

	public String getCustomHtmlHeaders()
	{
		return "<script src=\"res/scripts/custom/addingajax.js\" language=\"javascript\"></script>\n"
				+ "<script src=\"res/scripts/custom/animalterm.js\" language=\"javascript\"></script>\n"
				+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
	}

	@Override
	public String getViewName()
	{
		return "plugins_animal_RemAnimalPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/animal/RemAnimalPlugin.ftl";
	}

	// Animal related methods:
	public List<Integer> getAnimalIdList()
	{
		return animalIdList;
	}

	public void setAnimalIdList(List<Integer> animalIdList)
	{
		this.animalIdList = animalIdList;
	}
	
	public String getAnimalName(Integer id) {
		if (animalMap.get(id) != null) {
			return animalMap.get(id);
		} else {
			return id.toString();
		}
	}

	public void setRemovalCodeList(List<Code> removalCodeList)
	{
		this.removalCodeList = removalCodeList;
	}

	public List<Code> getRemovalCodeList()
	{
		return removalCodeList;
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{
			String action = request.getString("__action");
			if (action.equals("applyDeath"))
			{
				// Get animal ID
				int animalId = request.getInt("animal");
				
				// Get kind of removal
				String removal = request.getString("removal");

				// Get datetime of removal
				String deathDatetimeString = request.getString("deathdatetime");
				SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss", Locale.US);
				SimpleDateFormat sdfForDbCompare = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
				Date deathDatetime = sdf.parse(deathDatetimeString);
				String deathDatetimeParsedString = sdfForDbCompare.format(deathDatetime);

				// Check if animal in experiment
				int featureId = ct.getMeasurementId("Experiment");
				Query<ObservedValue> q = db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, animalId));
				q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureId));
				q.addRules(new QueryRule(ObservedValue.TIME, Operator.LESS_EQUAL, deathDatetimeParsedString));
				q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
				List<ObservedValue> valueList = q.find();
				int expid = -1;
				String discomfort = "";
				String endstatus = "";
				if (valueList.size() == 1)
				{
					ObservedValue value = valueList.get(0);
					expid = value.getRelation_Id();
					// set end date-time here already
					value.setEndtime(deathDatetime);
					db.update(value);
					// get values from form
					discomfort = request.getString("discomfort");
					endstatus = request.getString("endstatus");
				}
				
				int investigationId = ct.getInvestigationId("AnimalDB");
				
				// Set 'Removal' feature
				int protocolId = ct.getProtocolId("SetRemoval");
				int measurementId = ct.getMeasurementId("Removal");
				db.add(ct.createObservedValueWithProtocolApplication(investigationId, deathDatetime, 
						null, protocolId, measurementId, animalId, removal, 0));
				
				// Report as dead/removed by setting the endtime of the Active value
				featureId = ct.getMeasurementId("Active");
				Query<ObservedValue> activeQuery = db.query(ObservedValue.class);
				activeQuery.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, animalId));
				activeQuery.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureId));
				List<ObservedValue> activeValueList = activeQuery.find();
				if (activeValueList.size() == 1) {
					ObservedValue activeValue = activeValueList.get(0);
					activeValue.setEndtime(deathDatetime);
					activeValue.setValue("Dead");
					db.update(activeValue);
				}
				
				// If applicable, set Death date
				if (removal.equals("dood")) {
					protocolId = ct.getProtocolId("SetDeathDate");
					measurementId = ct.getMeasurementId("DeathDate");
					db.add(ct.createObservedValueWithProtocolApplication(investigationId, 
							deathDatetime, null, protocolId, measurementId, animalId, 
							deathDatetimeParsedString, 0));
				}
				
				// Set subproject end values
				if (expid != -1) {
					protocolId = ct.getProtocolId("AnimalFromSubproject");
					ProtocolApplication app = ct.createProtocolApplication(investigationId, protocolId);
					db.add(app);
					int protappid = app.getId();
					measurementId = ct.getMeasurementId("FromExperiment");
					db.add(ct.createObservedValue(investigationId, protappid, deathDatetime, null, 
							measurementId, animalId, null, expid));
					measurementId = ct.getMeasurementId("ActualDiscomfort");
					db.add(ct.createObservedValue(investigationId, protappid, deathDatetime, null, 
							measurementId, animalId, discomfort, 0));
					measurementId = ct.getMeasurementId("ActualAnimalEndStatus");
					db.add(ct.createObservedValue(investigationId, protappid, deathDatetime, null, 
							measurementId, animalId, endstatus, 0));
				}
				
				this.getMessages().clear();
				this.getMessages().add(new ScreenMessage("Animal(s) successfully removed", true));
				this.reload(db);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
		}
	}

	@Override
	public void reload(Database db)
	{
		ct.setDatabase(db);

		try {
			// Populate animal list
			this.setAnimalIdList(ct.getAllObservationTargetIds("Individual", true));
			this.animalMap = ct.getObservationTargetNames(this.animalIdList);
			
			// Populate removal code list
			this.setRemovalCodeList(ct.getAllCodesForFeature("Removal"));
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
		}
	}

	@Override
	public boolean isVisible()
	{
		// you can use this to hide this plugin, e.g. based on user rights.
		if (this.getLogin().isAuthenticated()){
			return true;
		} else {
			return false;
		}
	}
}
