package plugins.protocol;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Tuple;

class EditProtocolHandler {

	private ProtocolPluginService service;
    private EditProtocolPluginModel model;
    private EditProtocolUI ui;
    private static transient Logger logger = Logger.getLogger(ApplyProtocolHandler.class);

    public EditProtocolHandler(EditProtocolPluginModel model, EditProtocolUI ui, ProtocolPluginService service) {
		this.service = service;
		this.model = model;
		this.ui = ui;
    }

    ScreenMessage handleApply(Tuple request) {
    	
    	DateFormat formatter = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss", Locale.US);
    	
		try {
			// TODO: fix more nicely
			int investigationId;
			if (service.getObservationTarget(model.getTargetsIdList().get(0)) != null) {
				investigationId = service.getObservationTarget(model.getTargetsIdList().get(0)).getInvestigation();
			} else {
				investigationId = service.getInvestigationId("AnimalDB");
			}
			
		    int paId = service.makeProtocolApplication(investigationId, model.getProtocolId());
		    int sizeTargets = model.getTargetsIdList().size();
	
		    for (int row = 1; row <= sizeTargets; row++) {
	
		    	// TODO: if user selected "new values", don't retrieve existing values
		    	// but make only new ones
				List<ObservedValue> originalValues = service.getObservedValuesByTargetAndFeatures(
					model.getTargetsIdList().get(row - 1), model.getFeaturesIdList());
		
				int sizeFeatures = model.getFeaturesIdList().size();
				for (int col = 0; col < sizeFeatures; col++) {
					int colNrInTable = col;
					if (model.isTimeInfo()) {
						colNrInTable *= 3;
					}
					
				    // Get original value/relation
				    ObservedValue originalObservedValue = originalValues.get(col);
				    int featureId = model.getFeaturesIdList().get(col);
				    String dataType = service.getMeasurement(featureId).getDataType();
				    String oldValue = "";
				    if (dataType.equals("xref")) {
						if (originalObservedValue.getRelation() != null) {
							oldValue = originalObservedValue.getRelation().toString();
						}
				    } else {
				    	oldValue = originalObservedValue.getValue();
				    }
				    Date oldStartTime = originalObservedValue.getTime();
				    Date oldEndTime = originalObservedValue.getEndtime();
		
				    // Get new/modified value/relation
				    Date startTime = null;
				    Date endTime = null;
					if (model.isTimeInfo()) {
						// If date-time info supplied, get and parse that as well
						if (request.getString((colNrInTable + 1) + "_" + row) != null) {
							String startTimeString = request.getString((colNrInTable + 1) + "_" + row);
							startTime = formatter.parse(startTimeString);
						}
						if (request.getString((colNrInTable + 2) + "_" + row) != null) {
							String endTimeString = request.getString((colNrInTable + 2) + "_" + row);
							endTime = formatter.parse(endTimeString);
						}
					}
				    String newValue = "";
				    if (request.getString(colNrInTable + "_" + row) != null) {
				    	newValue = request.getString(colNrInTable + "_" + row);
				    }
		
				    // Compare, and update if necessary
				    if (!oldValue.equals(newValue) || oldStartTime != startTime || oldEndTime != endTime) {
						if (dataType.equals("xref")) {
						    originalObservedValue.setRelation(Integer.parseInt(newValue));
						    originalObservedValue.setValue(null);
						} else {
						    originalObservedValue.setValue(newValue);
						}
						originalObservedValue.setTime(startTime);
						originalObservedValue.setEndtime(endTime);
						originalObservedValue.setProtocolApplication(paId);
			
						try {
						    service.update(originalObservedValue);
						} catch(Exception e) {
						    e.printStackTrace();
						    logger.error("An exception occurred while updating ObservedValue " + 
							    originalValues.get(col).getValue() + " for ObservableFeature " + 
							    originalValues.get(col).getFeature_Name(), e);
						}
		
				    } else {
						// If user pressed 'Apply Defaults', values in the boxes were changed,
						// so after 'Apply' reset these boxes to the original values, so we can be sure
						// they reflect what the user entered
						if (dataType.equals("xref")) {
						    originalObservedValue.setRelation(Integer.parseInt(oldValue));
						    originalObservedValue.setValue(null);
						} else {
						    originalObservedValue.setValue(oldValue);
						}
						originalObservedValue.setTime(oldStartTime);
						originalObservedValue.setEndtime(oldEndTime);
				    }
		
				    ui.makeInputAndSetCell(col, colNrInTable, row, originalObservedValue);
				    if (model.isTimeInfo()) {
				    	String startTimeString = "";
				    	if (originalObservedValue.getTime() != null) {
				    		startTimeString = formatter.format(originalObservedValue.getTime());
				    	}
				    	String endTimeString = "";
				    	if (originalObservedValue.getEndtime() != null) {
				    		endTimeString = formatter.format(originalObservedValue.getEndtime());
				    	}
				    	ui.makeDateInputAndSetCell(colNrInTable + 1, row, startTimeString);
				    	ui.makeDateInputAndSetCell(colNrInTable + 2, row, endTimeString);
				    }
				}
		    }
	
		     return new ScreenMessage("Protocol applied successfully", true);
		} catch (Exception e) {
		    e.printStackTrace();
		    if (e.getMessage() != null) {
		    	return new ScreenMessage("Something went wrong while applying protocol: " + e.getMessage(), false);
		    }
		}
		
		return new ScreenMessage("Something went wrong while applying protocol", false);
    }

    ScreenMessage handleApplyAllDefaults(Tuple request) {

		int sizeTargets = model.getTargetsIdList().size();
		int sizeFeatures = model.getFeaturesIdList().size();
	
		for (int col = 0; col < sizeFeatures; col++) {
			int colNrInTable = col;
			if (model.isTimeInfo()) {
				colNrInTable *= 3;
			}
			
		    int featureId = model.getFeaturesIdList().get(col);
		    String dataType = "string";
		    try {
		    	dataType = service.getMeasurement(featureId).getDataType();
		    } catch (Exception e) {
		    	// do nothing, stick with default
		    }
	
		    // Placeholder to store value from default in
		    ObservedValue newValue = new ObservedValue();
		    // Put value in appropriate field
		    if (dataType.equals("xref")) {
		    	newValue.setRelation(request.getInt(colNrInTable + "_0"));
		    } else {
		    	newValue.setValue(request.getString(colNrInTable + "_0"));
		    }
		    try {
				for (int row = 1; row <= sizeTargets; row++) {
				    ui.makeInputAndSetCell(col, colNrInTable, row, newValue);
				}
		    } catch(Exception e) {
		    	return new ScreenMessage("Setting defaults failed: " + e, false);
		    }
		}
		
		return null;
    }

    /**
     * Takes the default value for one column and puts it in all the input boxes for that column (a.k.a. feature).
     * 
     * @param request
     * @param i 
     */
    ScreenMessage handleApplyDefaults(Tuple request, int col) {

    	int featureNr = col;
    	int nrOfCols = model.getFeaturesIdList().size();
    	if (model.isTimeInfo()) {
    		featureNr /= 3;
    		nrOfCols *= 3;
    	}
    	int featureId = model.getFeaturesIdList().get(featureNr);
		int sizeTargets = model.getTargetsIdList().size();
		String dataType = "string";
		try {
		    dataType = service.getMeasurement(featureId).getDataType();
		} catch (Exception e) {
		    // do nothing, stick with default
		}
		
		fixValues(request);
	
		// Placeholder to store value from default in
		ObservedValue newValue = new ObservedValue();
		// Put value in appropriate field
		if (dataType.equals("xref")) {
		    newValue.setRelation(request.getInt(col + "_0"));
		} else {
		    newValue.setValue(request.getString(col + "_0"));
		}
		try {
		    for (int row = 1; row <= sizeTargets; row++) {
		    	ui.makeInputAndSetCell(featureNr, col, row, newValue);
		    }
		} catch(Exception e) {
			return new ScreenMessage("Setting defaults failed: " + e, false);
		}
		
		return null;
    }

	public ScreenMessage handleApplyStartTime(Tuple request, int col) {
		
		fixValues(request);
		
		try {
			String startTime = request.getString(col + "_0");
		    for (int row = 1; row <= model.getTargetsIdList().size(); row++) {
		    	ui.makeDateInputAndSetCell(col, row, startTime);
		    }
		} catch(Exception e) {
			return new ScreenMessage("Setting start date-time defaults failed: " + e, false);
		}
		
		return null;
	}


	public ScreenMessage handleApplyEndTime(Tuple request, int col) {
		
		fixValues(request);
		
		try {
			String endTime = request.getString(col + "_0");
		    for (int row = 1; row <= model.getTargetsIdList().size(); row++) {
		    	ui.makeDateInputAndSetCell(col, row, endTime);
		    }
		} catch(Exception e) {
			return new ScreenMessage("Setting end date-time defaults failed: " + e, false);
		}
		
		return null;
	}
	
	/**
	 * Go through all inputs in the table and set their values,
	 * so changes that the user already made don't get lost
	 */
	private void fixValues(Tuple request) {
		
		int nrOfCols = model.getFeaturesIdList().size();
    	if (model.isTimeInfo()) {
    		nrOfCols *= 3;
    	}
		
		for (int colNr = 0; colNr < nrOfCols; colNr++) {
			 for (int row = 1; row <= model.getTargetsIdList().size(); row++) {
				 ui.fixCellValue(colNr, row, request.getString(colNr + "_" + row));
			 }
		}
	}

    /**
     * Fill the protocol application selectbox based on the chosen protocol.
     * 
     * @param db
     * @param request
     * @throws ParseException 
     * @throws DatabaseException 
     */
    ScreenMessage handleProtocolSelect(Tuple request)
    {
		// Get protocol
		Object protocol = request.getObject("Protocols");
		if (protocol == null) {
			return new ScreenMessage("No protocol selected", false);
		}
		model.setProtocolId(Integer.parseInt(protocol.toString()));
		ui.setProtocolValue(protocol);
		
		// Get time info yes/no
		if (request.getBool("TimeBox") != null) {
			model.setTimeInfo(true);
		} else {
			model.setTimeInfo(false);
		}
		ui.setTimeBoxValue();
		
		// Remove protocol select button
		ui.removeButtonFromProtocolSelect();
		
		// Make and add selectbox for protocol application
		ui.makeProtocolApplicationSelect();
		
		return null;
    }
    
    /**
     * Makes the table based on the protocol application selected by the user.
     * 
     * @param db
     * @param request
     * @throws ParseException 
     * @throws DatabaseException 
     */
    ScreenMessage handleProtocolApplicationSelect(Tuple request)
    {
		// Get protocol application
		Object protocolApplication = request.getObject("ProtocolApplications");
		model.setProtocolApplicationId(Integer.parseInt(protocolApplication.toString()));
		ui.setProtocolApplicationValue(protocolApplication);
		
		// Get time info yes/no (again)
		if (request.getBool("TimeBox") != null) {
			model.setTimeInfo(true);
		} else {
			model.setTimeInfo(false);
		}
		ui.setTimeBoxValue();
		
		// Make and add table
		ui.makeTable(model.getProtocolId(), model.getProtocolApplicationId());
		ui.makeApplyAllDefaultsButton();
		ui.makeApplyButton();
		ui.addTableDiv();
		
		return null;
    }


}
