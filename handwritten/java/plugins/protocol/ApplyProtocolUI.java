package plugins.protocol;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.molgenis.MolgenisFieldTypes;
import org.molgenis.batch.MolgenisBatch;
import org.molgenis.fieldtypes.UnknownField;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.CheckboxInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.DatetimeInput;
import org.molgenis.framework.ui.html.DivPanel;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.RadioInput;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.SelectMultipleInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.framework.ui.html.Table;
import org.molgenis.framework.ui.html.TextParagraph;
import org.molgenis.framework.ui.html.XrefInput;
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
    private SelectMultipleInput targets;
    private SelectMultipleInput batches;
    private RadioInput newOrEditButtons;
    private CheckboxInput timeBox;
    private CheckboxInput allValuesBox;
    
    private ApplyProtocolPluginModel model;
    private CommonService cs = CommonService.getInstance();
    
    private static transient Logger logger = Logger.getLogger(ApplyProtocolUI.class);

    public ApplyProtocolUI(ApplyProtocolPluginModel model) {
		this.model = model;
    }
    
    public void initScreen() {
    	model.setNewProtocolApplication(false);
    	model.setTimeInfo(false);
		protocolApplicationContainer = new Container();
		protocolDiv = new DivPanel("ProtocolPanel", null);
		tableDiv = new DivPanel("TablePanel", null);
		makeProtocolSelect();
		makeTargetsSelect();
		makeBatchSelect();
		makeNewOrEditButtons();
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
				valueInput = new SelectInput(col + "_" + row);
				List<ObservationTarget> panelList = model.getAllPanelsForFeature(feature);
				for (ObservationTarget p : panelList) {
					((SelectInput)valueInput).addOption(p.getName(), p.getName());
				}
			} else {
				// Normally, show the input belonging to the data type
				valueInput = MolgenisFieldTypes.createInput(dataType, col + "_" + row + "_" + order, 
					observationTargetType, model.getDatabase());
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
			    	((XrefInput) valueInput).setValueLabel("name", value.getRelation_Name());
		    	}
		    }
		}
	
		return valueInput;

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
		    protocols.setLabel("Choose Protocol:");
		    protocols.setOptions(cs.getAllProtocolsSorted(Protocol.NAME, "ASC", model.getInvestigationId()), 
		    		Protocol.ID, Protocol.NAME);
		    protocolDiv.add(protocols);
	
		} catch(Exception e) {
			e.printStackTrace();
		    logger.error("An error occurred while retrieving protocols from the database", e);
		}
    }

    /** Create a select box with ObservationTargets grabbed from the database
     * 
     */
    public void makeTargetsSelect() {
		try {
		    targets = new SelectMultipleInput("Targets", null);
		    targets.setLabel("Choose Targets:");
		    int investigationId = cs.getUserInvestigationId(model.getUserId());
		    for (ObservationTarget o : cs.getAllObservationTargets(investigationId)) {
		    	targets.addOption(o.getId(), this.getTargetName(o.getId()));
		    }
		    protocolDiv.add(targets);
	
		} catch(Exception e) {
			e.printStackTrace();
		    logger.error("An error occurred while retrieving targets from the database", e);
		}
    }
    
    /**
     * Create a select box with Batches grabbed from the database
     */
    public void makeBatchSelect() {
		try {
		    batches = new SelectMultipleInput("Batches", null);
		    batches.setLabel("Choose Batches:");
		    for (MolgenisBatch o : cs.getAllBatches()) {
		    	batches.addOption(o.getId(), o.getName());
		    }
		    protocolDiv.add(batches);
	
		} catch(Exception e) {
			e.printStackTrace();
		    logger.error("An error occurred while retrieving batches from the database", e);
		}
    }
    
    /**
     * Create radio buttons to select the way to apply the protocol.
     */
    private void makeNewOrEditButtons() {
    	Vector<ValueLabel> options = new Vector<ValueLabel>();
    	options.add(new ValueLabel("New", "Make new values"));
    	options.add(new ValueLabel("Edit", "Edit existing values"));
    	String value = "New";
    	newOrEditButtons = new RadioInput("NewOrEdit", "", 
				"Indicate whether you want to fill in new values or edit existing ones.", options, value);
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
    	protocolDiv.add(new TextParagraph("", "")); // gives empty <p></p>
		protocolDiv.add(timeBox);
    }
    
    /**
     * Create a checkbox to toggle time fields with the values.
     */
    private void makeAllValuesSelectbox() {
    	Vector<ValueLabel> options = new Vector<ValueLabel>();
    	options.add(new ValueLabel("AllValues", 
    			"Show not only most recent but all values (works only with 'Edit existing values'"));
    	allValuesBox = new CheckboxInput("AllValuesBox", "", 
    			"Indicate whether you want to see all values for every target-measurement combination", 
    			options, null);
    	protocolDiv.add(new TextParagraph("", "")); // gives empty <p></p>
		protocolDiv.add(allValuesBox);
    }

    /**
     * 
     */
    public void makeSelectButton() {
		ActionInput selectButton = new ActionInput("Select", "&nbsp;", "Select");
		protocolDiv.add(selectButton);
    }

    /** Create a button to clear selections
     * 
     */
    public void makeClearButton() {
		ActionInput clearButton = new ActionInput("Clear", "&nbsp;", "Reset");
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
		    valueTable.addRow(this.getTargetName(targetId));
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
		    DivPanel div;
		    // First row contains default input boxes
		    int sizeFeatures = model.getFeaturesList().size();
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
					DatetimeInput datetimeInputStart = new DatetimeInput((colNrInTable + 1) + "_0", now);
					datetimeInputStart.setLabel("");
					div2.add(datetimeInputStart);
					ActionInput applyButtonStartTime = new ActionInput("ApplyStartTime_" + (colNrInTable + 1), "", "Set");
					div2.add(applyButtonStartTime);
					valueTable.setCell(colNrInTable + 1, 0, div2);
					
					// Make div with default end date-time
					DivPanel div3 = new DivPanel();
					DatetimeInput datetimeInputEnd = new DatetimeInput((colNrInTable + 2) + "_0", now);
					datetimeInputEnd.setLabel("");
					div3.add(datetimeInputEnd);
					ActionInput applyButtonEndTime = new ActionInput("ApplyEndTime_" + (colNrInTable + 2), "", "Set");
					div3.add(applyButtonEndTime);
					valueTable.setCell(colNrInTable + 2, 0, div3);
				}
				valueTable.setCell(colNrInTable, 0, div);
		    }
		    
		    int investigationId = cs.getUserInvestigationId(model.getUserId());
	
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
			    		List<ObservedValue> values = cs.getObservedValuesByTargetAndFeatures(
			    			model.getTargetsIdList().get(row - 1), model.getFeaturesList().get(col), 
			    			investigationId);
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
    
    /**
     * Get the custom label (if available) or name for the ObservationTarget with id 'id'
     * 
     * @param id
     * @return
     */
    public String getTargetName(Integer id) {
    	try {
			return cs.getObservationTargetLabel(id);
		} catch (Exception e) {
			return id.toString();
		}
    }
    
    public void addTableDiv() {
    	this.protocolApplicationContainer.add(tableDiv);
    }
    
    public void setValues() {
		protocols.setValue(model.getProtocolId());
		targets.setValue(model.getTargetList());
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
				((XrefInput) input).setValueLabel("name", cs.getObservationTargetById(targetId).getName());
			} catch (Exception e) {
				// Do nothing, no value will be set
			}
    	}
    	
		valueTable.setCell(col, row, input);
	}

}
