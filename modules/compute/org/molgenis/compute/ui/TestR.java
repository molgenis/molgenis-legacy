package org.molgenis.compute.ui;

import org.molgenis.compute.workflowgenerator.WorkflowGenerator;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.protocol.Workflow;
import org.molgenis.util.Tuple;

import java.util.Hashtable;

// hardcoded test - don't be scared with listing !!
public class TestR extends EasyPluginController<TestRView>
{

    private WorkflowGenerator processing = new WorkflowGenerator();

    public TestR(String name, ScreenController<?> parent)
    {
        super(name, null, parent);
        this.setModel(new TestRView(this)); //the default model
        this.setView(new FreemarkerView("TestR.ftl", getModel())); //<plugin flavor="freemarker"
    }

    @Override
    public void reload(Database db) throws Exception
    {
    }


    public void buttonRunTest(Database db, Tuple request) throws Exception
    {
        System.out.println("Run R");

        String outputname = request.getString("outputName");
        outputname = '"' + outputname + '"';

        //only one user value goes from ui
        String applicationName = null;
        Hashtable<String, String> userValues = new Hashtable<String, String>();

        userValues.put("outputname", outputname);

        //I have no idea how to specify constraction of the application name
        applicationName = "RTEST"
                        + "_" + processing.getFormattedTime();

        //get NGS workflow
        Workflow workflow = db.query(Workflow.class).equals(Workflow.NAME, "TestR").find().get(0);

        //set few necessary parameters
        //should it come from some settings file
        processing.setRemoteLocation("/data/gcc/test_george/");

        //and local settings for debugging
        processing.setToWriteLocally(true);
        processing.setLocalLocation("/test/");

        processing.processSingleWorksheet(db, request, userValues, workflow, applicationName);
        getModel().setSuccess("start workflow succesfull");
    }

}
