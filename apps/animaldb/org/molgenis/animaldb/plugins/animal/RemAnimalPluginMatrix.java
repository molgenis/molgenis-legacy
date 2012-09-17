/* Date:        September 14, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.animal;

import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.DateInput;
import org.molgenis.framework.ui.html.DivPanel;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.matrix.component.MatrixViewer;
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Tuple;


public class RemAnimalPluginMatrix extends EasyPluginController
{
	private static final long serialVersionUID = 6730055654508843657L;
	MatrixViewer targetMatrixViewer = null;
	static String TARGETMATRIX = "targetmatrix";
	private Container container = null;
	private DivPanel div = null;
	private CommonService cs = CommonService.getInstance();
	private List<Integer> targetList = null;
	//private SimpleDateFormat newDateOnlyFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
	private SimpleDateFormat newDateOnlyDbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	private SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

	public RemAnimalPluginMatrix(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
    public Show handleRequest(Database db, Tuple request, OutputStream out)
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
				deathdate.setNillable(false);
				deathdate.setDescription("The date at which these animals died.");
				deathdate.setDateFormat("yyyy-MM-dd");
				deathdate.setJqueryproperties("dateFormat: 'yy-mm-dd', changeMonth: true, changeYear: true, showButtonPanel: true, numberOfMonths: 1");
				//deathdate.setValue(new Date());
				div.add(deathdate);				
				
				// show a Remarks field
				StringInput remarks = new StringInput("remarks");
				remarks.setLabel("Remarks:");
				remarks.setNillable(true);
				div.add(remarks);
				
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
				Date deathDate = newDateOnlyDbFormat.parse(deathDateString);
				String deathDateStr = newDateOnlyDbFormat.format(deathDate);
				
				
				String investigationName = cs.getOwnUserInvestigationName(db.getLogin().getUserName());
				String notRemoved = "";
				String removed = "";
				for (Integer animalId : targetList) {
					// add animals to stringlist for report
					String animalName = cs.getObservationTargetLabel(animalId);
					if (inExperiment(db, animalName, deathDate)) {
						notRemoved += animalName + " ";
						continue;
					}
					removed += animalName + " ";
					// Set 'Removal' feature
					db.add(cs.createObservedValueWithProtocolApplication(investigationName, deathDate, 
							null, "SetRemoval", "Removal", animalName, removal, null));
					// Report as dead/removed by setting the endtime of the Active value
					Query<ObservedValue> activeQuery = db.query(ObservedValue.class);
					activeQuery.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, animalName));
					activeQuery.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Active"));
					List<ObservedValue> activeValueList = activeQuery.find();
					if (activeValueList.size() == 1) {
						ObservedValue activeValue = activeValueList.get(0);
						activeValue.setEndtime(deathDate);
						activeValue.setValue("Dead");
						db.update(activeValue);
					}
					// If applicable, set Death date
					if (removal.equals("dood")) {
						db.add(cs.createObservedValueWithProtocolApplication(investigationName, 
								deathDate, null, "SetDeathDate", "DeathDate", animalName, 
								deathDateStr, null));
					}
					// Set remark
					if (request.getString("remarks") != null) {
						db.add(cs.createObservedValueWithProtocolApplication(investigationName, deathDate, null, 
								"SetRemark", "Remark", animalName, request.getString("remarks"), null));
					}
				}
				
				if (!removed.equals("")) {
					this.getMessages().add(new ScreenMessage("Animal(s) " + removed +  "successfully removed", true));
				}
				if (!notRemoved.equals("")) {
					this.getMessages().add(new ScreenMessage("Animal(s) " + notRemoved + "not removed because they are still in a DEC subproject - remove them using the 'DEC subprojects' screen", false));
				}
				
				container = null; // force refresh
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			this.getMessages().add(new ScreenMessage("Something went wrong while handling request: " + e.getMessage(), false));
		}
		
		return Show.SHOW_MAIN;
    }
	
	@Override
    public void reload(Database db)
    {
		cs.setDatabase(db);
		
		if (container == null) {
			container = new Container();
			div = new DivPanel();
			try {
				List<String> investigationNames = cs.getAllUserInvestigationNames(db.getLogin().getUserName());
				List<String> measurementsToShow = new ArrayList<String>();
				measurementsToShow.add("Active");
				measurementsToShow.add("Location");
				measurementsToShow.add("Experiment");
				measurementsToShow.add("Sex");
				measurementsToShow.add("Species");
				measurementsToShow.add("Line");
				List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
				filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Individual.INVESTIGATION_NAME, 
						Operator.IN, investigationNames));
				filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, 
						cs.getMeasurementId("Active"), ObservedValue.VALUE, Operator.EQUALS,
						"Alive"));
				targetMatrixViewer = new MatrixViewer(this, TARGETMATRIX, 
						new SliceablePhenoMatrix<Individual, Measurement>(Individual.class, Measurement.class), 
						true, 2, false, false, filterRules, 
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
	
	public ScreenView getView()
    {
		MolgenisForm view = new MolgenisForm(this);
    	view.add(container);
    	return view;
    }
	
	private boolean inExperiment(Database db, String animalName, Date deathDate) throws DatabaseException, ParseException {
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, animalName));
		q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Experiment"));
		q.addRules(new QueryRule(ObservedValue.TIME, Operator.LESS_EQUAL, dbFormat.format(deathDate)));
		q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
		if (q.find().size() == 1) {
			return true;
		} else {
			return false;
		}
	}
}
