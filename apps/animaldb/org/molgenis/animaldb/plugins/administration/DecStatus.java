/* Date:        March 1, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.administration;

import java.text.DecimalFormat;
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
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.JQueryDataTable;
import org.molgenis.framework.ui.html.Paragraph;
import org.molgenis.framework.ui.html.Table;
import org.molgenis.framework.ui.html.TablePanel;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Tuple;


public class DecStatus extends EasyPluginController
{
	private static final long serialVersionUID = -8647856553529155758L;
	
	private TablePanel tablePanel = null;
	private CommonService cq = CommonService.getInstance();
	private Database db = null;
	
	public DecStatus(String name, ScreenController<?> parent)
	{
		super(name, parent);
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
		
		tablePanel.add(new Paragraph("<h2>Active DECs and their subprojects</h2>"));
		
		Table statusTable = new JQueryDataTable("StatusTable", "");
		statusTable.addColumn("DEC");
		statusTable.addColumn("Start");
		statusTable.addColumn("End");
		statusTable.addColumn("Subproject");
		statusTable.addColumn("Start");
		statusTable.addColumn("End");
		statusTable.addColumn("Nr. of animals alive");
		statusTable.addColumn("Nr. of animals used");
		statusTable.addColumn("Budget");
		statusTable.addColumn("Percentage used");
		statusTable.addColumn("Status");
		
		try {
			List<String> investigationNames = cq.getAllUserInvestigationNames(db.getLogin().getUserName());
			int rowCount = 0;
			List<ObservationTarget> decList = cq.getAllMarkedPanels("DecApplication", investigationNames);
			List<ObservationTarget> expList = cq.getAllMarkedPanels("Experiment", investigationNames);
			List<String> aliveAnimalNameList = cq.getAllObservationTargetNames("Individual", true, 
					investigationNames);
			List<String> totalAnimalNameList = cq.getAllObservationTargetNames("Individual", false, 
					investigationNames);
			for (ObservationTarget decApp : decList) {
				rowCount = addStatusRows(decApp, statusTable, rowCount, expList, 
						aliveAnimalNameList, totalAnimalNameList);
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
	private int addStatusRows(ObservationTarget decApp, Table statusTable, int rowCount, 
			List<ObservationTarget> expList, List<String> aliveAnimalNameList,
			List<String> totalAnimalNameList) throws DatabaseException, ParseException {
		
		int nrOfAnimalsAliveCum = 0;
		int nrOfAnimalsRemovedCum = 0;
		int budgetCum = 0;
		DecimalFormat f = new DecimalFormat("0.##");
		String decName = decApp.getName();
		
		// First check: bail out if DEC no longer active
		SimpleDateFormat newDateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		String endDateString = cq.getMostRecentValueAsString(decName, "EndDate");
		Date endDate = newDateOnlyFormat.parse(endDateString);
		Date now = new Date();
		if (endDate.before(now)) {
			return rowCount;
		}
		
		statusTable.addRow("" + (rowCount + 1));
		statusTable.setCell(0, rowCount, cq.getMostRecentValueAsString(decName, "DecNr"));
		statusTable.setCell(1, rowCount, cq.getMostRecentValueAsString(decName, "StartDate"));
		statusTable.setCell(2, rowCount, endDateString);
		
		List<ObservationTarget> expInDecList = new ArrayList<ObservationTarget>();
		int extraRow = 1;
		for (ObservationTarget subproject : expList) {
			// Take only Subprojects that belong to the current DEC
			String decApplicationName = cq.getMostRecentValueAsXrefName(subproject.getName(), "DecApplication");
			if (decApplicationName.equals(decName)) {
				expInDecList.add(subproject);
				statusTable.addRow("" + (rowCount + 1 + extraRow));
				extraRow++;
			}
		}
		
		for (ObservationTarget subproject : expInDecList) {
			String subprojectName = subproject.getName();
			String subProjectCode = cq.getMostRecentValueAsString(subprojectName, "ExperimentNr");
			char codeA = 'A';
			char code = subProjectCode.toCharArray()[0];
			int expRow = 1 + code - codeA;
			if (expRow > expInDecList.size()) {
				expRow = expInDecList.size();
			}
			
			statusTable.setCell(3, rowCount + expRow, subProjectCode);
			statusTable.setCell(4, rowCount + expRow, cq.getMostRecentValueAsString(subprojectName, "StartDate"));
			statusTable.setCell(5, rowCount + expRow, cq.getMostRecentValueAsString(subprojectName, "EndDate"));
			
			// Calculate numbers of animals alive/used/total
			int nrOfAnimalsAlive = 0;
			int nrOfAnimalsRemoved = 0;
			int nrOfAnimalsTotal = 0;
			//int budget = 100; // TODO: use real budget
			Integer budget = Integer.decode(cq.getMostRecentValueAsString(subprojectName, "DecSubprojectBudget"));
			budgetCum += budget;
			java.sql.Date nowDb = new java.sql.Date(new Date().getTime());
			if (aliveAnimalNameList.size() > 0) {
				Query<ObservedValue> q = db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.RELATION_NAME, Operator.EQUALS, subprojectName));
				q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.IN, aliveAnimalNameList));
				q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Experiment"));
				q.addRules(new QueryRule(ObservedValue.TIME, Operator.LESS_EQUAL, nowDb));
				q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
				nrOfAnimalsAlive = q.count();
			}
			nrOfAnimalsAliveCum += nrOfAnimalsAlive;
			if (totalAnimalNameList.size() > 0) {
				Query<ObservedValue> q = db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.RELATION_NAME, Operator.EQUALS, subprojectName));
				q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.IN, totalAnimalNameList));
				q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Experiment"));
				nrOfAnimalsTotal = q.count();
			}
			nrOfAnimalsRemoved = nrOfAnimalsTotal - nrOfAnimalsAlive;
			nrOfAnimalsRemovedCum += nrOfAnimalsRemoved;
			
			statusTable.setCell(6, rowCount + expRow, nrOfAnimalsAlive);
			statusTable.setCell(7, rowCount + expRow, nrOfAnimalsRemoved);
			statusTable.setCell(8, rowCount + expRow, budget);
			double perc = budget > 0 ? (nrOfAnimalsAlive + nrOfAnimalsRemoved) * 100.0 / budget : 0.0;
			statusTable.setCell(9, rowCount + expRow, f.format(perc));
			statusTable.setCell(10, rowCount + expRow, "&nbsp;");
			statusTable.setCellStyle(10, rowCount + expRow, "border: 1px solid black; background-color: green");
		}
		
		// Set cells with cumulative values
		statusTable.setCell(6, rowCount, nrOfAnimalsAliveCum);
		statusTable.setCell(7, rowCount, nrOfAnimalsRemovedCum);
		statusTable.setCell(8, rowCount, budgetCum);
		double perc = budgetCum > 0 ? (nrOfAnimalsAliveCum + nrOfAnimalsRemovedCum) * 100.0 / budgetCum : 0.0;
		statusTable.setCell(9, rowCount, f.format(perc));
		statusTable.setCell(10, rowCount, "&nbsp;");
		statusTable.setCellStyle(10, rowCount, "border: 1px solid black; background-color: green");
		
		return rowCount + expInDecList.size() + 1;
	}

	public ScreenView getView()
	{
	  return this.tablePanel;
	}
}
