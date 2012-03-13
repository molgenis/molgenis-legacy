/* Date:        May 10, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.specialgroups;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;


public class AddSpecialGroupPlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = 3656718805951479631L;
	private List<String> labelList = new ArrayList<String>();
	private CommonService ct = CommonService.getInstance();
	
	public AddSpecialGroupPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders()
    {
        return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
    }

	@Override
	public String getViewName()
	{
		return "org_molgenis_animaldb_plugins_specialgroups_AddSpecialGroupPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/animaldb/plugins/specialgroups/AddSpecialGroupPlugin.ftl";
	}
	
	// Label list related methods:
	public List<String> getLabelList() {
		return labelList;
	}

	public void setLabelList(List<String> labelList) {
		this.labelList = labelList;
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try {
			int invid = ct.getOwnUserInvestigationId(this.getLogin().getUserName());
			String action = request.getString("__action");
			if (action.equals("addSpecialGroup")) {
				// Get values from form
				Calendar calendar = Calendar.getInstance();
				Date now = calendar.getTime();
				String name = request.getString("name");
				String label = null;
				if (request.getString("newlabel") != null) {
					label = request.getString("newlabel");
				} else {
					label = request.getString("label");
				}
				
				int groupid = ct.makePanel(invid, name, this.getLogin().getUserId());
				if (label != null && !label.equals("0")) {
					int protocolId = ct.getProtocolId("SetTypeOfGroup");
					int measurementId = ct.getMeasurementId("TypeOfGroup");
					db.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
							protocolId, measurementId, groupid, label, 0));
				}
				
				this.getMessages().clear();
				this.getMessages().add(new ScreenMessage("Group successfully added", true));
				this.reload(db);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.getMessages().clear();
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
			// Populate label list
			labelList.clear();
			Query<ObservedValue> q = db.query(ObservedValue.class);
			q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "TypeOfGroup"));
			List<ObservedValue> valueList = q.find();
			for (ObservedValue v : valueList) {
				if (!labelList.contains(v.getValue())) {
					labelList.add(v.getValue());
				}
			}
		} catch (Exception e) {
			this.getMessages().clear();
			String message = "Something went wrong while loading label list";
			if (e.getMessage() != null) {
				message += (": " + e.getMessage());
			}
			this.getMessages().add(new ScreenMessage(message, false));
			e.printStackTrace();
		}
	}
	
}
