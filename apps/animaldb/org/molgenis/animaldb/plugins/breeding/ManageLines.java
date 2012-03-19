/* Date:        April 28, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.breeding;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.molgenis.animaldb.commonservice.CommonService;
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


public class ManageLines extends PluginModel<Entity>
{
	private static final long serialVersionUID = 3355794876439855835L;
	
	private CommonService cs = CommonService.getInstance();
	
	private String lineName;
	private String fullName;
	private String sourceName;
	private String speciesName;
	private String remarks;
	private int lineId = -1;
	
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
		return "org_molgenis_animaldb_plugins_breeding_ManageLines";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/animaldb/plugins/breeding/ManageLines.ftl";
	}
	
	public String getFullName(String lineName) {
		String fullName;
		try {
			fullName = cs.getMostRecentValueAsString(lineName, "LineFullName");
		} catch (Exception e) {
			fullName = "Error when retrieving full name";
		}
		return fullName;
	}
	
	public String getSourceName(String lineName) {
		String sourceName;
		try {
			sourceName = cs.getMostRecentValueAsXrefName(lineName, "Source");
		} catch (Exception e) {
			sourceName = "Error when retrieving source";
		}
		return sourceName;
	}
	
	public String getSpeciesName(String lineName) {
		String speciesName;
		try {
			speciesName = cs.getMostRecentValueAsXrefName(lineName, "Species");
		} catch (Exception e) {
			speciesName = "Error when retrieving species";
		}
		return speciesName;
	}
	
	public String getRemarksString(String lineName) throws DatabaseException {
		//List<String> remarksList = cs.getRemarks(lineId);
		String returnString = "";
//		for (String remark : remarksList) {
//			returnString += (remark + "<br>");
//		}
//		if (returnString.length() > 0) {
//			returnString = returnString.substring(0, returnString.length() - 4);
//		}
		try {
			returnString = cs.getMostRecentValueAsString(lineName, "Remark");
		} catch (Exception e) {
			returnString = "Error when retrieving remarks";
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
				lineId = request.getInt("id");
				lineName = this.getLine(lineId);
				fullName = this.getFullName(lineName);
				speciesName = this.getSpeciesName(lineName);
				sourceName = this.getSourceName(lineName);
				remarks = this.getRemarksString(lineName);
			}
			
			if (action.equals("Delete")) {
				lineId = request.getInt("id");
				List<ObservedValue> valList = db.query(ObservedValue.class).eq(ObservedValue.TARGET, lineId).
						or().eq(ObservedValue.RELATION, lineId).find();
				db.remove(valList);
				ObservationTarget line = cs.getObservationTargetById(lineId);
				db.remove(line);
				this.setSuccess("Line successfully removed");
			}
			
			if (action.equals("addLine")) {
				Date now = new Date();
				lineName = request.getString("lineName");
				String invName = cs.getOwnUserInvestigationName(this.getLogin().getUserName());
				String message = "";
				// Make or get group
				if (lineId == -1) {
					lineId = cs.makePanel(invName, lineName, this.getLogin().getUserName());
					message = "Line successfully added";
				} else {
					ObservationTarget line = cs.getObservationTargetById(lineId);
					line.setName(lineName); // maybe user has changed name
					db.update(line);
					message = "Line successfully updated";
				}
				// Mark group as Line using a special event
				db.add(cs.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetTypeOfGroup", "TypeOfGroup", lineName, "Line", null));
				// Set full name
				if (request.getString("fullname") != null) {
					fullName = request.getString("fullname");
					db.add(cs.createObservedValueWithProtocolApplication(invName, now, null, 
							"SetLineFullName", "LineFullName", lineName, fullName, null));
				}
				// Set species
				speciesName = request.getString("species");
				db.add(cs.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetSpecies", "Species", lineName, null, speciesName));
				// Set source
				sourceName = request.getString("source");
				db.add(cs.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetSource", "Source", lineName, null, sourceName));
				// Set remark
				if (request.getString("remarks") != null) {
					remarks = request.getString("remarks");
					db.add(cs.createObservedValueWithProtocolApplication(invName, now, null, 
							"SetRemark", "Remark", lineName, remarks, null));
				}
				this.setSuccess(message);
				// Reset everything so form is empty again
				lineId = -1;
				lineName = null;
				fullName = null;
				speciesName = null;
				sourceName = null;
				remarks = null;
			}
			
		} catch (Exception e) {
			this.getMessages().clear();
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
			e.printStackTrace();
		}
	}

	private String getLine(int lineId) throws DatabaseException, ParseException {
		return cs.getObservationTargetLabel(lineId);
	}

	@Override
	public void reload(Database db)
	{
		cs.setDatabase(db);
		cs.makeObservationTargetNameMap(this.getLogin().getUserName(), false);
		
		try {
			List<String> investigationNames = cs.getAllUserInvestigationNames(this.getLogin().getUserName());
			// Populate source list
			// All source types pertaining to "Eigen fok binnen uw organisatorische werkeenheid"
			sourceList = new ArrayList<ObservationTarget>();
			List<ObservationTarget> tmpSourceList = cs.getAllMarkedPanels("Source", investigationNames);
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
			speciesList = cs.getAllMarkedPanels("Species", investigationNames);
			// Populate existing lines list
			lineList = cs.getAllMarkedPanels("Line", investigationNames);
			
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

	public String getSource() {
		if (sourceName == null) {
			return "";
		}
		return sourceName;
	}
	
	public String getSpecies() {
		if (speciesName == null) {
			return "";
		}
		return speciesName;
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
