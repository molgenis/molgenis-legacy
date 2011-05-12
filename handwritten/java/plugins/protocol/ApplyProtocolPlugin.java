/* Date:        February 24, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.protocol;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.GenericPlugin;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class ApplyProtocolPlugin extends GenericPlugin
{
    private static final long serialVersionUID = -5500131586262572567L;
    private ApplyProtocolPluginModel model;
    private ApplyProtocolUI ui;
    private CommonService cs = CommonService.getInstance();

    public ApplyProtocolPlugin(String name, ScreenController<?> parent)
    {
		super(name, parent);
	
		model = new ApplyProtocolPluginModel();
		ui = new ApplyProtocolUI(model);
    }

    @Override
    public void handleRequest(Database db, Tuple request)
    {
    	ScreenMessage message = null;
	    String action = request.getString("__action");

	    if( action.equals("Select") )
	    {
	    	message = handleSelect(request);
	    }
	    if( action.equals("Clear") )
	    {
	    	ui.initScreen();
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

	    if (message != null) {
	    	this.setMessages(message);
	    }
    }

  
    @Override
    public void reload(Database db)
    {
		cs.setDatabase(db);
		cs.makeObservationTargetNameMap(this.getLogin().getUserId(), false);
	
		// Only first time:
		if (ui.getProtocolApplicationContainer() == null) {
		    ui.initScreen();
		}
    }

    public String render()
    {
    	return ui.getProtocolApplicationContainer().toHtml();
    }
    
    ScreenMessage handleApply(Tuple request, Database db) {
    	
    	DateFormat formatter = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss", Locale.US);
    	
		try {
			int investigationId = cs.getUserInvestigationId(this.getLogin().getUserId());;
		    int paId = cs.makeProtocolApplication(investigationId, model.getProtocolId());
		    int sizeTargets = model.getFullTargetList().size();
	
		    for (int row = 1; row <= sizeTargets; row++) {
	
		    	// TODO: if user selected "new values", don't retrieve existing values
		    	// but make only new ones
				List<ObservedValue> originalValues = cs.getObservedValueByTargetAndFeatures(
					model.getTargetsIdList().get(row - 1), model.getFeaturesList());
		
				int sizeFeatures = model.getFeaturesList().size();
				for (int col = 0; col < sizeFeatures; col++) {
					int colNrInTable = col;
					if (model.isTimeInfo()) {
						colNrInTable *= 3;
					}
					
				    // Get original value/relation
				    ObservedValue originalObservedValue = originalValues.get(col);
				    Measurement measurement = model.getFeaturesList().get(col);
				    String dataType = measurement.getDataType();
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
						    db.update(originalObservedValue); // TODO: add to batch list and add later
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


    /**
     * Makes the table based on the protocol and targets selected by the user.
     * 
     * @param db
     * @param request
     * @throws ParseException 
     * @throws DatabaseException 
     */
    ScreenMessage handleSelect(Tuple request)
    {
		List<String> fullTargetList = new ArrayList<String>();
		
		// Get protocol
		Object protocol = request.getObject("Protocols");
		if (protocol == null) {
			return new ScreenMessage("No protocol selected", false);
		}
		model.setProtocolId(Integer.parseInt(protocol.toString()));
		
		// Get targets
		List<?> targetListObject = request.getList("Targets");
		if (targetListObject != null) {
			for (Object o : targetListObject) {
				String tmpString = (String)o;
				model.getTargetList().add(tmpString);
				fullTargetList.add(tmpString);
			}
		}
		
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
			    fullTargetList.addAll(cs.getTargetsFromBatch(id));
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
		
		ui.setValues();
	
		// Make the table div
		ui.makeTable();
		ui.makeApplyAllDefaultsButton();
		ui.makeApplyButton();
		ui.addTableDiv();
		
		return null;
    }


	public ScreenMessage handleApplyStartTime(Tuple request, int col) {
		
		fixValues(request);
		
		try {
			String startTime = request.getString(col + "_0");
		    for (int row = 1; row <= model.getFullTargetList().size(); row++) {
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
		    for (int row = 1; row <= model.getFullTargetList().size(); row++) {
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
		
		int nrOfCols = model.getFeaturesList().size();
    	if (model.isTimeInfo()) {
    		nrOfCols *= 3;
    	}
		
		for (int colNr = 0; colNr < nrOfCols; colNr++) {
			 for (int row = 1; row <= model.getFullTargetList().size(); row++) {
				 ui.fixCellValue(colNr, row, request.getString(colNr + "_" + row));
			 }
		}
	}


}
