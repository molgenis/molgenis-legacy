package org.molgenis.compute;

import compute.pipelinemodel.Pipeline;
import compute.pipelinemodel.Script;
import compute.pipelinemodel.Step;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.*;
import org.molgenis.ngs.FlowcellLaneSample;
import org.molgenis.ngs.Worksheet;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
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
    private static final String PARAMETER = "parameter";//reserved word to show that ComputeFeatureValue goes from WorkflowElementParameter


    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd-HH:mm:ss";
    //format to run pipeline in compute
    private Pipeline pipeline = null;
    private Step currentStep = null;
    private String strCurrentPipelineStep = "INITIAL";
    private int pipelineElementNumber = 0;

    //map of all compute features/values
    private Hashtable<String, String> weavingValues = null;
    private Hashtable<String, String> userValues = null;
    private HashMap<String, ComputeFeature> computeFeatures = new HashMap<String, ComputeFeature>();

    //target of pipeline
    private ObservationTarget target = null;
    //whole workflow application
    private ComputeApplication wholeWorkflowApp = null;

    private Calendar cal = Calendar.getInstance();
    private WorkflowParametersWeaver weaver = new WorkflowParametersWeaver();

    private enum UserParameter
    {
        sample, flowcell, lane, barcode, machine, date, capturing;
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
        userValues = new Hashtable<String, String>();
        pipelineElementNumber = 0;

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
        String strCapturing = data.getCapturing();

        //application for the whole workflow
        wholeWorkflowApp = new ComputeApplication();

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
                        userValues.put(computeFeature.getName(), strSample);
                        break;
                    case lane:
                        userValues.put(computeFeature.getName(), strLane);
                        break;
                    case flowcell:
                        userValues.put(computeFeature.getName(), strFlowcell);
                        break;
                    case barcode:
                        userValues.put(computeFeature.getName(), strBarcode);
                        break;
                    case machine:
                        userValues.put(computeFeature.getName(), strMachine);
                        break;
                    case date:
                        userValues.put(computeFeature.getName(), strDate);
                        break;
                   case capturing:
                        userValues.put(computeFeature.getName(), strCapturing);
                        break;
                }
            }
            else
            {
                //allFeatureValues.put(computeFeature.getName(), computeFeature.getDefaultValue());
            }
        }

        System.out.println("workflow" + workflow.getName());

        //our current targer is FlowcellLaneSample
        target = db.query(FlowcellLaneSample.class).equals(FlowcellLaneSample.LANENUMBER, strLane).
                equals(FlowcellLaneSample.FLOWCELL_NAME, strFlowcell).
                equals(FlowcellLaneSample.SAMPLE_NAME, strSample).find().get(0);

        //add few parameters
        wholeWorkflowApp.setComputeResource("cluster");//for time being
        wholeWorkflowApp.setTime(cal.getTime());

        //set app name everywhere and add to database
        String appName = strLane + "_" + strFlowcell + "_" + strSample + "_" + now();
        wholeWorkflowApp.setName(appName);
        pipeline.setId(appName);
        weaver.setJobID(appName);
        db.add(wholeWorkflowApp);

        //process workflow elements
        List<WorkflowElement> workflowElements = db.query(WorkflowElement.class).equals(WorkflowElement.WORKFLOW, workflow.getId()).find();

        for (int i = 0; i < workflowElements.size(); i++)
        {
            WorkflowElement workflowElement = workflowElements.get(i);
            processWorkflowElement(db, request, workflowElement);
        }

        String logfile = weaver.getLogfilename();
        System.out.println("logfile: " + logfile);

        pipeline.setLogfile(logfile);


        getModel().setSuccess("update succesfull");
    }

    private void processWorkflowElement(Database db, Tuple request, WorkflowElement workflowElement)
            throws DatabaseException, ParseException, IOException
    {

        weavingValues = new Hashtable<String, String>();
        weavingValues.putAll(userValues);

        System.out.println(">>> workflow element: " + workflowElement.getName());

        //create complex features, which will be processed after simple features
        Vector<ComputeFeature> featuresToIterate = new Vector<ComputeFeature>();
        Vector<ComputeFeature> featuresToDerive = new Vector<ComputeFeature>();

        //get protocol and template
        ComputeProtocol protocol = db.findById(ComputeProtocol.class, workflowElement.getProtocol_Id());

        //fill vectors/hashtable with complex/simple compute features
        if (protocol.getInputs().size() > 0)
        {
            List<ComputeFeature> computeFeatures = db.query(ComputeFeature.class).in(ComputeFeature.ID, protocol.getInputs_Id()).find();
            for (ComputeFeature computeFeature : computeFeatures)
            {
                //System.out.println("input feature: " + computeFeature.getName() + " --> " + computeFeature.getDefaultValue());
                if (computeFeature.getIsUser() || computeFeature.getDefaultValue().equalsIgnoreCase(PARAMETER))
                    continue;
                else if (computeFeature.getIsDerived())
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
                //System.out.println("output feature: " + computeFeature.getName() + " --> " + computeFeature.getDefaultValue());
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
            //System.out.println("par: " + par.getFeature_Name() + " --> " + par.getTarget_Name());

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
                System.out.println(">>>>>>>> fastQC : " + fastqcFeature.getName());
                weavingValues.put(fastqcFeature.getName(), i + "");
                generateComApp(db, request, workflowElement, protocol, weavingValues, featuresToDerive);
            }
        }
        else
        {
            generateComApp(db, request, workflowElement, protocol, weavingValues, featuresToDerive);

        }

    }

    private void generateComApp(Database db, Tuple request, WorkflowElement workflowElement, ComputeProtocol protocol,
                                Hashtable<String, String> weavingValues, Vector<ComputeFeature> featuresToDerive)
            throws IOException, DatabaseException
    {
        ComputeApplication app = new ComputeApplication();
        app.setProtocol(protocol);
        app.setTime(cal.getTime());
        app.setComputeResource("cluster");

        String appName = "ngs_" + pipeline.getId() + "_" + workflowElement.getName() + "_" + pipelineElementNumber;
        app.setName(appName);
        System.out.println("---application---> " + appName);

        String protocolTemplate = protocol.getScriptTemplate();

        System.out.println("--- template \n" + protocolTemplate );

        //weave complex features
        for (int i = 0; i < featuresToDerive.size(); i++)
        {
            ComputeFeature feature = featuresToDerive.elementAt(i);
            String featureName = feature.getName();
            String featureTemplate = feature.getDefaultValue();

            String featureValue = weaver.weaveFreemarker(featureTemplate, weavingValues);
            //System.out.println("complex-feature: " + featureName + " --> " + featureValue);
            weavingValues.put(featureName, featureValue);
        }

        String result = weaver.weaveFreemarker(protocolTemplate, weavingValues);
        app.setComputeScript(result);
        //app.setPrevSteps();
        db.add(app);

        Set entries = weavingValues.entrySet();
        Iterator it = entries.iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();
            String name = (String) entry.getKey();
            String value = (String) entry.getValue();

            System.out.println("feature: " + name + " ---> " + value);
            ObservedValue observedValue = new ObservedValue();
            observedValue.setValue(value);
            observedValue.setProtocolApplication(app);
            observedValue.setTarget(target);
            ComputeFeature feature = computeFeatures.get(name);
            observedValue.setFeature(feature);
            db.add(observedValue);
        }

       System.out.println("script \n " + result);


        pipelineElementNumber++;

        //create compute pipeline
        String scriptID = app.getName();
        weaver.setScriptID(scriptID);
        weaver.setWalltime(protocol.getComputationalTime());
        weaver.setActualCommand(result);
        String remoteLocation = computeFeatures.get("outputdir").getDefaultValue();
        weaver.setDatasetLocation(remoteLocation);
        //write file for testing purposes
        String scriptFile = weaver.makeScript();
        String logfile = weaver.getLogfilename();
        pipeline.setLogfile(logfile);

        weaver.writeToFile("/test/" + pipelineElementNumber + scriptID, scriptFile);

        //todo rewrite pipeline generation
        //look into proper choose of logfile and all path settings
        List<String> strPreviousWorkflowElements = workflowElement.getPreviousSteps_Name();

        if(strPreviousWorkflowElements.size() == 0)//script does not depend on other scripts
        {
            if(currentStep == null) //it is a first script in the pipeline
            {
                Step step = new Step("step_" + app.getName());
                currentStep = step;
                pipeline.addStep(step);
            }

            Script pipelineScript = new Script(scriptID, remoteLocation, result.getBytes());
            currentStep.addScript(pipelineScript);
        }
        else //scripts depends on previous scripts
        {
            String strPrevious = strPreviousWorkflowElements.get(0);

            if(!strPrevious.equalsIgnoreCase(strCurrentPipelineStep))
            {
                Step step = new Step("step_" + app.getName());
                currentStep = step;
                pipeline.addStep(step);
            }

            Script pipelineScript = new Script(scriptID, remoteLocation, result.getBytes());
            currentStep.addScript(pipelineScript);

            strCurrentPipelineStep = strPrevious;
        }

        System.out.println("--- pipeline: " + pipeline.getId());
        for(int i = 0; i < pipeline.getNumberOfSteps(); i++)
        {
            Step step = pipeline.getStep(i);
            System.out.println("step: " + step.getId());
            for(int ii = 0; ii < step.getNumberOfScripts(); ii++)
            {
                Script script = step.getScript(ii);
                System.out.println("script: " + script.getID());
            }
        }


    }

    //returns format yymmdd
    private String formatDate(String date)
    {
        return date.substring(2, 4) + date.substring(5, 7) + date.substring(8, 10);
    }

    public String now()
    {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());

    }

}
