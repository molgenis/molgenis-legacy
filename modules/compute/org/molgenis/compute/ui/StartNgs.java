package org.molgenis.compute.ui;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletContext;

import org.molgenis.compute.ComputeApplication;
import org.molgenis.compute.ComputeFeature;
import org.molgenis.compute.ComputeProtocol;
import org.molgenis.compute.pipelinemodel.Pipeline;
import org.molgenis.compute.pipelinemodel.Script;
import org.molgenis.compute.pipelinemodel.Step;
import org.molgenis.compute.scriptserver.MCF;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.ngs.LibraryLane;
import org.molgenis.ngs.Worksheet;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Workflow;
import org.molgenis.protocol.WorkflowElement;
import org.molgenis.protocol.WorkflowElementParameter;
import org.molgenis.util.HttpServletRequestTuple;
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
//        int stepID = request.getInt("inputStep");
//        System.out.println("step to debug: " + stepID);
//
//        Pipeline testPipeline = new Pipeline();
//        testPipeline.setId("step" + stepID + "_" + pipeline.getId());
//        testPipeline.setPipelinelogpath(pipeline.getPipelinelogpath());
//        testPipeline.setMonitor(pipeline.getMonitor());
//        System.out.println("!! step " + pipeline.getStep(stepID).toString());
//        testPipeline.addStep(pipeline.getStep(stepID));
//
//        executePipeline(db, testPipeline);
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
