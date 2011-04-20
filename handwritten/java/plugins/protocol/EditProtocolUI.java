package plugins.protocol;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.molgenis.MolgenisFieldTypes;
import org.molgenis.fieldtypes.UnknownField;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.CheckboxInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.DatetimeInput;
import org.molgenis.framework.ui.html.DivPanel;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.framework.ui.html.Table;
import org.molgenis.framework.ui.html.TextParagraph;
import org.molgenis.framework.ui.html.XrefInput;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.ValueLabel;

public class EditProtocolUI {
    
    private Container protocolApplicationContainer = null;
    private DivPanel protocolDiv;
    private DivPanel protocolApplicationDiv;
    private DivPanel tableDiv;
    private Table valueTable;
    private SelectInput protocols;
    private SelectInput protocolApplications;
    private CheckboxInput timeBox;
    private ActionInput selectButton;
    
    private EditProtocolPluginModel model;
    private ProtocolPluginService service;
    
    private static transient Logger logger = Logger.getLogger(EditProtocolUI.class);

    public EditProtocolUI(EditProtocolPluginModel model, ProtocolPluginService service) {
		this.model = model;
		this.service = service;
    }
    
    public void initScreen() {
    	model.setNewProtocolApplication(false);
		protocolApplicationContainer = new Container();
		protocolDiv = new DivPanel("ProtocolPanel", null);
		protocolApplicationDiv = new DivPanel("ProtocolApplicationPanel", null);
		tableDiv = new DivPanel("TablePanel", null);
		
		makeClearButton();
		makeTimeSelectbox();
		makeProtocolSelect();
		makeProtocolSelectButton();
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
     * @param value the value to insert
     * @throws Exception 
     */
    private HtmlInput makeInput(int featureNr, int col, int row, ObservedValue value) throws Exception {
		HtmlInput valueInput;
		int featureId = model.getFeaturesIdList().get(featureNr);
	
		// Get the metadata to create the input
		
		// Data type
		String dataType = service.getMeasurement(featureId).getDataType();
		if( MolgenisFieldTypes.getType(dataType) instanceof UnknownField ) {
		    throw new Exception("Fieldtype " + dataType + "' is unknown in MOLGENIS");
		}
		
		// Target type allowed for relation
		String observationTargetType = "ObservationTarget";
		if (service.getMeasurement(featureId).getTargettypeAllowedForRelation() != null) {
			int entityId = service.getMeasurement(featureId).getTargettypeAllowedForRelation();
			observationTargetType = service.getEntityName(entityId);
		}
		
		// Panel label for relation
		String panelLabel = service.getMeasurement(featureId).getPanelLabelAllowedForRelation();
	
		// Make the appropriate input
		if (dataType.equals("string") && service.getCodesForMeasurement(featureId).size() > 0) {
		    // If there are codes for this Measurement, show a selectbox with those
		    valueInput = new SelectInput(col + "_" + row);
		    ((SelectInput)valueInput).setOptionsFromStringList(service.getCodesForMeasurement(featureId));
		} else {
			if (panelLabel != null) {
				// If there's only a subset of labeled Panels allowed for this Measurement, show a selectbox with those
				valueInput = new SelectInput(col + "_" + row);
				List<Panel> panelList = service.getLabeledPanels(panelLabel);
				for (Panel p : panelList) {
					((SelectInput) valueInput).addOption(p.getId(), p.getName());
				}
			} else {
				// Normally, show the input belonging to the data type
				valueInput = MolgenisFieldTypes.createInput(dataType, col + "_" + row, observationTargetType , service.getDatabase());
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
		    } else if (value.getRelation() != null) {
		    	// Otherwise it must be a relation, so use the xref id as a value...
		    	valueInput.setValue(value.getRelation().toString());
		    	if (panelLabel == null) { // If a panel label was set, valueInput has been turned into a selectbox and we cannot do the statement below
			    	// Because this involves an xref box, set the value and label of the selected option
			    	// Note: cannot use getRelation_name() because, in case of apply defaults, value
			    	// is not in the database and mapper methods do not work
			    	((XrefInput) valueInput).setValueLabel("name", service.getObservationTarget(value.getRelation()).getName());
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
     * Make a selectbox with protocols.
     */
    public void makeProtocolSelect() {

		try {
		    protocols = new SelectInput("Protocols");
		    protocols.setLabel("Choose Protocol:");
		    protocols.setOptions(service.getProtocols(), Protocol.ID, Protocol.NAME);
		    protocolDiv.add(protocols);
	
		} catch(Exception e) {
		    logger.error("An error occurred while retrieving protocols from the database", e);
		}

    }
    
    public void removeButtonFromProtocolSelect() {
    	protocolDiv.remove(selectButton);
    }
    
    /**
     * Make a selectbox with protocol applications.
     */
    public void makeProtocolApplicationSelect() {

		try {
		    List<ProtocolApplication> appList = service.getProtocolApplications(model.getProtocolId());
		    if (appList.size() > 0) {
		    	
		    	protocolApplications = new SelectInput("ProtocolApplications");
			    protocolApplications.setLabel("Choose Protocol Application:");
			    for (ProtocolApplication pa : appList) {
			    	protocolApplications.addOption(pa.getId(), pa.getTime());
			    }
			    protocolApplicationDiv.add(protocolApplications);
			    
			    makeProtocolApplicationSelectButton();
			    
			    protocolApplicationContainer.add(protocolApplicationDiv);
		    }
		} catch(Exception e) {
		    logger.error("An error occurred while retrieving protocol applications from the database", e);
		}

    }

    /**
     * Make a button to select a protocol.
     */
    public void makeProtocolSelectButton() {
		selectButton = new ActionInput("SelectProtocol", "&nbsp;", "Select");
		protocolDiv.add(selectButton);
    }

    /**
     * Make a button to select a protocol application.
     */
    public void makeProtocolApplicationSelectButton() {
		ActionInput selectButton = new ActionInput("SelectProtocolApplication", "&nbsp;", "Select");
		protocolApplicationDiv.add(selectButton);
    }
    
    /** Create a button to clear selections
     * 
     */
    public void makeClearButton() {
		ActionInput clearButton = new ActionInput("Clear", "&nbsp;", "Reset");
		protocolApplicationContainer.add(clearButton);
    }

    /** Create an 'apply' button
     * 
     */
    public void makeApplyButton() {
		ActionInput applyButton = new ActionInput("Save", "", "Save");
		tableDiv.add(applyButton);
    }

    /** Create a button to apply defaults to the table
     * 
     */
    public void makeApplyAllDefaultsButton() {
		ActionInput applyDefaultsButton = new ActionInput("ApplyAllDefaults", "", "Set All Defaults");
		tableDiv.add(applyDefaultsButton);
    }
    
    /**
     * Create a checkbox to toggle time fields with the values.
     */
    private void makeTimeSelectbox() {
    	Vector<ValueLabel> options = new Vector<ValueLabel>();
    	options.add(new ValueLabel("Time", "Show date-time fields with values"));
    	timeBox = new CheckboxInput("TimeBox", "", "Indicate whether you want date-time fields next to the values", 
    			options, null);
    	// TODO: make checkbox readonly when application name is NGS-LIMS
    	protocolDiv.add(new TextParagraph("", "")); // gives empty <p></p>
		protocolDiv.add(timeBox);
    }

    /** Make a table given a protocolId (whose feature to use) and a targetlist
     * 
     * @param protocolId
     * @param targetList
     */
    public void makeTable(int protocolId, int protocolApplicationId) {
		try {
		    valueTable = new Table("ValueTable", "");
		    makeColumns(protocolId);
		    makeRows(protocolApplicationId);
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
    public void makeColumns(int protocolId) throws DatabaseException, ParseException {
		model.setFeaturesIdList(service.getObservableFeaturesInProtocol(model.getProtocolId()));
		for (Integer mId : model.getFeaturesIdList()) {
		    String measurementName = service.getMeasurement(mId).getName();
		    valueTable.addColumn(measurementName);
		    if (model.isTimeInfo()) {
		    	 valueTable.addColumn(measurementName + " start");
		    	 valueTable.addColumn(measurementName + " end");
		    }
		}
    }

    /** Create rows for a list of targets 
     * 
     * @param targetList the list of targets 
     * @throws DatabaseException
     * @throws ParseException
     */
    public void makeRows(int protocolApplicationId) throws DatabaseException, ParseException {
		valueTable.addRow("Defaults:");
		
		model.setTargetsIdList(service.getObservationTargetsInProtocolApplication(protocolApplicationId));
		for (int targetId : model.getTargetsIdList()) {
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
		    int sizeFeatures = model.getFeaturesIdList().size();
		    for (int col = 0; col < sizeFeatures; col++) {
		    	int colNrInTable = col;
				if (model.isTimeInfo()) {
					colNrInTable *= 3;
				}
				div = new DivPanel();
				HtmlInput input = makeInput(col, colNrInTable, 0, null);
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
	
		    // Rest of the rows contain inputs for each target-feature combination
		    for (int row = 1; row <= model.getTargetsIdList().size(); row++) {
		    	
		    	List<ObservedValue> values = null;
		    	if (!model.isNewProtocolApplication()) {
		    		values = service.getObservedValuesByTargetAndFeatures(
		    				model.getTargetsIdList().get(row - 1), model.getFeaturesIdList());
		    	}
		
				for (int col = 0; col < sizeFeatures; col++) {
					int colNrInTable = col;
					if (model.isTimeInfo()) {
						colNrInTable *= 3;
					}
					
					ObservedValue tmpValue = null;
					if (!model.isNewProtocolApplication()) {
						tmpValue = values.get(col);
					}
				    HtmlInput input = makeInput(col, colNrInTable, row, tmpValue);
				    // Put the input in the right place in the table
					if (model.isTimeInfo()) {
						DatetimeInput datetimeInputStart = new DatetimeInput((colNrInTable + 1) + "_" + row);
						if (tmpValue != null && tmpValue.getTime() != null) {
							datetimeInputStart.setValue(tmpValue.getTime());
						}
						valueTable.setCell(colNrInTable + 1, row, datetimeInputStart);
						DatetimeInput datetimeInputEnd = new DatetimeInput((colNrInTable + 2) + "_" + row);
						if (tmpValue != null && tmpValue.getEndtime() != null) {
							datetimeInputEnd.setValue(tmpValue.getEndtime());
						}
						valueTable.setCell(colNrInTable + 2, row, datetimeInputEnd);
					}
				    valueTable.setCell(colNrInTable, row, input);
				}
		    }
		} catch(Exception e) {
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
			return service.getObservationTargetLabel(id);
		} catch (Exception e) {
			return id.toString();
		}
    }
    
    /**
     * Make an input that's appropriate for the given observed value and insert it into the value table
     * at col, row.
     * 
     * @param featureNr : place where the observable feature occurs in the model's observable feature list
     * @param col : table column to put input in
     * @param row : table row to put input in
     * @param originalObservedValue : observed value to take the value from
     * @throws Exception
     */
    public void makeInputAndSetCell(int featureNr, int col, int row, ObservedValue originalObservedValue) throws Exception {
		HtmlInput input = makeInput(featureNr, col, row, originalObservedValue);
		valueTable.setCell(col, row, input);
    }
    
    /**
     * Make a date-time input and insert it into the value table at col, row.
     * 
     * @param col
     * @param row
     * @param datetime
     * @throws Exception
     */
    public void makeDateInputAndSetCell(int col, int row, String datetime) throws Exception {
		DatetimeInput input = new DatetimeInput(col + "_" + row, datetime);
		valueTable.setCell(col, row, input);
    }
    
    public void addTableDiv() {
    	this.protocolApplicationContainer.add(tableDiv);
    }
    
    public void setProtocolValue(Object protocolValue) {
		protocols.setValue(protocolValue);
    }
    
    public void setProtocolApplicationValue(Object protAppValue) {
		protocolApplications.setValue(protAppValue);
    }
    
    public void setTimeBoxValue() {
    	if (model.isTimeInfo()) {
			Vector<String> valueVector = new Vector<String>();
			valueVector.add("Time");
			timeBox.setValue(valueVector); // checkboxInput's setValue() expects a vector of String (undocumented behavior)
		} else {
			timeBox.setValue(null);
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
				((XrefInput) input).setValueLabel("name", service.getObservationTarget(targetId).getName());
			} catch (Exception e) {
				// Do nothing, no value will be set
			}
    	}
    	
		valueTable.setCell(col, row, input);
	}

}
