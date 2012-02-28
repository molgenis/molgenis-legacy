
package plugins.breedingplugin;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.matrix.component.MatrixViewer;
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
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
	MatrixViewer animalMatrixViewer = null;
	static String ANIMALMATRIX = "animalmatrix";
	private String animalMatrixRendered;

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
	
	public String getAnimalMatrix() {
		if (animalMatrixRendered != null) {
			return animalMatrixRendered;
		}
		return "Error - animal matrix not initialized";
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
		if (animalMatrixViewer != null) {
			animalMatrixViewer.setDatabase(db);
		} else {
			try {
				List<String> investigationNames = cs.getAllUserInvestigationNames(this.getLogin().getUserId());
				List<String> measurementsToShow = new ArrayList<String>();
				measurementsToShow.add("Active");
				measurementsToShow.add("Mother");
				measurementsToShow.add("Father");
				measurementsToShow.add("Parentgroup");
				measurementsToShow.add("Litter");
				measurementsToShow.add("Line");
				measurementsToShow.add("Sex");
				List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
				filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Individual.INVESTIGATION_NAME, 
						Operator.IN, investigationNames));
				filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, 
						cs.getMeasurementId("Active"), ObservedValue.VALUE, Operator.EQUALS,
						"Alive"));
				animalMatrixViewer = new MatrixViewer(this, ANIMALMATRIX, 
						new SliceablePhenoMatrix<Individual, Measurement>(Individual.class, Measurement.class), 
						true, 1, false, false, filterRules, 
						new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN, measurementsToShow));
				animalMatrixViewer.setDatabase(db);
				animalMatrixViewer.setLabel("Choose animal:");
			} catch (Exception e) {
				this.setError("Could not initialize matrix");
			}
		}
		animalMatrixRendered = animalMatrixViewer.render();
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
		cs.setDatabase(db);
		if (animalMatrixViewer != null) {
			animalMatrixViewer.setDatabase(db);
		}
		try
		{
			action = request.getString("__action");
			
			if (action.startsWith(animalMatrixViewer.getName())) {
	    		animalMatrixViewer.handleRequest(db, request);
			}
			
			if (action.equals("reqInfo"))
			{
				// Get animal ID from matrix
				int animalId;
				List<?> rows = animalMatrixViewer.getSelection(db);
				try { 
					int row = request.getInt(ANIMALMATRIX + "_selected");
					animalId = ((ObservationElement) rows.get(row)).getId();
				} catch (Exception e) {
					throw new Exception("No animal selected");
				}
				String animalName = cs.getObservationTargetLabel(animalId);
				
				// Get litter ID
				int litterId = -1;
				Query<ObservedValue> q = db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, animalId));
				q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Litter"));
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
				q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Litter"));
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
				q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Parentgroup"));
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
				q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, parentgroupId));
				q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "ParentgroupMother"));
				valueList = q.find();
				for (ObservedValue value : valueList) {
					mothers += (cs.getObservationTargetLabel(value.getRelation_Id()) + ", ");
				}
				if (mothers.length() > 0) {
					mothers = mothers.substring(0, mothers.length() - 2);
				}
				
				// Get father(s)
				String fathers = "";
				q = db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, parentgroupId));
				q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "ParentgroupFather"));
				valueList = q.find();
				for (ObservedValue value : valueList) {
					fathers += (cs.getObservationTargetLabel(value.getRelation_Id()) + ", ");
				}
				if (fathers.length() > 0) {
					fathers = fathers.substring(0, fathers.length() - 2);
				}
				
				info = "<h2>Animal " + animalName + "</h2>" +
					"is from litter: " + litterName + "<br />" +
					"which came from parentgroup: " + parentgroupName + "<br />" +
					"with mother(s): " + mothers + "<br />" + 
					"and father(s): " + fathers + "<br />" +
					"and with sibling(s): " + siblings;
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
