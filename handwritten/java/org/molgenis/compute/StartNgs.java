package org.molgenis.compute;

import app.ui.WorksheetFormController;
import compute.pipelinemodel.Pipeline;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.*;
import org.molgenis.ngs.FlowcellLaneSample;
import org.molgenis.ngs.Worksheet;
import org.molgenis.ngs.ui.WorksheetForm;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.protocol.*;
import org.molgenis.util.Tuple;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * StartNgsController takes care of all user requests and application logic.
 * <p/>
 * <li>Each user request is handled by its own method based action=methodName.
 * <li> MOLGENIS takes care of db.commits and catches exceptions to show to the user
 * <li>StartNgsModel holds application state and business logic on top of domain model. Get it via this.getModel()/setModel(..)
 * <li>StartNgsView holds the template to show the layout. Get/set it via this.getView()/setView(..).
 */
public class StartNgs extends EasyPluginController<StartNgsModel>
{
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd-HH:mm:ss";
    private Pipeline pipeline = null;

    public StartNgs(String name, ScreenController<?> parent)
    {
        super(name, null, parent);
        this.setModel(new StartNgsModel(this)); //the default model
        this.setView(new FreemarkerView("StartNgsView.ftl", getModel())); //<plugin flavor="freemarker"
    }

    @Override
    public void reload(Database db) throws Exception
    {
    }

    public void buttonStart(Database db, Tuple request) throws Exception
    {

        System.out.println("pipeline started");

        pipeline = new Pipeline();

        ScreenController<?> parentController = (ScreenController<?>) this.getParent();
        FormModel<Worksheet> parentForm = (FormModel<Worksheet>) ((FormController) parentController).getModel();
        Worksheet data = parentForm.getRecords().get(0);
        //data.

        Date date = data.getDate();
        String strDate = formatDate("" + date);
        String strLane = data.getLane();
        String strMachine = data.getMachine();
        String strFlowcell = data.getFlowcell();
        String strBarcode = data.getBarcode();
        String strSample = data.getSample();

        //application for the whole workflow
        ComputeApplication wholeWorkflowApp = new ComputeApplication();

        //we have only one workflow
        Workflow workflow = db.query(Workflow.class).find().get(0);
        wholeWorkflowApp.setProtocol(workflow);

        System.out.println("workflow" + workflow.getName());

        //our current targer is FlowcellLaneSample
        ObservationTarget target = db.query(FlowcellLaneSample.class).equals(FlowcellLaneSample.LANENUMBER, strLane).
                equals(FlowcellLaneSample.FLOWCELL_NAME, strFlowcell).
                equals(FlowcellLaneSample.SAMPLE_NAME, strSample).find().get(0);

        //add few parameters
        wholeWorkflowApp.setComputeResource("gridgain");//for time being
        wholeWorkflowApp.setTime(Calendar.getInstance().getTime());

        //add to database
        String appName = strLane + "_" + strFlowcell + "_" + strSample + "_" + now();
        wholeWorkflowApp.setName(appName);
        pipeline.setId(appName);
        db.add(wholeWorkflowApp);

        //process workflow elements
        List<WorkflowElement> workflowElements = db.query(WorkflowElement.class).equals(WorkflowElement.WORKFLOW, workflow.getId()).find();

        for (int i = 0; i < workflowElements.size(); i++)
        {
            WorkflowElement workflowElement = workflowElements.get(i);
            processWorkflowElement(db, request, workflowElement);

        }


        getModel().setSuccess("update succesfull");
    }

    private void processWorkflowElement(Database db, Tuple request, WorkflowElement workflowElement) throws DatabaseException, ParseException
    {
        //get protocol and template
        ComputeProtocol protocol = db.findById(ComputeProtocol.class, workflowElement.getProtocol_Id());
        String protocolTemplate = protocol.getScriptTemplate();

        if (protocol.getInputs().size() > 0)
        {
            List<ComputeFeature> computeFeatures = db.query(ComputeFeature.class).in(ComputeFeature.ID, protocol.getInputs_Id()).find();
        }

        if (protocol.getOutputs().size() > 0)
        {
            List<ComputeFeature> computeFeatures = db.query(ComputeFeature.class).in(ComputeFeature.ID, protocol.getOutputs_Id()).find();
        }

        List<WorkflowElementParameter> workflowElementParameters = db.query(WorkflowElementParameter.class).
                equals(WorkflowElementParameter.WORKFLOWELEMENT, workflowElement.getId()).find();

    }

    //returns format yymmdd
    private String formatDate(String date)
    {
        return date.substring(2, 4) + date.substring(5, 7) + date.substring(8, 10);
    }

    public static String now()
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());

    }

}
