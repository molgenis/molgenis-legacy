/* Date:        February 24, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.listplugin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class ListPlugin2 extends PluginModel<Entity> {
	private static final long serialVersionUID = -7341276676642021364L;
	private List<ObservationTarget> targetList;
	private List<Measurement> featureList;
	private List<ObservationTarget> groupList = new ArrayList<ObservationTarget>();
	private boolean firstTime = true;
	private CommonService ct = CommonService.getInstance();

	public ListPlugin2(String name, ScreenController<?> parent) {
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders() {
		//return "<script type=\"text/javascript\" src=\"res/scripts/custom/jquery.dataTables.js\"></script>\n"
		//	+ "<script type=\"text/javascript\" charset=\"utf-8\">jQuery.noConflict();</script>\n"
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">\n"
		//	+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/demo_table.css\">\n"
		//	+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/demo_page.css\">\n"
			+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/scripts/custom/extjs/resources/css/ext-all.css\">\n"
			+ "<script type=\"text/javascript\" src=\"res/scripts/custom/extjs/adapter/jquery/ext-jquery-adapter-debug.js\"></script>\n"
			+ "<script type=\"text/javascript\" src=\"res/scripts/custom/extjs/ext-all-debug.js\"></script>\n";
	}


	@Override
	public String getViewName() {
		return "plugins_animaldb_listplugin2_ListPlugin2";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/animaldb/listplugin2/ListPlugin2.ftl";
	}

	// Animal related methods:
	public List<ObservationTarget> getTargetList() {
		return targetList;
	}

	public void setTargetList(List<ObservationTarget> targetList) {
		this.targetList = targetList;
	}
	
	// Feature related methods:
	public List<Measurement> getFeatureList() {
		return featureList;
	}

	public void setFeatureList(List<Measurement> featureList) {
		this.featureList = featureList;
	}
	
	// Group list related methods:
	public List<ObservationTarget> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<ObservationTarget> groupList) {
		this.groupList = groupList;
	}

	public void handleRequest(Database db, Tuple request) {
		try {
			String action = request.getString("__action");
			if (action.equals("saveGroup")) {
				Calendar calendar = Calendar.getInstance();
				Date now = calendar.getTime();
				int investigationId = ct.getInvestigationId("AnimalDB");
				String groupName;
				int groupId;
				
				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
				
				if (request.getString("newgroupname") != null) {
					groupName = request.getString("newgroupname");
					groupId = ct.makePanel(investigationId, groupName, this.getLogin().getUserId());
					int protocolId = ct.getProtocolId("SetTypeOfGroup");
					int measurementId = ct.getMeasurementId("TypeOfGroup");
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(investigationId, 
							now, null, protocolId, measurementId, groupId, "Selection", 0));
				} else {
					groupName = request.getString("groupname");
					groupId = ct.getObservationTargetId(groupName);
				}
				List<?> nameList = request.getList("saveselection", ",");
				for (Object o : nameList) {
					int animalId = ct.getObservationTargetId(o.toString());
					valuesToAddList.add(ct.addObservationTargetToPanel(investigationId, animalId, now, groupId));
					// cq function checks if animal is already in group
				}
				
				// Add everything to DB
				db.add(valuesToAddList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// e.g. show a message in your form
		}
	}
	
	public void reload(Database db) {
		if (firstTime) {
			firstTime = false;
			ct.setDatabase(db);
			ct.makeObservationTargetNameMap(this.getLogin().getUserId());
		}
		
		// Reset servlet
		//try {
		//	URL servletURL = new URL("http://localhost:8080/pheno/EventViewerExtJsJSONServlet?reset=1");
		//	URLConnection servletConn = servletURL.openConnection();
		//	servletConn.getContent();
		//} catch (MalformedURLException e1) {
		//	e1.printStackTrace();
		//} catch (IOException e) {
		//	e.printStackTrace();
		//}
			
		// Populate target list
//		try {
//			this.setTargetList(ct.GetAllAnimals());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		// Populate feature list
//		try {
//			this.setFeatureList(ct.GetAllMeasurements());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		// Populate group list
//		try {
//			groupList = ct.GetAllMarkedGroups("Selection");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

}
