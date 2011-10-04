package org.molgenis.compute.ui;

import org.molgenis.compute.pipelinemodel.Pipeline;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.ngs.Worksheet;
import org.molgenis.protocol.Workflow;
import org.molgenis.util.Tuple;

/**
 * StartNgsController takes care of all user requests and application logic.
 * <p/>
 * <li>Each user request is handled by its own method based action=methodName.
 * <li> MOLGENIS takes care of db.commits and catches exceptions to show to the user
 * <li>StartNgsModel holds application state and business logic on top of domain model. Get it via this.getModel()/setModel(..)
 * <li>StartNgsView holds the template to show the layout. Get/set it via this.getView()/setView(..).
 */
public class StartNgs extends EasyPluginController<StartNgsView>
{
    private NGSProcessing processing = new NGSProcessing();

    public StartNgs(String name, ScreenController<?> parent)
    {
        super(name, null, parent);
        this.setModel(new StartNgsView(this)); //the default model
        this.setView(new FreemarkerView("StartNgsView.ftl", getModel())); //<plugin flavor="freemarker"
    }

    @Override
    public void reload(Database db) throws Exception
    {
    }


    public void buttonTest(Database db, Tuple request) throws Exception
    {
        int stepID = request.getInt("inputStep");
        System.out.println("step to debug: " + stepID);

        Pipeline testPipeline = new Pipeline();
        Pipeline pipeline = processing.getCurrectPipeline();
        testPipeline.setId("step" + stepID + "_" + pipeline.getId());
        testPipeline.setPipelinelogpath(pipeline.getPipelinelogpath());
        testPipeline.setMonitor(pipeline.getMonitor());
        System.out.println("!! step " + pipeline.getStep(stepID).toString());
        testPipeline.addStep(pipeline.getStep(stepID));

        processing.executePipeline(db, testPipeline);
    }

    public void buttonGenerate(Database db, Tuple request) throws Exception
    {
        processing.setFlagJustGenerate(true);
        buttonStart(db, request); // we will only generate the scripts in this case
        processing.setFlagJustGenerate(false);
    }


    public void buttonStart(Database db, Tuple request) throws Exception
    {
        ScreenController<?> parentController = this.getParent();
        FormModel<Worksheet> parentForm = (FormModel<Worksheet>) ((FormController) parentController).getModel();
        Worksheet data = parentForm.getRecords().get(0);

        Workflow wf = db.query(Workflow.class).find().get(0);
        processing.processSingleWorksheet(db, request, data, wf); // <<< NB First workflow will be applied!

    }

    public void buttonTestFrom(Database db, Tuple request) throws Exception
    {
        int stepID = request.getInt("inputFromStep");
        System.out.println("debug from step: " + stepID);

        Pipeline testPipeline = new Pipeline();
        Pipeline pipeline = processing.getCurrectPipeline();
        testPipeline.setId("debugfrom" + stepID + "_" + pipeline.getId());
        testPipeline.setPipelinelogpath(pipeline.getPipelinelogpath());
        testPipeline.setMonitor(pipeline.getMonitor());
        System.out.println("!! step " + pipeline.getStep(stepID).toString());

        for(int i = stepID; i < pipeline.getNumberOfSteps(); i++)
        {
            testPipeline.addStep(pipeline.getStep(i));
        }

        processing.executePipeline(db, testPipeline);
    }

}
