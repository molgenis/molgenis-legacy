/* Date:        September 14, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.animal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.GenericPlugin;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.DateInput;
import org.molgenis.framework.ui.html.DivPanel;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.matrix.component.MatrixViewer;
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class RemAnimalPluginMatrix extends GenericPlugin
{
	private static final long serialVersionUID = 6730055654508843657L;
	MatrixViewer targetMatrixViewer = null;
	static String TARGETMATRIX = "targetmatrix";
	private Container container = null;
	private DivPanel div = null;
	private CommonService cs = CommonService.getInstance();
	private List<Integer> targetList = null;
	private SimpleDateFormat newDateOnlyFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
	private SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

	public RemAnimalPluginMatrix(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
    public void handleRequest(Database db, Tuple request)
    {
		cs.setDatabase(db);
		if (targetMatrixViewer != null) {
			targetMatrixViewer.setDatabase(db);
		}
		
		String action = request.getAction();
		
		try {
			if (action.startsWith(targetMatrixViewer.getName())) {
	    		targetMatrixViewer.handleRequest(db, request);
			}
			
			if (action.equals("Select")) {
				targetList = new ArrayList<Integer>();
				// Get targets from matrix
				@SuppressWarnings("unchecked")
				List<ObservationElement> rows = (List<ObservationElement>) targetMatrixViewer.getSelection(db);
				int rowCnt = 0;
				for (ObservationElement row : rows) {
					if (request.getBool(TARGETMATRIX + "_selected_" + rowCnt) != null) {
						targetList.add(row.getId());
					}
					rowCnt++;
				}
				
				container = new Container();
				div = new DivPanel();
				// show select box for removal type
				SelectInput removal = new SelectInput("removal");
				removal.setLabel("Type of removal:");
				for (Category c : cs.getAllCodesForFeature("Removal")) {
					removal.addOption(c.getDescription(), c.getDescription());
				}
				removal.setNillable(false);
				div.add(removal);
				// show date input for removal date
				DateInput deathdate = new DateInput("deathdate");
				deathdate.setLabel("Date of removal:");
				deathdate.setValue(new Date());
				deathdate.setNillable(false);
				div.add(deathdate);
				// show apply button
				ActionInput applyButton = new ActionInput("Apply", "", "Apply");
				div.add(applyButton);
				
				container.add(div);
			}
			
			if (action.equals("Apply")) {
				// Get kind of removal
				String removal = request.getString("removal");
				if (removal == null) {
					throw new Exception("No kind of removal set - animals not terminated");
				}
				// Get date of removal
				String deathDateString = request.getString("deathdate");
				if (deathDateString == null) {
					throw new Exception("No date of removal set - animals not terminated");
				}
				Date deathDate = newDateOnlyFormat.parse(deathDateString);
				
				int investigationId = cs.getOwnUserInvestigationId(this.getLogin().getUserId());
				String notRemoved = "";
				for (Integer animalId : targetList) {
					
					if (inExperiment(db, animalId, deathDate)) {
						notRemoved += animalId + " ";
						continue;
					}
					
					// Set 'Removal' feature
					int protocolId = cs.getProtocolId("SetRemoval");
					int measurementId = cs.getMeasurementId("Removal");
					db.add(cs.createObservedValueWithProtocolApplication(investigationId, deathDate, 
							null, protocolId, measurementId, animalId, removal, 0));
					
					// Report as dead/removed by setting the endtime of the Active value
					measurementId = cs.getMeasurementId("Active");
					Query<ObservedValue> activeQuery = db.query(ObservedValue.class);
					activeQuery.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, animalId));
					activeQuery.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, measurementId));
					List<ObservedValue> activeValueList = activeQuery.find();
					if (activeValueList.size() == 1) {
						ObservedValue activeValue = activeValueList.get(0);
						activeValue.setEndtime(deathDate);
						activeValue.setValue("Dead");
						db.update(activeValue);
					}
					
					// If applicable, set Death date
					if (removal.equals("dood")) {
						protocolId = cs.getProtocolId("SetDeathDate");
						measurementId = cs.getMeasurementId("DeathDate");
						db.add(cs.createObservedValueWithProtocolApplication(investigationId, 
								deathDate, null, protocolId, measurementId, animalId, 
								newDateOnlyFormat.format(deathDate), 0));
					}
				}
				
				String message = "Animal(s) successfully removed";
				if (!notRemoved.equals("")) {
					message += "; animal(s) " + notRemoved + "not removed because they are still in a DEC subproject";
				}
				this.getMessages().add(new ScreenMessage(message, true));
				
				container = null;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			this.getMessages().add(new ScreenMessage("Something went wrong while handling request: " + e.getMessage(), false));
		}
    }
	
	@Override
    public void reload(Database db)
    {
		cs.setDatabase(db);
		
		if (container == null) {
			container = new Container();
			div = new DivPanel();
			try {
				List<String> investigationNames = cs.getAllUserInvestigationNames(this.getLogin().getUserId());
				List<String> measurementsToShow = new ArrayList<String>();
				measurementsToShow.add("Active");
				measurementsToShow.add("Location");
				measurementsToShow.add("Experiment");
				List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
				filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Individual.INVESTIGATION_NAME, 
						Operator.IN, investigationNames));
				filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, 
						cs.getMeasurementId("Active"), ObservedValue.VALUE, Operator.EQUALS,
						"Alive"));
				targetMatrixViewer = new MatrixViewer(this, TARGETMATRIX, 
						new SliceablePhenoMatrix<Individual, Measurement>(Individual.class, Measurement.class), 
						true, true, false, false, filterRules, 
						new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN, measurementsToShow));
				targetMatrixViewer.setDatabase(db);
				targetMatrixViewer.setLabel("Choose animals:");
				div.add(targetMatrixViewer);
				
				ActionInput selectButton = new ActionInput("Select", "", "Select");
				div.add(selectButton);
				
				container.add(div);
			} catch(Exception e) {
				e.printStackTrace();
				this.getMessages().add(new ScreenMessage("Something went wrong while loading matrix: " + e.getMessage(), false));
			}
		} else {
			targetMatrixViewer.setDatabase(db);
		}
    }
	
	public String render()
    {
    	return container.toHtml();
    }
	
	private boolean inExperiment(Database db, int animalId, Date deathDate) throws DatabaseException, ParseException {
		int featureId = cs.getMeasurementId("Experiment");
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, animalId));
		q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureId));
		q.addRules(new QueryRule(ObservedValue.TIME, Operator.LESS_EQUAL, dbFormat.format(deathDate)));
		q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
		if (q.find().size() == 1) {
			return true;
		} else {
			return false;
		}
	}
}
