/* Date:        April 28, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.breedingplugin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.molgenis.framework.db.Database;
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
	private int source;
	private List<ObservationTarget> sourceList;

	public ManageLines(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders() {
		return "<script src=\"res/scripts/custom/addingajax.js\" language=\"javascript\"></script>\n"
				+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
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

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try {
			String action = request.getString("__action");
			if (action.equals("addLine")) {
				Date now = Calendar.getInstance().getTime();
				this.setLineName(request.getString("lineName"));
				// Make group
				int invid = cs.getUserInvestigationId(this.getLogin().getUserId());
				int groupid = cs.makePanel(invid, lineName, this.getLogin().getUserId());
				// Mark group as Line using a special event
				int protocolId = cs.getProtocolId("SetTypeOfGroup");
				int measurementId = cs.getMeasurementId("TypeOfGroup");
				db.add(cs.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, groupid, "Line", 0));
				// Set source
				this.setSource(request.getInt("source"));
				protocolId = cs.getProtocolId("SetSource");
				measurementId = cs.getMeasurementId("Source");
				db.add(cs.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, groupid, null, source));
				
				this.getMessages().clear();
				this.getMessages().add(new ScreenMessage("Line succesfully added", true));
			}
			
		} catch (Exception e) {
			this.getMessages().clear();
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
			e.printStackTrace();
		}
	}

	@Override
	public void reload(Database db)
	{
		cs.setDatabase(db);
		cs.makeObservationTargetNameMap(this.getLogin().getUserId(), false);
		
		try {
			int investigationId = cs.getUserInvestigationId(this.getLogin().getUserId());
			// Populate source list
			// All source types pertaining to "Eigen fok binnen uw organisatorische werkeenheid"
			sourceList = new ArrayList<ObservationTarget>();
			List<ObservationTarget> tmpSourceList = cs.getAllMarkedPanels("Source", investigationId);
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

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public void setSourceList(List<ObservationTarget> sourceList) {
		this.sourceList = sourceList;
	}

	public List<ObservationTarget> getSourceList() {
		return sourceList;
	}
}
