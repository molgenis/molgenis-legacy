/**
 * @author Jessica Lundberg
 * @date 02-11-2010
 * 
 * This is the plugin class for the Sample Viewer (aka Matrix Viewer) and displays a matrix of samples with 
 * their features and the values. Has various filtering options. 
 */
package plugins.ngs.samplerviewer;

import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.ngs.NgsSample;
import org.molgenis.ngs.Project;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class SampleViewer extends PluginModel<Entity> {

    private static final long serialVersionUID = 4613044793132206269L;
    private static transient Logger logger = Logger.getLogger(SampleViewer.class);

    private Database db;
    private SampleViewerModel model;

    public SampleViewer(String name, ScreenModel<Entity> parent)
    {
	super(name, parent);
	model = new SampleViewerModel();
	model.setCommonQueries(CommonService.getInstance());
    }

    @Override
    public String getViewName()
    {
	return "plugins_ngs_samplerviewer_SampleViewer";
    }

    @Override
    public String getViewTemplate()
    {
	return "plugins/ngs/samplerviewer/SampleViewer.ftl";
    }

    @Override
    public void handleRequest(Database db, Tuple request) {
	this.db = db;
	
	model.setAction(request.getString("__action"));

	if(model.getAction().equals("showAllSamples")) {
	    model.setProjectName("");
	    this.reload(db);
	} else if(model.getAction().equals("showAllProtocols")) {
	    model.setProtocolId(0);
	    model.setProtocolName("");
	    this.reload(db);
	} else if(model.getAction().equals("selectProject")) {
	    model.setProjectName(request.getString("projecttype"));
	} else if (model.getAction().equals("submitchanges")) {
	    submitMatrixChanges(request);		
	} else if(model.getAction().equals("selectProtocol")) {
	    model.setProtocolId(request.getInt("protocoltype"));
	}

    }
    
    @Override
    public void reload(Database db)
    {
	this.db = db;
	try {
	    model.setProjects(db.find(Project.class));
	    model.setProtocols(db.find(Protocol.class));
	} catch (Exception e) {
	    String msg = "An exception occured when retrieving projects and/or protocols from the database";
	    logger.error(msg,e);
	    this.setMessages(new ScreenMessage(msg != null ? msg : "null", false));
	}
	setSampleMatrix(model.getProjectName(), model.getProtocolId());
    }

    /** Retrieve all necessary data to fill the Sample Matrix Viewer
     * 
     * @param sampleName The sample whose data we want to fill the matrix with
     */
    public void setSampleMatrix(String projectName, int protocolId) {
	try {
	    
 	    if (!projectName.equals("")) {
		model.setSamples(model.getCommonQueries().getAllSamplesForProject(projectName));
	    } else {
		model.setSamples(model.getCommonQueries().getAllSamples());
	    }
	    
	    if (protocolId > 0) {
		model.setProtocolName(model.getCommonQueries().getProtocolById(protocolId).getName());
		model.setFeatures(model.getCommonQueries().getObservableFeaturesByProtocol(protocolId));
	    } else {
		model.setFeatures(model.getCommonQueries().getAllObservableFeatures());
		model.setProtocolName("");
	    }
	} catch (Exception e) {

	    String msg = "A problem occured while setting the sample matrix";
	    logger.error(msg, e);
	    this.setMessages(new ScreenMessage(msg != null ? msg : "null", false));

	}

    }


    /** Submit the matrix changes, adding any new values to the database.
     * 
     * @param request request from freemarker, containing submission data
     */
    public void submitMatrixChanges(Tuple request) {
	List<ObservedValue> values;
	
	try {
	    for (NgsSample s : model.getSamples()) { 
		values = model.getValuesBySample(s.getId(), model.getFeatures());
		
		for (int i = 0; i < values.size(); ++i) { 
		    String originalValue = values.get(i).getValue();
		    String newValue = request.getString(s.getId() + "_" + i);
		    if (!originalValue.equals(newValue)) {
			if (newValue != null)
			    values.get(i).setValue(newValue);
			else
			    values.get(i).setValue(""); 

			db.update(values.get(i));
		    } 
		}
	    }
	} catch (Exception e) {
	    String msg = "One or more entities did not get updated in the database due to a error.";
	    logger.error(msg, e);
	    this.setMessages(new ScreenMessage(msg != null ? msg : "null", false));
	}
    }

    @Override
    public boolean isVisible()
    {
    	return true;
    }
    
    /** Clear screen messages
     * 
     */
    public void clearMessage()
	{
		this.setMessages();
	}
    
    /** Get the model
     * 
     * @return the model
     */
    public SampleViewerModel getModel() {
	    return model;
	}
}