package org.molgenis.compute.ui;

import org.molgenis.compute.pipelinemodel.Pipeline;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.*;
import org.molgenis.ngs.Worksheet;
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
        buttonStart(db, request);
        processing.setFlagJustGenerate(true);
    }


    public void buttonStart(Database db, Tuple request) throws Exception
    {
        ScreenController<?> parentController = this.getParent();
        FormModel<Worksheet> parentForm = (FormModel<Worksheet>) ((FormController) parentController).getModel();
        Worksheet data = parentForm.getRecords().get(0);

        processing.processSingleWorksheet(db, request, data);

    }

}
