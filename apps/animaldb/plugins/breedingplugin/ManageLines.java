/* Date:        April 28, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.breedingplugin;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class ManageLines extends PluginModel<Entity>
{
	private static final long serialVersionUID = 3355794876439855835L;
	
	private CommonService cs = CommonService.getInstance();
	
	private String lineName;
	private String fullName;
	private int source;
	private int species;
	private String remarks;
	
	private List<ObservationTarget> sourceList;
	private List<ObservationTarget> lineList;
	private List<ObservationTarget> speciesList;

	public ManageLines(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders() {
		return "<script type=\"text/javascript\" src=\"res/jquery-plugins/datatables/js/jquery.dataTables.js\"></script>\n" +
				"<script src=\"res/scripts/custom/addingajax.js\" language=\"javascript\"></script>\n" +
				"<script src=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.js\" language=\"javascript\"></script>\n" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/datatables/css/demo_table_jui.css\">\n" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.css\">" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.rounded.css\">" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.roundedBr.css\">" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
	}

	@Override
	public String getViewName()
	{
		return "plugins_breedingplugin_ManageLines";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/breedingplugin/ManageLines.ftl";
	}
	
	public String getFullName(int lineId) {
		String fullName;
		try {
			fullName = cs.getMostRecentValueAsString(lineId, "LineFullName");
		} catch (Exception e) {
			fullName = "Error when retrieving full name";
		}
		return fullName;
	}
	
	public String getSourceName(int lineId) {
		String sourceName;
		try {
			int sourceId = cs.getMostRecentValueAsXref(lineId, "Source");
			sourceName = cs.getObservationTargetLabel(sourceId);
		} catch (Exception e) {
			sourceName = "Error when retrieving source";
		}
		return sourceName;
	}
	
	public String getSpeciesName(int lineId) {
		String speciesName;
		try {
			int speciesId = cs.getMostRecentValueAsXref(lineId, "Species");
			speciesName = cs.getObservationTargetLabel(speciesId);
		} catch (Exception e) {
			speciesName = "Error when retrieving species";
		}
		return speciesName;
	}
	
	public String getRemarksString(int lineId) throws DatabaseException {
		List<String> remarksList = cs.getRemarks(lineId);
		String returnString = "";
		for (String remark : remarksList) {
			returnString += (remark + "<br>");
		}
		if (returnString.length() > 0) {
			returnString = returnString.substring(0, returnString.length() - 4);
		}
		return returnString;
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		cs.setDatabase(db);
		try {
			String action = request.getString("__action");
			
			if (action.equals("Edit")) {
				int lineId = request.getInt("id");
				this.setLineName(this.getLine(lineId));
				this.setFullName(this.getFullName(lineId));
				this.setSpecies(this.getSpeciesId(lineId));
				this.setSource(this.getSourceId(lineId));
				this.setRemarks(this.getRemarksString(lineId));
			}
			
			if (action.equals("addLine")) {
				// TODO: add or update!!
				Date now = Calendar.getInstance().getTime();
				this.setLineName(request.getString("lineName"));
				// Make group
				int invid = cs.getOwnUserInvestigationId(this.getLogin().getUserId());
				int lineId = cs.makePanel(invid, lineName, this.getLogin().getUserId());
				// Mark group as Line using a special event
				int protocolId = cs.getProtocolId("SetTypeOfGroup");
				int measurementId = cs.getMeasurementId("TypeOfGroup");
				db.add(cs.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, lineId, "Line", 0));
				// Set species
				this.setSpecies(request.getInt("species"));
				protocolId = cs.getProtocolId("SetSpecies");
				measurementId = cs.getMeasurementId("Species");
				db.add(cs.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, lineId, null, species));
				// Set source
				this.setSource(request.getInt("source"));
				protocolId = cs.getProtocolId("SetSource");
				measurementId = cs.getMeasurementId("Source");
				db.add(cs.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, lineId, null, source));
				// Set remark
				if (request.getString("remarks") != null) {
					protocolId = cs.getProtocolId("SetRemark");
					measurementId = cs.getMeasurementId("Remark");
					db.add(cs.createObservedValueWithProtocolApplication(invid, now, null, 
							protocolId, measurementId, lineId, request.getString("remarks"), 0));
				}
				
				this.setSuccess("Line successfully added");
				// TODO: reset everything so form is empty again
			}
			
		} catch (Exception e) {
			this.getMessages().clear();
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
			e.printStackTrace();
		}
	}

	private int getSourceId(int lineId) throws DatabaseException, ParseException {
		return cs.getMostRecentValueAsXref(lineId, "Source");
	}

	private int getSpeciesId(int lineId) throws DatabaseException, ParseException {
		return cs.getMostRecentValueAsXref(lineId, "Species");
	}

	private String getLine(int lineId) throws DatabaseException, ParseException {
		return cs.getObservationTargetLabel(lineId);
	}

	@Override
	public void reload(Database db)
	{
		cs.setDatabase(db);
		cs.makeObservationTargetNameMap(this.getLogin().getUserId(), false);
		
		try {
			List<Integer> investigationIds = cs.getAllUserInvestigationIds(this.getLogin().getUserId());
			// Populate source list
			// All source types pertaining to "Eigen fok binnen uw organisatorische werkeenheid"
			sourceList = new ArrayList<ObservationTarget>();
			List<ObservationTarget> tmpSourceList = cs.getAllMarkedPanels("Source", investigationIds);
			for (ObservationTarget tmpSource : tmpSourceList) {
				int featid = cs.getMeasurementId("SourceType");
				Query<ObservedValue> sourceTypeQuery = db.query(ObservedValue.class);
				sourceTypeQuery.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, tmpSource.getId()));
				sourceTypeQuery.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featid));
				List<ObservedValue> sourceTypeValueList = sourceTypeQuery.find();
				if (sourceTypeValueList.size() > 0)
				{
					String sourcetype = sourceTypeValueList.get(0).getValue();
					if (sourcetype.equals("Eigen fok binnen uw organisatorische werkeenheid")) {
						sourceList.add(tmpSource);
					}
				}
			}
			// Populate species list
			speciesList = cs.getAllMarkedPanels("Species", investigationIds);
			// Populate existing lines list
			lineList = cs.getAllMarkedPanels("Line", investigationIds);
			
		} catch (Exception e) {
			this.getMessages().clear();
			String message = "Something went wrong while loading lists";
			if (e.getMessage() != null) {
				message += (": " + e.getMessage());
			}
			this.getMessages().add(new ScreenMessage(message, false));
			e.printStackTrace();
		}
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	public String getLineName() {
		return lineName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}
	
	public int getSpecies() {
		return species;
	}

	public void setSpecies(int species) {
		this.species = species;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public void setSourceList(List<ObservationTarget> sourceList) {
		this.sourceList = sourceList;
	}

	public List<ObservationTarget> getSourceList() {
		return sourceList;
	}
	
	public void setSpeciesList(List<ObservationTarget> speciesList) {
		this.speciesList = speciesList;
	}

	public List<ObservationTarget> getSpeciesList() {
		return speciesList;
	}

	public List<ObservationTarget> getLineList() {
		return lineList;
	}

	public void setLineList(List<ObservationTarget> lineList) {
		this.lineList = lineList;
	}
}
