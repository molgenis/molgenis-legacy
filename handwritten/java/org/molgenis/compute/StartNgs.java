package org.molgenis.compute;

//import app.ui.WorksheetFormController;
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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    //format to run pipeline in compute
    private Pipeline pipeline = null;

    //map of all compute features/values
    private HashMap<String, String> allFeatureValues = new HashMap<String, String>();
    private HashMap<String, ComputeFeature> computeFeatures = new HashMap<String, ComputeFeature>();

    private enum UserParameter
    {
        sample, flowcell, lane, barcode, machine, date;
    }

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

        //take all compute features, not so many for ngs pipeline
        // having one workflow makes life easy
        //otherwise - select all features of workflow
        List<ComputeFeature> allComputeFeatures = db.query(ComputeFeature.class).find();
        System.out.println("we have so many features: " + allComputeFeatures.size());

        //creating map feature/value
        for (ComputeFeature computeFeature : allComputeFeatures)
        {
            System.out.println("feature: " + computeFeature.getName() + " --> " + computeFeature.getDefaultValue());

            computeFeatures.put(computeFeature.getName(), computeFeature);

            if (computeFeature.getIsUser())
            {
                switch (UserParameter.valueOf(computeFeature.getName()))
                {
                    case sample:
                        allFeatureValues.put(computeFeature.getName(), strSample);
                        break;
                    case lane:
                        allFeatureValues.put(computeFeature.getName(), strLane);
                        break;
                    case flowcell:
                        allFeatureValues.put(computeFeature.getName(), strFlowcell);
                        break;
                    case barcode:
                        allFeatureValues.put(computeFeature.getName(), strBarcode);
                        break;
                    case machine:
                        allFeatureValues.put(computeFeature.getName(), strMachine);
                        break;
                    case date:
                        allFeatureValues.put(computeFeature.getName(), strDate);
                        break;
                }
            }
            else
            {
                allFeatureValues.put(computeFeature.getName(), computeFeature.getDefaultValue());
            }
        }

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

    private void processWorkflowElement(Database db, Tuple request, WorkflowElement workflowElement)
            throws DatabaseException, ParseException, IOException
    {
        //table of values for weaving
        Hashtable<String, String> weavingValues = new Hashtable<String, String>();

        System.out.println(">>> workflow element: " + workflowElement.getName());

        //create complex features, which will be processed after simple features
        Vector<ComputeFeature> featuresToIterate = new Vector<ComputeFeature>();
        Vector<ComputeFeature> featuresToDerive = new Vector<ComputeFeature>();

        //get protocol and template
        ComputeProtocol protocol = db.findById(ComputeProtocol.class, workflowElement.getProtocol_Id());
        String protocolTemplate = protocol.getScriptTemplate();

        //fill vectors/hashtable with complex/simple compute features
        if (protocol.getInputs().size() > 0)
        {
            List<ComputeFeature> computeFeatures = db.query(ComputeFeature.class).in(ComputeFeature.ID, protocol.getInputs_Id()).find();
            for (ComputeFeature computeFeature : computeFeatures)
            {
                System.out.println("feature: " + computeFeature.getName() + " --> " + computeFeature.getDefaultValue());
                if (computeFeature.getIsDerived())
                {
                    featuresToDerive.addElement(computeFeature);
                }
                else if (computeFeature.getIterateOver())
                {
                    featuresToIterate.addElement(computeFeature);
                }
                else
                {
                    weavingValues.put(computeFeature.getName(), computeFeature.getDefaultValue());
                }
            }
        }

        if (protocol.getOutputs().size() > 0)
        {
            List<ComputeFeature> computeFeatures = db.query(ComputeFeature.class).in(ComputeFeature.ID, protocol.getOutputs_Id()).find();
            for (ComputeFeature computeFeature : computeFeatures)
            {
                System.out.println("feature: " + computeFeature.getName() + " --> " + computeFeature.getDefaultValue());
                if (computeFeature.getIsDerived())
                {
                    featuresToDerive.addElement(computeFeature);
                }
                else if (computeFeature.getIterateOver())
                {
                    featuresToIterate.addElement(computeFeature);
                }
                else
                {
                    weavingValues.put(computeFeature.getName(), computeFeature.getDefaultValue());
                }

            }
        }

        //find value of workflowelementparameter
        List<WorkflowElementParameter> workflowElementParameters = db.query(WorkflowElementParameter.class).
                equals(WorkflowElementParameter.WORKFLOWELEMENT, workflowElement.getId()).find();

        for (WorkflowElementParameter par : workflowElementParameters)
        {
            System.out.println("par: " + par.getFeature_Name() + " --> " + par.getTarget_Name());

            ComputeFeature feature = computeFeatures.get(par.getTarget_Name());
            weavingValues.put(par.getFeature_Name(), feature.getDefaultValue());

        }

        //create compute applications
        //todo iterate over several features
        //now just hardcoded for fastqc index (only one feature to iterate over)
        if (featuresToIterate.size() > 0)
        {
            ComputeFeature fastqcFeature = featuresToIterate.elementAt(0);
            for (int i = 1; i < 3; i++)
            {
                weavingValues.put(fastqcFeature.getName(), i + "");
                generateComApp(db, request, workflowElement, weavingValues, featuresToDerive);
            }
        }
        else
        {
            generateComApp(db, request, workflowElement, weavingValues, featuresToDerive);

        }

    }

    private void generateComApp(Database db, Tuple request, WorkflowElement workflowElement,
                                Hashtable<String, String> weavingValues,
                                Vector<ComputeFeature> featuresToDerive) throws IOException, DatabaseException
    {
        ComputeApplication app = new ComputeApplication();

        //db.add(app);

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
