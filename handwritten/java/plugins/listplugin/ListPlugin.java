/* Date:        February 24, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.listplugin;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import app.servlet.MolgenisServlet;

import commonservice.CommonService;

public class ListPlugin extends PluginModel<Entity> {
	private static final long serialVersionUID = -7341276676642021364L;
	private List<Measurement> featureList;
	private List<Panel> groupList = new ArrayList<Panel>();
	private CommonService ct = CommonService.getInstance();

	public ListPlugin(String name, ScreenModel<Entity> parent) {
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders() {
		return "<script type=\"text/javascript\" src=\"res/scripts/custom/jquery.dataTables.js\"></script>\n"
			//+ "<script type=\"text/javascript\" charset=\"utf-8\">jQuery.noConflict();</script>\n"
			//+ "<script src=\"res/scripts/custom/jquery-ui-1.8.6.custom.min.js\" type=\"text/javascript\" language=\"javascript\"></script>"
			+ "<script src=\"res/scripts/custom/jquery.autocomplete.combobox.js\" type=\"text/javascript\" language=\"javascript\"></script>"
			+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">\n"
			+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/demo_table.css\">\n"
			+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/demo_page.css\">\n";
			//+"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/ui-lightness/jquery-ui-1.8.6.custom.css\">";
		   //+ "<script>$(document).ready(function(){ $( \"#arf\" ).combobox();  }); </script>";
			//+ "<script> $(function() { $( \"#arf\" ).combobox();  });	</script>;";
	}

	@Override
	public String getViewName() {
		return "plugins_listplugin_ListPlugin";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/listplugin/ListPlugin.ftl";
	}
	
	// Feature related methods:
	public List<Measurement> getFeatureList() {
		return featureList;
	}

	public void setFeatureList(List<Measurement> featureList) {
		this.featureList = featureList;
	}
	
	// Group list related methods:
	public List<Panel> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<Panel> groupList) {
		this.groupList = groupList;
	}

	public void handleRequest(Database db, Tuple request) {
		try {
			String action = request.getString("__action");
			if (action.equals("saveGroup")) {
				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
				
				Calendar calendar = Calendar.getInstance();
				Date now = calendar.getTime();
				int investigationId = ct.getInvestigationId("AnimalDB");
				String groupName;
				int groupId;
				if (request.getString("newgroupname") != null) {
					groupName = request.getString("newgroupname");
					groupId = ct.makePanel(investigationId, groupName);
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
					// ct function checks if animal is already in group
				}
				
				// Add everything to DB
				db.add(valuesToAddList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null) {
				this.setMessages(new ScreenMessage(e.getMessage(), false));
			}
		}
	}
	
	public void reload(Database db) {
		
		// Reset servlet
		try {
			URL servletURL = new URL("http://localhost:8080/" + MolgenisServlet.getMolgenisVariantID() + 
					"/EventViewerJSONServlet?reset=1");
			URLConnection servletConn = servletURL.openConnection();
			servletConn.getContent();
		} catch (Exception e) {
			//
		}
			
		try {
			// Populate measurement list
			List<Measurement> featList = ct.getAllMeasurementsSorted(Measurement.NAME, "ASC");
			if (featList.size() > 0) {
				this.setFeatureList(featList);
			} else {
				throw new DatabaseException("Something went wrong while loading Measurement list");
			}
		
			// Populate group list
			groupList = ct.getAllMarkedPanels("Selection");
		} catch (Exception e) {
			this.setMessages(new ScreenMessage(e.getMessage(), false));
		}
	}

	@Override
	public boolean isVisible() {
		// you can use this to hide this plugin, e.g. based on user rights.
		if (this.getLogin().isAuthenticated()) {
			return true;
		} else {
			return false;
		}
	}
}
