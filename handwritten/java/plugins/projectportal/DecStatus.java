/* Date:        March 1, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.projectportal;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.GenericPlugin;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.Table;
import org.molgenis.framework.ui.html.TablePanel;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.util.Tuple;

import com.ibm.icu.util.Calendar;
import commonservice.CommonService;

public class DecStatus extends GenericPlugin
{
	private static final long serialVersionUID = -8647856553529155758L;
	
	private TablePanel tablePanel = null;
	private CommonService cq = CommonService.getInstance();
	private Database db = null;
	
	public DecStatus(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	@Override
	public void handleRequest(Database db, Tuple request)
	{
		//replace example below with yours
//		try
//		{
//		Database db = this.getDatabase();
//		String action = request.getString("__action");
//		
//		if( action.equals("do_add") )
//		{
//			Experiment e = new Experiment();
//			e.set(request);
//			db.add(e);
//		}
//		} catch(Exception e)
//		{
//			//e.g. show a message in your form
//		}
	}
	
	public Database getDatabase() {
		return db;
	}
	
	public void setDatabase(Database db) {
		this.db = db;
	}

	@Override
	public void reload(Database db)
	{
		setDatabase(db);
		
		cq.setDatabase(db);
		
		populateTablePanel(db);
	}
	
	/**
	 * Fills the table panel that makes up the UI for this plugin.
	 * 
	 * @param db
	 */
	private void populateTablePanel(Database db) {
		tablePanel = new TablePanel(this.getName() + "panel", null);
		
		Table statusTable = new Table("StatusTable", "");
		statusTable.addColumn("Start");
		statusTable.addColumn("End");
		statusTable.addColumn("DEC subproject");
		statusTable.addColumn("Start");
		statusTable.addColumn("End");
		statusTable.addColumn("Nr. of animals alive");
		statusTable.addColumn("Nr. of animals used");
		statusTable.addColumn("Budget");
		statusTable.addColumn("Percentage used");
		statusTable.addColumn("Status");
		
		try {
			int rowCount = 0;
			List<Panel> decList = cq.getAllMarkedPanels("DecApplication");
			for (Panel decApp : decList) {
				rowCount = addStatusRows(decApp, statusTable, rowCount);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			logger.error("An error occurred while trying to fill status table: " + e.getMessage());
		}
		
		tablePanel.add(statusTable);
	}
	
	/**
	 * For DEC Application 'decApp', adds rows to statusTable with details about itself
	 * and its Subprojects.
	 * 
	 * @param decApp : DEC Application
	 * @param statusTable : Table to add rows to
	 * @param rowCount : Start row
	 * @return Nr. of last row added to the table
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	private int addStatusRows(ObservationTarget decApp, Table statusTable, int rowCount) throws DatabaseException, ParseException {
		
		int rowCountOriginal = rowCount;
		int nrOfAnimalsAliveCum = 0;
		int nrOfAnimalsRemovedCum = 0;
		int nrOfAnimalsTotalCum = 0;
		int budgetCum = 0;
		
		DecimalFormat f = new DecimalFormat("0.##");
		
		int decId = decApp.getId();
		
		int featureId = cq.getMeasurementId("DecNr");
		String decNr = cq.getMostRecentValueAsString(decId, featureId);
		statusTable.addRow(decNr);
		
		featureId = cq.getMeasurementId("StartDate");
		statusTable.setCell(0, rowCount, cq.getMostRecentValueAsString(decId, featureId));
		
		featureId = cq.getMeasurementId("EndDate");
		statusTable.setCell(1, rowCount, cq.getMostRecentValueAsString(decId, featureId));
		
		List<Panel> expList = cq.getAllMarkedPanels("Experiment");
		for (Panel subproject : expList) {
			int subprojectId = subproject.getId();
			
			// Take only Subprojects that belong to the current DEC
			featureId = cq.getMeasurementId("DecApplication");
			int decApplicationId = cq.getMostRecentValueAsXref(subprojectId, featureId);
			if (decApplicationId == decId) {
				
				rowCount++;
				statusTable.addRow("");
				
				featureId = cq.getMeasurementId("ExperimentNr");
				statusTable.setCell(2, rowCount, cq.getMostRecentValueAsString(subprojectId, featureId));
				
				featureId = cq.getMeasurementId("StartDate");
				statusTable.setCell(3, rowCount, cq.getMostRecentValueAsString(subprojectId, featureId));
				
				featureId = cq.getMeasurementId("EndDate");
				statusTable.setCell(4, rowCount, cq.getMostRecentValueAsString(subprojectId, featureId));
				
				// Calculate numbers of animals alive/used/total
				int nrOfAnimalsAlive = 0;
				int nrOfAnimalsRemoved = 0;
				int nrOfAnimalsTotal = 0;
				int budget = 100; // TODO: use real budget
				budgetCum += budget;
				Date now = Calendar.getInstance().getTime();
				featureId = cq.getMeasurementId("Experiment");
				List<Integer> aliveAnimalIdList = cq.getAllObservationTargetIds("Individual", true);
				if (aliveAnimalIdList.size() > 0) {
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.RELATION, Operator.EQUALS, subprojectId));
					q.addRules(new QueryRule(ObservedValue.TARGET, Operator.IN, aliveAnimalIdList));
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureId));
					q.addRules(new QueryRule(ObservedValue.TIME, Operator.LESS_EQUAL, now));
					q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
					nrOfAnimalsAlive = q.count();
				}
				nrOfAnimalsAliveCum += nrOfAnimalsAlive;
				List<Integer> totalAnimalIdList = cq.getAllObservationTargetIds("Individual", false);
				if (totalAnimalIdList.size() > 0) {
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.RELATION, Operator.EQUALS, subprojectId));
					q.addRules(new QueryRule(ObservedValue.TARGET, Operator.IN, totalAnimalIdList));
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureId));
					nrOfAnimalsTotal = q.count();
				}
				nrOfAnimalsTotalCum += nrOfAnimalsTotal;
				nrOfAnimalsRemoved = nrOfAnimalsTotal - nrOfAnimalsAlive;
				nrOfAnimalsRemovedCum += nrOfAnimalsRemoved;
				
				statusTable.setCell(5, rowCount, nrOfAnimalsAlive);
				statusTable.setCell(6, rowCount, nrOfAnimalsRemoved);
				statusTable.setCell(7, rowCount, budget);
				double perc = budget > 0 ? (nrOfAnimalsAlive + nrOfAnimalsRemoved) * 100.0 / budget : 0.0;
				statusTable.setCell(8, rowCount, f.format(perc));
				statusTable.setCell(9, rowCount, "&nbsp;");
				statusTable.setCellStyle(9, rowCount, "border: 1px solid black; background-color: green");
			}
		}
		
		// Set cells with cumulative values
		statusTable.setCell(5, rowCountOriginal, nrOfAnimalsAliveCum);
		statusTable.setCell(6, rowCountOriginal, nrOfAnimalsRemovedCum);
		statusTable.setCell(7, rowCountOriginal, budgetCum);
		double perc = budgetCum > 0 ? (nrOfAnimalsAliveCum + nrOfAnimalsRemovedCum) * 100.0 / budgetCum : 0.0;
		statusTable.setCell(8, rowCountOriginal, f.format(perc));
		statusTable.setCell(9, rowCountOriginal, "&nbsp;");
		statusTable.setCellStyle(9, rowCountOriginal, "border: 1px solid black; background-color: green");
		
		return rowCount + 1;
	}

	public String render()
	{
	   return this.tablePanel.toHtml();
	}
}
