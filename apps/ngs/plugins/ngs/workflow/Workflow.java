/**
 * @author Jessica Lundberg
 * @date 15-11-2010
 * 
 * Plugin class for Workflow.java which shows a sample workflow. 
 */
package plugins.ngs.workflow;

import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.ngs.NgsSample;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class Workflow extends PluginModel<Entity> {

    private static final long serialVersionUID = 2647518452673854028L;
    private static transient Logger logger = Logger.getLogger(Workflow.class);
    
    private WorkflowModel model;
    private Database db;


    /** Constructor which calls parent and sets model
     * 
     * @param name
     * @param parent
     */
    public Workflow(String name, ScreenController<?> parent)
    {
		super(name, parent);
		model = new WorkflowModel(this);
		this.model.setCommonQueries(CommonService.getInstance());
    }

    @Override
    public String getViewName()
    {
    	return "plugins_ngs_workflow_Workflow";
    }

    @Override
    public String getViewTemplate()
    {
    	return "plugins/ngs/workflow/Workflow.ftl";
    }

    @Override
    public void handleRequest(Database db, Tuple request) {
    	model.setUserId(this.getLogin().getUserId());
    	
		model.getCommonQueries().setDatabase(db);
		this.db = db;
		model.setAction(request.getString("__action"));
	
		if(model.getAction().equals("showAll")) {
		    model.setProjectName("");
		} else if(model.getAction().equals("ShowSamplesForProject")) {
		    model.setProjectName(request.getString("id"));
		    this.reload(db);
		} else if (model.getAction().equals("ShowSampleInfo")) {
		    String sampleName = request.getString("id");
		    setSampleMatrix(sampleName);
	
		} else if (model.getAction().equals("submitchanges")) {
		    submitMatrixChanges(request);
		    setSampleMatrix(model.getSample().getName());
		    model.setAction("ShowSampleInfo");
	
		} else if (model.getAction().equals("changeProtocol")) {
		    Integer workflowId = request.getInt("protocolbox");
		    changeWorkflow(workflowId);
		    model.setAction("ShowSampleInfo");
		    setSampleMatrix(model.getSample().getName());
	
		}

    }

    /**Change the current workflow element for a sample and change which workflows elements are
     * allowed as the following step.
     * 
     * @param workflowId
     */
    private void changeWorkflow(Integer workflowId) {
	NgsSample samp = model.getSample();
	
	if(true) throw new UnsupportedOperationException("see problem on next line.");
	//samp.setWorkflowElement(workflowId);
	try {
	    db.update(samp);
	} catch (Exception e) {
	   String msg = "An exception occured while trying to change sample " + samp.getName() + "'s workflow";
	   logger.error(msg, e);
	   this.setMessages(new ScreenMessage(msg != null ? msg : "null", false));
	   
	}
	
	
    }

    /** Changes a sample's current protocol and create a new protocol application for chosen protocol
     * 
     * @param protocolId the protocol to change to
     */

    @Override
    public void reload(Database db)
    {
	model.getCommonQueries().setDatabase(db);
	this.db = db;

	try {
//	    if(model.getProjectName().equals("")) {
//		model.setSamples(model.getCommonQueries().getAllSamples());	
//	    }
//	    else {
//		model.setSamples(model.getCommonQueries().getAllSamplesForInvestigation(model.getProjectName()));
//	    }

	    model.setProjects(db.find(Investigation.class));


	} catch(Exception e) {
	    logger.error("An exception occured while retrieving samples, projects and/or protocols from the db", e);
	}
    }

    @Override
    public boolean isVisible()
    {
    	return true;
    }
    
    public void clearMessage()
	{
		this.setMessages();
	}

    /** Retrieve all necessary data to fill the Sample Matrix Viewer
     * 
     * @param sampleName The sample whose data we want to fill the matrix with
     */
    public void setSampleMatrix(String sampleName) {
	try {
		throw new Exception("commented out lines below");
//	    //sample should never be null: sample is chosen from a drop down list grabbed from Database
//	    model.setSample(model.getCommonQueries().getSampleByName(sampleName));
//	    
//	    NgsSample samp = model.getCommonQueries().getSampleByName(sampleName);
//	    //WorkflowElement element = model.getCommonQueries().getWorkflowElement(samp.getWorkflowElement_Name());
//	    model.setFeatures(model.getCommonQueries().getMeasurementsByProtocol(element.getProtocol_Id()));
//	    model.setCurrentProtocol(model.getCommonQueries().getProtocolById(element.getProtocol_Id()));
//	    model.setValuesBySample(model.getSample().getId(), model.getFeatures());
//	    model.setWorkflowElements(model.getCommonQueries().getCandidateWorkflowElements(model.getSample().getWorkflowElement_Id()));
 
	} catch (Exception e) {
	    String msg = "Exception occured while trying to retrieve Sample information for Sample" + sampleName;
	    logger.error(msg, e);
	    this.setMessages(new ScreenMessage(msg != null ? msg : "null", false));
	}

    }

    /** Store any changes to data in the Sample Matrix Viewer (checks all values)
     * 
     * @param db
     * @param request the request from the page, containing field data
     */
    public void submitMatrixChanges(Tuple request) {
	List<ObservedValue> values;
	
	try {
	    
		values = model.getValuesBySample();

		for (int i = 0; i < values.size(); ++i) { 
		    String originalValue = values.get(i).getValue();
		    String newValue = request.getString(model.getSample().getId() + "_" + i);
		    if (!originalValue.equals(newValue)) {
			if (newValue != null)
			    values.get(i).setValue(newValue);
			else
			    values.get(i).setValue(""); 

			db.update(values.get(i));
		    } 
		}
	    
	} catch (Exception e) {
	    String msg = "One or more entities did not get updated in the database due to a error.";
	    logger.error(msg, e);
	    this.setMessages(new ScreenMessage(msg != null ? msg : "null", false));
	}
    }
    
    public WorkflowModel getModel() {
	return model;
    }
    
}