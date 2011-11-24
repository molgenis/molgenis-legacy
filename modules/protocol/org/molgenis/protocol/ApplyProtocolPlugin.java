/* Date:        February 24, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.protocol;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.GenericPlugin;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.HtmlInputException;
import org.molgenis.matrix.MatrixException;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Tuple;

public class ApplyProtocolPlugin extends GenericPlugin
{
    private static final long serialVersionUID = -5500131586262572567L;
    private ApplyProtocolPluginModel model;
    private ApplyProtocolUI ui;
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.US);
    private ApplyProtocolService service = new ApplyProtocolService();

    public ApplyProtocolPlugin(String name, ScreenController<?> parent)
    {
		super(name, parent);
	
		model = new ApplyProtocolPluginModel();
		
		ui = new ApplyProtocolUI(model);
    }

    @Override
    public void handleRequest(Database db, Tuple request)
    {
    	if (ui.targetMatrixViewer != null) {
    		ui.targetMatrixViewer.setDatabase(db);
    	}
    	service.setDatabase(db);
    	ScreenMessage message = null;
	    String action = request.getString("__action");

	    try {
	    	if (action.startsWith(ui.targetMatrixViewer.getName())) {
	    		ui.targetMatrixViewer.handleRequest(db, request);
				action = "init";
			}
		    if( action.equals("Select") )
		    {
		    	message = handleSelect(db, request);
		    }
		    if( action.equals("Clear") )
		    {
		    	ui.initScreen(db, this, this.getLogin().getUserId(), this.getLogin().getUserName());
		    }
		    if( action.equals("Apply") )
		    {
		    	message = handleApply(request, db);
		    }
		    if( action.equals("ApplyAllDefaults") )
		    {
		    	message = handleApplyAllDefaults(request);
		    }
		    if( action.contains("ApplyDefault_"))
		    {
		    	message = handleApplyDefaults(request, Integer.parseInt(action.substring(13)));
		    }
		    if( action.contains("ApplyStartTime_"))
		    {
		    	message = handleApplyStartTime(request, Integer.parseInt(action.substring(15)));
		    }
		    if( action.contains("ApplyEndTime_"))
		    {
		    	message = handleApplyEndTime(request, Integer.parseInt(action.substring(13)));
		    }
	    } catch (Exception e) {
	    	message = new ScreenMessage("Something went wrong while handling your request: " + e.getMessage(), false);
	    }

	    if (message != null) {
	    	this.setMessages(message);
	    }
    }

  
    @Override
    public void reload(Database db)
    {
    	if (ui.targetMatrixViewer != null) {
    		ui.targetMatrixViewer.setDatabase(db);
    	}
    	service.setDatabase(db);
		model.setService(service);
		ui.setService(service);
		
		int userId = this.getLogin().getUserId();
		// Only first time or if user changed:
		if (ui.getProtocolApplicationContainer() == null || userId != model.getUserId()) {
			model.setUserAndInvestigationIds(userId);
		    try {
				ui.initScreen(db, this, userId, this.getLogin().getUserName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }

    public String render()
    {
    	return ui.getProtocolApplicationContainer().toHtml();
    }
    
    ScreenMessage handleApply(Tuple request, Database db) {
    	
    	DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US);
    	
		try {
			int userId = this.getLogin().getUserId();
			int ownInvId = service.getOwnUserInvestigationIds(userId).get(0);
			List<Integer> investigationIds = service.getWritableUserInvestigationIds(userId);
		    int paId = service.makeProtocolApplication(ownInvId, model.getProtocolId());
		    
		    for (int row = 1; row <= model.getFullTargetList().size(); row++) {
		    	
		    	int targetId = model.getTargetsIdList().get(row - 1);
		
				for (int col = 0; col < model.getFeaturesList().size(); col++) {
					
					Measurement measurement = model.getFeaturesList().get(col);
				    String dataType = measurement.getDataType();
					
					int colNrInTable = col;
					if (model.isTimeInfo()) {
						colNrInTable *= 3;
					}
					
					List<ObservedValue> originalValues = service.getObservedValuesByTargetAndFeature(
							targetId, measurement, investigationIds, ownInvId);
					
					if (!model.isNewProtocolApplication()) {
						// User chose to edit existing values (db.update())
						// Note: if a value did not yet exist for the target-feature combination,
						// a new one is made and added to the DB.
						
						// Loop through the values
						int valueCounter = 0;
						for (ObservedValue originalObservedValue : originalValues) {
							
						    String oldValue = "";
						    if (dataType.equals("xref")) {
								if (originalObservedValue.getRelation_Name() != null) {
									oldValue = originalObservedValue.getRelation_Name();
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
								if (request.getString((colNrInTable + 1) + "_" + row + "_" + valueCounter) != null) {
									String startTimeString = request.getString((colNrInTable + 1) + "_" + row + "_" + valueCounter);
									startTime = formatter.parse(startTimeString);
								}
								if (request.getString((colNrInTable + 2) + "_" + row + "_" + valueCounter) != null) {
									String endTimeString = request.getString((colNrInTable + 2) + "_" + row + "_" + valueCounter);
									endTime = formatter.parse(endTimeString);
								}
							}
						    String newValue = "";
						    if (request.getString(colNrInTable + "_" + row + "_" + valueCounter) != null) {
						    	newValue = request.getString(colNrInTable + "_" + row + "_" + valueCounter);
						    }
				
						    // Compare, and update if necessary
						    if (!oldValue.equals(newValue) || oldStartTime != startTime || oldEndTime != endTime) {
								if (dataType.equals("xref")) {
									// Set _Id instead of _Name because db.update() doesn't resolve foreign keys
									originalObservedValue.setRelation_Id(service.getObservationTargetId(newValue));
								    originalObservedValue.setValue(null);
								} else {
								    originalObservedValue.setValue(newValue);
								}
								originalObservedValue.setTime(startTime);
								originalObservedValue.setEndtime(endTime);
								if (originalObservedValue.getProtocolApplication_Id() == null) {
									// No prot.app set yet -> new value -> db.add()
									originalObservedValue.setProtocolApplication_Id(paId);
									db.add(originalObservedValue);
								} else {
									// Prot.app. already set -> existing value -> db.update()
									db.update(originalObservedValue);
								}
					
								// TODO: add value to batch list and add/update later
						    }
						    
						    if (!model.isAllValues()) {
			    				// If user wants only the first value, jump out now:
			    				break;
			    			}
						    
						    valueCounter++;
						} // end of value loop
					} else {
						// User chose to enter new values (db.add())
						Date startTime = null;
					    Date endTime = null;
					    ObservedValue newValue = new ObservedValue();
						if (model.isTimeInfo()) {
							// If date-time info supplied, get and parse that as well
							if (request.getString((colNrInTable + 1) + "_" + row + "_0") != null) {
								String startTimeString = request.getString((colNrInTable + 1) + "_" + row + "_0");
								startTime = formatter.parse(startTimeString);
							}
							if (request.getString((colNrInTable + 2) + "_" + row + "_0") != null) {
								String endTimeString = request.getString((colNrInTable + 2) + "_" + row + "_0");
								endTime = formatter.parse(endTimeString);
							}
						}
					    String value = "";
					    if (request.getString(colNrInTable + "_" + row + "_0") != null) {
					    	value = request.getString(colNrInTable + "_" + row + "_0");
					    }
			
					    // Compare, and update if necessary
					    if (dataType.equals("xref")) {
					    	newValue.setRelation_Id(null);
					    	newValue.setRelation_Name(value);
					    	newValue.setValue(null);
						} else {
							newValue.setValue(value);
						}
					    newValue.setFeature(measurement);
					    newValue.setTarget(targetId);
					    newValue.setTime(startTime);
					    newValue.setEndtime(endTime);
					    newValue.setProtocolApplication(paId);
					    // TODO: is it correct that new values are always assigned to the investigation
					    // owned by the current user?
					    if (model.getOwnInvestigationId() != -1) {
					    	newValue.setInvestigation(model.getOwnInvestigationId());
					    }
				
						db.add(newValue);
						// TODO: add to batch list and add later
					}
				} // end of feature loop
		    } // end of target loop
		    
		    // Reset table:
		    ui.fillTableCells();
		    
		    return new ScreenMessage("Protocol applied successfully", true);
		} catch (Exception e) {
		    e.printStackTrace();
		    if (e.getMessage() != null) {
		    	return new ScreenMessage("Something went wrong while applying protocol: " + e.getMessage(), false);
		    }
		}
		return null;
    }

    ScreenMessage handleApplyAllDefaults(Tuple request) {

		int sizeTargets = model.getFullTargetList().size();
		int sizeFeatures = model.getFeaturesList().size();
	
		for (int col = 0; col < sizeFeatures; col++) {
			int colNrInTable = col;
			if (model.isTimeInfo()) {
				colNrInTable *= 3;
			}
			
			Measurement measurement = model.getFeaturesList().get(col);
		    String dataType = "string";
		    try {
		    	dataType = measurement.getDataType();
		    } catch (Exception e) {
		    	// do nothing, stick with default
		    }
	
		    // Placeholder to store value from default in
		    ObservedValue newValue = new ObservedValue();
		    // Put value in appropriate field
		    if (dataType.equals("xref")) {
		    	newValue.setRelation_Name(request.getString(colNrInTable + "_0_0"));
		    } else {
		    	newValue.setValue(request.getString(colNrInTable + "_0_0"));
		    }
		    try {
				for (int row = 1; row <= sizeTargets; row++) {
				    ui.makeInputAndSetCell(col, colNrInTable, row, 0, newValue);
				}
		    } catch(Exception e) {
		    	return new ScreenMessage("Setting defaults failed: " + e, false);
		    }
		}

		return null;
    }

    /**
     * Takes the default value for each column and puts it in all the input boxes for that column (a.k.a. feature).
     * 
     * @param request
     * @param i 
     */
    ScreenMessage handleApplyDefaults(Tuple request, int col) {

    	int featureNr = col;
    	int nrOfCols = model.getFeaturesList().size();
    	if (model.isTimeInfo()) {
    		featureNr /= 3;
    		nrOfCols *= 3;
    	}
    	Measurement measurement = model.getFeaturesList().get(featureNr);
		int sizeTargets = model.getFullTargetList().size();
		String dataType = "string";
		try {
		    dataType = measurement.getDataType();
		} catch (Exception e) {
		    // do nothing, stick with default
		}
		
		fixValues(request);
	
		// Placeholder to store value from default in
		ObservedValue newValue = new ObservedValue();
		// Put value in appropriate field
		if (dataType.equals("xref")) {
		    newValue.setRelation_Name(request.getString(col + "_0_0"));
		} else {
		    newValue.setValue(request.getString(col + "_0_0"));
		}
		try {
		    for (int row = 1; row <= sizeTargets; row++) {
		    	ui.makeInputAndSetCell(featureNr, col, row, 0, newValue);
		    }
		} catch(Exception e) {
			return new ScreenMessage("Setting defaults failed: " + e, false);
		}
		
		return null;
    }


    /**
     * Makes the table based on the protocol and targets selected by the user.
     * 
     * @param db
     * @param request
     * @throws MatrixException 
     */
    ScreenMessage handleSelect(Database db, Tuple request) throws MatrixException
    {
		List<String> fullTargetList = new ArrayList<String>();
		
		// Get protocol
		Object protocol = request.getObject("Protocols");
		if (protocol == null) {
			return new ScreenMessage("No protocol selected", false);
		}
		model.setProtocolId(Integer.parseInt(protocol.toString()));
		// Set some feature info only once
		try {
			model.setFeaturesLists(service.getMeasurementsByProtocol(model.getProtocolId()));
		} catch (Exception e) {
			e.printStackTrace();
			return new ScreenMessage("Error: could not retrieve measurements for chosen protocol", false);
		}
		
		// Get targets
		List<?> targetListObject = request.getList(ui.TARGETMATRIX);
		List<ObservationElement> rows = (List<ObservationElement>) ui.targetMatrixViewer.getSelection(db);
		int rowCnt = 0;
		for (ObservationElement row : rows) {
			if (request.getBool(ui.TARGETMATRIX + "_selected_" + rowCnt) != null) {
				model.getTargetList().add(row.getId().toString());
				fullTargetList.add(row.getId().toString());
			}
			rowCnt++;
		}
//		if (targetListObject != null) {
//			for (Object o : targetListObject) {
//				String tmpString = (String)o;
//				model.getTargetList().add(tmpString);
//				fullTargetList.add(tmpString);
//			}
//		}
		
		// Get batches
		List<?> batchesListObject = request.getList("Batches");
		if (batchesListObject != null) {
			for (Object o : batchesListObject) {
				String tmpString = (String)o;
				model.getBatchesList().add(tmpString);
			}
		}
		
		// Get targets from batches and add them to the full target list
		if (model.getBatchesList() != null) {
			for (Object o : model.getBatchesList()) {
			    Integer id = Integer.parseInt((String)o);
			    fullTargetList.addAll(service.getTargetsFromBatch(id));
			}
		}
		if (fullTargetList.size() == 0) {
			return new ScreenMessage("No observation targets selected", false);
		}
		model.setFullTargetList(fullTargetList);
		
		// Get new/edit values
		if (request.getString("NewOrEdit").equals("New")) {
			model.setNewProtocolApplication(true);
		} else {
			model.setNewProtocolApplication(false);
		}
		
		// Get date-time info yes/no
		if (request.getBool("TimeBox") != null) {
			model.setTimeInfo(true);
		} else {
			model.setTimeInfo(false);
		}
		
		// Get all values yes/no
		if (request.getBool("AllValuesBox") != null) {
			model.setAllValues(true);
		} else {
			model.setAllValues(false);
		}
		
		ui.setValues();
	
		// Make the table div
		ui.makeTable();
		if (model.isAllValues() == false) {
			// Show Apply all defaults button only when showing one value per cell
			ui.makeApplyAllDefaultsButton();
		}
		ui.makeApplyButton();
		ui.addTableDiv();
		
		return null;
    }


	public ScreenMessage handleApplyStartTime(Tuple request, int col) {
		
		fixValues(request);
		
		try {
			String startTimeString = request.getString(col + "_0_0");
			Date startTime = dateTimeFormat.parse(startTimeString);
		    for (int row = 1; row <= model.getFullTargetList().size(); row++) {
		    	ui.makeDateInputAndSetCell(col, row, 0, startTime);
		    }
		} catch(Exception e) {
			return new ScreenMessage("Setting start date-time defaults failed: " + e, false);
		}

		return null;
	}


	public ScreenMessage handleApplyEndTime(Tuple request, int col) {
		
		fixValues(request);
		
		try {
			String endTimeString = request.getString(col + "_0_0");
			Date endTime = dateTimeFormat.parse(endTimeString);
		    for (int row = 1; row <= model.getFullTargetList().size(); row++) {
		    	ui.makeDateInputAndSetCell(col, row, 0, endTime);
		    }
		} catch(Exception e) {
			return new ScreenMessage("Setting end date-time defaults failed: " + e, false);
		}
		
		return null;
	}
	
	/**
	 * Go through all inputs in the table and set their values,
	 * so changes that the user already made don't get lost.
	 * Works only for cells that contain one input.
	 */
	private void fixValues(Tuple request) {
		
		int nrOfCols = model.getFeaturesList().size();
    	if (model.isTimeInfo()) {
    		nrOfCols *= 3;
    	}
		
		for (int colNr = 0; colNr < nrOfCols; colNr++) {
			 for (int row = 1; row <= model.getFullTargetList().size(); row++) {
				 ui.fixCellValue(colNr, row, request.getString(colNr + "_" + row + "_0"));
			 }
		}
	}


}
