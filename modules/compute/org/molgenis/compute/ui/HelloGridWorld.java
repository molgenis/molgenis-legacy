package org.molgenis.compute.ui;

import java.util.Hashtable;

import org.molgenis.compute.workflowgenerator.WorkflowGeneratorDB;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.protocol.Workflow;
import org.molgenis.util.Tuple;

// hardcoded test - don't be scared with listing !!
public class HelloGridWorld extends EasyPluginController<HelloGridWorldView>
{

    private WorkflowGeneratorDB processing = new WorkflowGeneratorDB();

    public HelloGridWorld(String name, ScreenController<?> parent)
    {
        super(name, parent);
        this.setModel(new HelloGridWorldView(this)); //the default model
    }
    
    public ScreenView getView()
    {
    	return new FreemarkerView("HelloGridWorld.ftl", getModel());
    }

    @Override
    public void reload(Database db) throws Exception
    {
    }


    public void buttonRunGrid(Database db, Tuple request) throws Exception
    {
       System.out.println("Run on the Grid");
       String outputname = request.getString("outputGridName");

        //only one user value goes from ui
        String applicationName = null;
        Hashtable<String, String> userValues = new Hashtable<String, String>();

        userValues.put("output_name", outputname);

        applicationName = "HelloGridWorld"
                        + "_" + processing.getFormattedTime();

        //get NGS workflow
        Workflow workflow = db.query(Workflow.class).equals(Workflow.NAME, "HelloGridWorld").find().get(0);

        //set few necessary parameters
        //should it come from some settings file
        processing.setRemoteLocation("/home/byelas/");

        //and local settings for debugging
        processing.setToWriteLocally(true);
        processing.setLocalLocation("/test/");

        processing.processSingleWorksheet(db, request, userValues, workflow, applicationName, WorkflowGeneratorDB.ENV_GRID);
        getModel().setSuccess("start workflow succesfull");
    }

}
