package org.molgenis.protocol;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.molgenis.MolgenisFieldTypes;
import org.molgenis.batch.MolgenisBatch;
import org.molgenis.fieldtypes.UnknownField;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.CheckboxInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.DatetimeInput;
import org.molgenis.framework.ui.html.DivPanel;
import org.molgenis.framework.ui.html.HorizontalRuler;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.HtmlInputException;
import org.molgenis.framework.ui.html.OptionInput;
import org.molgenis.framework.ui.html.RadioInput;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.SelectMultipleInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.framework.ui.html.Table;
import org.molgenis.framework.ui.html.Paragraph;
import org.molgenis.framework.ui.html.XrefInput;
import org.molgenis.matrix.component.MatrixViewer;
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.ValueLabel;

import commonservice.CommonService;

public class ApplyProtocolUI {
    
    private Container protocolApplicationContainer = null;
    private DivPanel protocolDiv;
    private DivPanel tableDiv;
    private Table valueTable;
    private SelectInput protocols;
    MatrixViewer targetMatrixViewer = null;
	static String TARGETMATRIX = "targetmatrix";
    private SelectMultipleInput batches;
    private OptionInput newOrEditButtons;
    private CheckboxInput timeBox;
    private CheckboxInput allValuesBox;
    
    private ApplyProtocolPluginModel model;
    private ApplyProtocolService service;
    
    private static transient Logger logger = Logger.getLogger(ApplyProtocolUI.class);

    public ApplyProtocolUI(ApplyProtocolPluginModel model) {
		this.model = model;
    }
    
    public void setService(ApplyProtocolService service) {
    	this.service = service;
    }
    
    public void initScreen(Database db, ScreenController plugin, int userId, String userName) throws Exception {
    	model.setNewProtocolApplication(false);
    	model.setTimeInfo(false);
		protocolApplicationContainer = new Container();
		protocolDiv = new DivPanel("ProtocolPanel", null);
		tableDiv = new DivPanel("TablePanel", null);
		makeProtocolSelect();
		makeTargetsMatrix(db, plugin, userId);
		makeBatchSelect();
		makeNewOrEditButtons(userName);
		makeTimeSelectbox();
		makeAllValuesSelectbox();
		makeSelectButton();
		makeClearButton();
		fillContainer();
    }
    
	public Container getProtocolApplicationContainer() {
    	return this.protocolApplicationContainer;
    }
    
    /**
     * Puts an input box with value 'value' into the table at position col, row
     * 
     * @param col column to insert the input in
     * @param row row to insert the input in
     * @param order in case of multiple HtmlInputs for one target-feature combination, append this number
     * to keep the input name unique
     * @param value the value to insert
     * @throws Exception 
     */
    private HtmlInput makeInput(int featureNr, int col, int row, int order, ObservedValue value) throws Exception {
    	
		HtmlInput valueInput;
		Measurement feature = model.getFeaturesList().get(featureNr);
	
		// Get the metadata to create the input
		
		// Data type
		String dataType = feature.getDataType();
		if( MolgenisFieldTypes.getType(dataType) instanceof UnknownField ) {
		    throw new Exception("Fieldtype " + dataType + "' is unknown in MOLGENIS");
		}
		
		// Target type allowed for relation
		String observationTargetType = model.getTargettypeAllowedForRelation(feature);
		
		// Panel label for relation
		String panelLabel = feature.getPanelLabelAllowedForRelation();
		
		// Make the appropriate input
		if (dataType.equals("string") && model.getAllCodesForFeature(feature).size() > 0) {
		    // If there are codes for this Measurement, show a selectbox with those
		    valueInput = new SelectInput(col + "_" + row + "_" + order);
		    ((SelectInput)valueInput).setOptionsFromStringList(model.getAllCodesForFeatureAsStrings(feature));
		} else {
			if (panelLabel != null) {
				// If there's only a subset of labeled Panels allowed for this Measurement, show a selectbox with those
				valueInput = new SelectInput(col + "_" + row + "_" + order);
				List<ObservationTarget> panelList = model.getAllPanelsForFeature(feature);
				for (ObservationTarget p : panelList) {
					((SelectInput)valueInput).addOption(p.getName(), p.getName());
				}
			} else {
				// Normally, show the input belonging to the data type
				valueInput = MolgenisFieldTypes.createInput(dataType, col + "_" + row + "_" + order, 
					observationTargetType);
				if (dataType.equals("string")) {
					((StringInput)valueInput).setWidth(20);
				}
			}
		}
	
		// If present, set the value of the input
		if (value != null) {
		    if (value.getValue() != null) {
		    	// If there's a literal string value, use that...
		    	valueInput.setValue(value.getValue());
		    } else if (value.getRelation_Name() != null) {
		    	// Otherwise it must be a relation, so use the xref id as a value...
		    	valueInput.setValue(value.getRelation_Name());
		    	if (panelLabel == null) { // If a panel label was set, valueInput has been turned into a selectbox and we cannot do the statement below
			    	// Because this involves an xref box, set the value and label of the selected option
		    		throw new RuntimeException("I don't understand the next code line. (Morris). In the new XrefInput it works slightly different...");
			    	//((XrefInput) valueInput).setValueLabel("name", value.getRelation_Name());
		    	}
		    }
		}
	
		return valueInput;

    }
    
    /**
     * Makes the appropriate input for the cell at col, row and adds it to the valueTable.
     * At the moment, supports only cells which contain one input.
     * 
     * @param featureNr
     * @param col
     * @param row
     * @param order
     * @param value
     * @throws Exception
     */
	public void makeInputAndSetCell(int featureNr, int col, int row, int order, ObservedValue value) throws Exception {
		HtmlInput input = makeInput(featureNr, col, row, order, value);
		valueTable.setCell(col, row, input);
	}
	
	/**
	 * Makes the appropriate date input for the cell at col, row and adds it to the valueTable.
     * At the moment, supports only cells which contain one input.
     * 
	 * @param col
	 * @param row
	 * @param order
	 * @param theTime
	 */
	public void makeDateInputAndSetCell(int col, int row, int order, Date theTime) {
		DatetimeInput input = new DatetimeInput(col + "_" + row + "_" + order, theTime);
		valueTable.setCell(col, row, input);
	}

    /** Fill the container
     * 
     */
    public void fillContainer() {
    	protocolApplicationContainer.add(protocolDiv);
    }
    
    /**
     * 
     */
    public void makeProtocolSelect() {
		try {
		    protocols = new SelectInput("Protocols");
		    protocols.setLabel("Choose protocol:");
		    protocols.setOptions(service.getAllProtocolsSorted(Protocol.NAME, "ASC", model.getInvestigationIds()), 
		    		Protocol.ID, Protocol.NAME);
		    protocolDiv.add(protocols);
		    protocolDiv.add(new HorizontalRuler());
		} catch(Exception e) {
			e.printStackTrace();
		    logger.error("An error occurred while retrieving protocols from the database", e);
		}
    }

    /** Create a select box with ObservationTargets grabbed from the database
     * 
     */
//    public void makeTargetsSelect() {
//		try {
//		    targets = new SelectMultipleInput("Targets", null);
//		    targets.setLabel("Choose Targets:");
//		    List<Integer> investigationIds = service.getWritableUserInvestigationIds(model.getUserId());
//		    for (ObservationTarget o : service.getAllObservationTargets(investigationIds)) {
//		    	targets.addOption(o.getId(), service.getObservationTargetById(o.getId()).getName());
//		    }
//		    protocolDiv.add(targets);
//	
//		} catch(Exception e) {
//			e.printStackTrace();
//		    logger.error("An error occurred while retrieving targets from the database", e);
//		}
//    }
    
    public void makeTargetsMatrix(Database db, ScreenController plugin, int userId) throws Exception {
    	
    	List<String> investigationNames = service.getAllUserInvestigationNames(userId);
		List<String> measurementsToShow = new ArrayList<String>();
		measurementsToShow.add("Species");
		measurementsToShow.add("Sex");
		measurementsToShow.add("Active");
		List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
		filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Individual.INVESTIGATION_NAME, 
				Operator.IN, investigationNames));
		targetMatrixViewer = new MatrixViewer(plugin, TARGETMATRIX, 
				new SliceablePhenoMatrix(Individual.class, Measurement.class), 
				true, true, filterRules, new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, 
						Operator.IN, measurementsToShow));
		targetMatrixViewer.setDatabase(db);
		targetMatrixViewer.setLabel("Choose animals:");
		protocolDiv.add(targetMatrixViewer);
		protocolDiv.add(new HorizontalRuler());
    }
    
    /**
     * Create a select box with Batches grabbed from the database
     */
    public void makeBatchSelect() {
		try {
		    batches = new SelectMultipleInput("Batches", null);
		    batches.setLabel("Choose batches:");
		    for (MolgenisBatch o : service.getAllBatches()) {
		    	batches.addOption(o.getId(), o.getName());
		    }
		    protocolDiv.add(batches);
		    protocolDiv.add(new HorizontalRuler());
		} catch(Exception e) {
			e.printStackTrace();
		    logger.error("An error occurred while retrieving batches from the database", e);
		}
    }
    
    /**
     * Create radio buttons to select the way to apply the protocol.
     * @throws HtmlInputException 
     */
    private void makeNewOrEditButtons(String userName) throws HtmlInputException {
    	List<String> options = new ArrayList<String>();
    	options.add("New");
    	options.add("Edit");
    	List<String> optionLabels = new ArrayList<String>();
    	optionLabels.add("Make new values");
    	optionLabels.add("Edit existing values");
    	
    	newOrEditButtons = new RadioInput("NewOrEdit", "", "Edit", false, false,
				"Indicate whether you want to fill in new values or edit existing ones.", options, optionLabels);
    	
    	if (!userName.equals("admin")) {
    		newOrEditButtons.setHidden(true);
    	}
    	
		protocolDiv.add(newOrEditButtons);
	}
    
    /**
     * Create a checkbox to toggle time fields with the values.
     */
    private void makeTimeSelectbox() {
    	Vector<ValueLabel> options = new Vector<ValueLabel>();
    	options.add(new ValueLabel("Time", "Show date-time fields with values"));
    	timeBox = new CheckboxInput("TimeBox", "", 
    			"Indicate whether you want date-time fields next to the values", options, null);
    	protocolDiv.add(new Paragraph("", "")); // gives empty <p></p>
		protocolDiv.add(timeBox);
    }
    
    /**
     * Create a checkbox to toggle showing all values, not only the most recent one(s).
     */
    private void makeAllValuesSelectbox() {
    	Vector<ValueLabel> options = new Vector<ValueLabel>();
    	options.add(new ValueLabel("AllValues", 
    			"Show not only most recent but all values (works only with 'Edit existing values' and " +
    			"disables defaults)"));
    	allValuesBox = new CheckboxInput("AllValuesBox", "", 
    			"Indicate whether you want to see all values for every target-measurement combination", 
    			options, null);
    	protocolDiv.add(new Paragraph("", "")); // gives empty <p></p>
		protocolDiv.add(allValuesBox);
    }

    /**
     * 
     */
    public void makeSelectButton() {
		ActionInput selectButton = new ActionInput("Select", "", "Select");
		protocolDiv.add(selectButton);
    }

    /** Create a button to clear selections
     * 
     */
    public void makeClearButton() {
		ActionInput clearButton = new ActionInput("Clear", "", "Reset");
		protocolDiv.add(clearButton);
    }

    /** Create an 'apply' button
     * 
     */
    public void makeApplyButton() {
		ActionInput applyButton = new ActionInput("Apply", "", "Apply Protocol");
		tableDiv.add(applyButton);
    }

    /** Create a button to apply defaults to the table
     * 
     */
    public void makeApplyAllDefaultsButton() {
		ActionInput applyDefaultsButton = new ActionInput("ApplyAllDefaults", "", "Set All Defaults");
		tableDiv.add(applyDefaultsButton);
    }

    /** Make a table given a protocolId (whose feature to use) and a targetlist
     * 
     * @param protocolId
     * @param targetList
     */
    public void makeTable() {
		try {
		    valueTable = new Table("ValueTable", "");
		    makeColumns();
		    makeRows();
		    fillTableCells();
		    tableDiv.add(valueTable);
		} catch (Exception e) {
		    logger.error(e.getMessage());
		    e.printStackTrace();
		} 
    }

    /** Create columns for a given protocol id, with the columns being the features for that protocol
     * 
     * @param protocolId the protocol whose features we want to use
     * @throws DatabaseException
     * @throws ParseException
     */
    public void makeColumns() throws DatabaseException, ParseException {
		for (Measurement m : model.getFeaturesList()) {
			String measurementName = m.getName();
		    valueTable.addColumn(measurementName);
		    if (model.isTimeInfo()) {
		    	 valueTable.addColumn(measurementName + " start");
		    	 valueTable.addColumn(measurementName + " end");
		    }
		}
    }

    /** Create rows for the selected targets 
     * 
     * @throws DatabaseException
     * @throws ParseException
     */
    public void makeRows() throws DatabaseException, ParseException {
		valueTable.addRow("Defaults:");
		model.getTargetsIdList().clear();
		for (String o : model.getFullTargetList()) {
		    Integer targetId = Integer.parseInt(o);
		    model.getTargetsIdList().add(targetId);
		    valueTable.addRow(service.getObservationTargetById(targetId).getName());
		}
    }
    
    /** Fill all table cells for the number of columns and rows
     * 
     * @param valueTable
     * @throws DatabaseException
     * @throws ParseException
     */
    public void fillTableCells() throws DatabaseException, ParseException {
		try {
			int sizeFeatures = model.getFeaturesList().size();
			 
			if (model.isAllValues() == false) {
			    DivPanel div;
			    // First row contains default input boxes
			    for (int col = 0; col < sizeFeatures; col++) {
			    	int colNrInTable = col;
					if (model.isTimeInfo()) {
						colNrInTable *= 3;
					}
					div = new DivPanel();
					HtmlInput input = makeInput(col, colNrInTable, 0, 0, null);
					input.setLabel("");
					div.add(input);
					ActionInput applyButton2 = new ActionInput("ApplyDefault_" + colNrInTable, "", "Set");
					div.add(applyButton2);
					// Put the input in the right place in the table
					if (model.isTimeInfo()) {
						Date now = Calendar.getInstance().getTime();
						
						// Make div with default start date-time
						DivPanel div2 = new DivPanel();
						DatetimeInput datetimeInputStart = new DatetimeInput((colNrInTable + 1) + "_0_0", now);
						datetimeInputStart.setLabel("");
						div2.add(datetimeInputStart);
						ActionInput applyButtonStartTime = new ActionInput("ApplyStartTime_" + (colNrInTable + 1), "", "Set");
						div2.add(applyButtonStartTime);
						valueTable.setCell(colNrInTable + 1, 0, div2);
						
						// Make div with default end date-time
						DivPanel div3 = new DivPanel();
						DatetimeInput datetimeInputEnd = new DatetimeInput((colNrInTable + 2) + "_0_0", now);
						datetimeInputEnd.setLabel("");
						div3.add(datetimeInputEnd);
						ActionInput applyButtonEndTime = new ActionInput("ApplyEndTime_" + (colNrInTable + 2), "", "Set");
						div3.add(applyButtonEndTime);
						valueTable.setCell(colNrInTable + 2, 0, div3);
					}
					valueTable.setCell(colNrInTable, 0, div);
			    }
			}
		    
			int userId = model.getUserId();
			int ownInvId = service.getOwnUserInvestigationId(userId);
		    List<Integer> investigationIds = service.getWritableUserInvestigationIds(userId);
	
		    // Rest of the rows contain inputs for each target-feature combination
		    for (int row = 1; row <= model.getFullTargetList().size(); row++) {
		    	
				for (int col = 0; col < sizeFeatures; col++) {
					int colNrInTable = col;
					if (model.isTimeInfo()) {
						colNrInTable *= 3;
					}
					
					if (!model.isNewProtocolApplication()) {
						// Show existing values, or new ones if none can be found
			    		DivPanel valueDiv = new DivPanel();
		    			DivPanel starttimeDiv = new DivPanel();
		    			DivPanel endtimeDiv = new DivPanel();
		    			int valueCounter = 0;
			    		List<ObservedValue> values = service.getObservedValuesByTargetAndFeature(
			    			model.getTargetsIdList().get(row - 1), model.getFeaturesList().get(col), 
			    			investigationIds, ownInvId);
			    		for (ObservedValue value : values) {
			    			HtmlInput input = makeInput(col, colNrInTable, row, valueCounter, value);
			    			input.setLabel("");
			    			valueDiv.add(input);
			    			if (model.isTimeInfo()) {
					    		DatetimeInput datetimeInputStart = new DatetimeInput((colNrInTable + 1) + 
					    				"_" + row + "_" + valueCounter);
					    		datetimeInputStart.setLabel("");
								if (value != null && value.getTime() != null) {
									datetimeInputStart.setValue(value.getTime());
								}
								starttimeDiv.add(datetimeInputStart);
								DatetimeInput datetimeInputEnd = new DatetimeInput((colNrInTable + 2) + 
										"_" + row + "_" + valueCounter);
								datetimeInputEnd.setLabel("");
								if (value != null && value.getEndtime() != null) {
									datetimeInputEnd.setValue(value.getEndtime());
								}
								endtimeDiv.add(datetimeInputEnd);
			    			}
			    			
			    			if (!model.isAllValues()) {
			    				// If user wants only the first value, jump out now:
			    				break;
			    			}
			    			
			    			valueCounter++;
				    	}
			    		valueTable.setCell(colNrInTable, row, valueDiv);
			    		valueTable.setCell(colNrInTable + 1, row, starttimeDiv);
			    		valueTable.setCell(colNrInTable + 2, row, endtimeDiv);
					} else {
						// Show only new values
						HtmlInput input = makeInput(col, colNrInTable, row, 0, null);
						valueTable.setCell(colNrInTable, row, input);
						if (model.isTimeInfo()) {
							DatetimeInput datetimeInputStart = new DatetimeInput((colNrInTable + 1) + "_" + row + "_0");
							valueTable.setCell(colNrInTable + 1, row, datetimeInputStart);
							DatetimeInput datetimeInputEnd = new DatetimeInput((colNrInTable + 2) + "_" + row + "_0");
							valueTable.setCell(colNrInTable + 2, row, datetimeInputEnd);
						}
					}
				}
		    }
		} catch(Exception e) {
			e.printStackTrace();
		    logger.error("Filling table cells failed", e);
		}
    }
    
    public void addTableDiv() {
    	this.protocolApplicationContainer.add(tableDiv);
    }
    
    public void setValues() {
		protocols.setValue(model.getProtocolId());
		batches.setValue(model.getBatchesList());
		if (model.isNewProtocolApplication()) {
			newOrEditButtons.setValue("New");
		} else {
			newOrEditButtons.setValue("Edit");
		}
		if (model.isTimeInfo()) {
			Vector<String> valueVector = new Vector<String>();
			valueVector.add("Time");
			timeBox.setValue(valueVector); // checkboxInput's setValue() expects a vector of String (undocumented behavior)
		} else {
			timeBox.setValue(null);
		}
		if (model.isAllValues()) {
			Vector<String> valueVector = new Vector<String>();
			valueVector.add("AllValues");
			allValuesBox.setValue(valueVector); // checkboxInput's setValue() expects a vector of String (undocumented behavior)
		} else {
			allValuesBox.setValue(null);
		}
    }

    /**
     * Set the value of the input in the table cell at col, row
     * 
     * @param col
     * @param row
     * @param value
     */
	public void fixCellValue(int col, int row, Object value) {
		HtmlInput input = (HtmlInput) valueTable.getCell(col, row);
		input.setValue(value);
		
		// And, if this involves an xref box, set the value and label of the selected option
    	if (valueTable.getCell(col, row) instanceof XrefInput) {
    		try {
    			int targetId = Integer.parseInt(value.toString());
				((XrefInput) input).setValue(service.getObservationTargetById(targetId));
			} catch (Exception e) {
				// Do nothing, no value will be set
			}
    	}
    	
		valueTable.setCell(col, row, input);
	}

}
