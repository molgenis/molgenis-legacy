
package plugins.breedingplugin;

import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

/**
 * ViewFamilyController takes care of all user requests and application logic.
 *
 * <li>Each user request is handled by its own method based action=methodName. 
 * <li> MOLGENIS takes care of db.commits and catches exceptions to show to the user
 * <li>ViewFamilyModel holds application state and business logic on top of domain model. Get it via this.getModel()/setModel(..)
 * <li>ViewFamilyView holds the template to show the layout. Get/set it via this.getView()/setView(..).
 */
public class ViewFamily extends PluginModel<Entity>
{
	private static final long serialVersionUID = -7609580651170222454L;
	private List<Integer> animalIdList;
	private String action = "init";
	private String info = "";
	private CommonService cs = CommonService.getInstance();

	public ViewFamily(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders()
	{
		return 	"<script src=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.js\" language=\"javascript\"></script>\n" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.css\">" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
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
		try {
			return cs.getObservationTargetLabel(id);
		} catch (Exception e) {
			return id.toString();
		}
	}
	
	/**
	 * At each page view: reload data from database into model and/or change.
	 *
	 * Exceptions will be caught, logged and shown to the user automatically via setMessages().
	 * All db actions are within one transaction.
	 */ 
	@Override
	public void reload(Database db)
	{	
		cs.setDatabase(db);
		cs.makeObservationTargetNameMap(this.getLogin().getUserId(), false);

		try {
			// Populate animal list
			List<Integer> investigationIds = cs.getAllUserInvestigationIds(this.getLogin().getUserId());
			this.setAnimalIdList(cs.getAllObservationTargetIds("Individual", true, investigationIds));
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
		}
	}

	@Override
	public String getViewName() {
		return "plugins_breedingplugin_ViewFamily";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/breedingplugin/ViewFamilyView.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request) {
		try
		{
			action = request.getString("__action");
			if (action.equals("reqInfo"))
			{
				// Get animal ID
				int animalId = request.getInt("animal");
				String animalName = cs.getObservationTargetLabel(animalId);
				
				// Get litter ID
				int litterId = -1;
				Query<ObservedValue> q = db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, animalId));
				q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, cs.getMeasurementId("Litter")));
				List<ObservedValue> valueList = q.find();
				if (valueList.size() == 1) {
					litterId = valueList.get(0).getRelation_Id();
				} else {
					throw new DatabaseException("Cannot show family info: animal is from no or multiple litters");
				}
				String litterName = cs.getObservationTargetById(litterId).getName();
				
				// Get siblings
				String siblings = "";
				q = db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.TARGET, Operator.NOT, animalId));
				q.addRules(new QueryRule(ObservedValue.RELATION, Operator.EQUALS, litterId));
				q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, cs.getMeasurementId("Litter")));
				valueList = q.find();
				for (ObservedValue value : valueList) {
					siblings += (cs.getObservationTargetLabel(value.getTarget_Id()) + ", ");
				}
				if (siblings.length() > 0) {
					siblings = siblings.substring(0, siblings.length() - 2);
				}
				
				// Get parentgroup
				int parentgroupId = -1;
				q = db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, litterId));
				q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, cs.getMeasurementId("Parentgroup")));
				valueList = q.find();
				if (valueList.size() == 1) {
					parentgroupId = valueList.get(0).getRelation_Id();
				} else {
					if (valueList.size() == 1) {
						throw new DatabaseException("Litter does not have a parentgroup");
					} else {
						throw new DatabaseException("Error: litter has multiple parentgroups");
					}
				}
				String parentgroupName = cs.getObservationTargetById(parentgroupId).getName();
				
				// Get mother(s)
				String mothers = "";
				q = db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.RELATION, Operator.EQUALS, parentgroupId));
				q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, cs.getMeasurementId("Mother")));
				valueList = q.find();
				for (ObservedValue value : valueList) {
					mothers += (cs.getObservationTargetLabel(value.getTarget_Id()) + ", ");
				}
				if (mothers.length() > 0) {
					mothers = mothers.substring(0, mothers.length() - 2);
				}
				
				// Get father(s)
				String fathers = "";
				q = db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.RELATION, Operator.EQUALS, parentgroupId));
				q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, cs.getMeasurementId("Father")));
				valueList = q.find();
				for (ObservedValue value : valueList) {
					fathers += (cs.getObservationTargetLabel(value.getTarget_Id()) + ", ");
				}
				if (fathers.length() > 0) {
					fathers = fathers.substring(0, fathers.length() - 2);
				}
				
				info = "Animal " + animalName +
					" is from litter: " + litterName + "<br />" +
					" which came from parentgroup: " + parentgroupName + "<br />" +
					" with mother(s): " + mothers + "<br />" + 
					" and father(s): " + fathers + "<br />" +
					" and with sibling(s): " + siblings;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null) {
				this.setMessages(new ScreenMessage(e.getMessage(), false));
			}
		}
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getInfo() {
		return info;
	}
}
