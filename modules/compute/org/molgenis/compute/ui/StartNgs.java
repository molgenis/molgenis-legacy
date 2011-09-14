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
    private static final String PARAMETER = "parameter";//reserved word to show that ComputeFeatureValue goes from WorkflowElementParameter
    private static final String LOG = "log";// reserved word for logging feature type used in ComputeFeature


    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd-HH-mm-ss";
    private SimpleDateFormat sdf = null;

    //format to run pipeline in compute
    private Pipeline pipeline = null;
    private Step currentStep = null;
    private String strCurrentPipelineStep = "INITIAL";
    private int pipelineElementNumber = 0;

    private List<ComputeFeature> allComputeFeatures = null;

    //compute
    private MCF mcf = null;


    //map of all compute features/values
    private Hashtable<String, String> weavingValues = null;
    private Hashtable<String, String> userValues = null;
    private HashMap<String, ComputeFeature> computeFeatures = new HashMap<String, ComputeFeature>();

    //target of pipeline
    private LibraryLane target = null;
    //whole workflow application
    private ComputeApplication wholeWorkflowApp = null;

//    private Calendar cal = Calendar.getInstance();
    private WorkflowParametersWeaver weaver = new WorkflowParametersWeaver();

    //responsible for updating DB with progress
    private DatabaseUpdater updater = new DatabaseUpdater();

    boolean flagJustGenerate = false;

    private enum UserParameter
    {
        sample, flowcell, lane, barcode, machine, date, capturing, project;
    }

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
        testPipeline.setId("step" + stepID + "_" + pipeline.getId());
        testPipeline.setPipelinelogpath(pipeline.getPipelinelogpath());
        testPipeline.setMonitor(pipeline.getMonitor());
        System.out.println("!! step " + pipeline.getStep(stepID).toString());
        testPipeline.addStep(pipeline.getStep(stepID));

        executePipeline(db, testPipeline);
    }

    public void buttonGenerate(Database db, Tuple request) throws Exception
    {
        flagJustGenerate = true;
        buttonStart(db, request);
        flagJustGenerate = false;
    }


    public void buttonStart(Database db, Tuple request) throws Exception
    {
        if (mcf == null)
        {
            HttpServletRequestTuple req = (HttpServletRequestTuple) request;
            ServletContext servletContext = req.getRequest().getSession().getServletContext();
            mcf = (MCF) servletContext.getAttribute("MCF");
        }


        System.out.println(">>> generate apps");

        pipeline = new Pipeline();
        userValues = new Hashtable<String, String>();
        pipelineElementNumber = 0;

        ScreenController<?> parentController = (ScreenController<?>) this.getParent();
        FormModel<Worksheet> parentForm = (FormModel<Worksheet>) ((FormController) parentController).getModel();
        Worksheet data = parentForm.getRecords().get(0);

        Date date = data.getSequencingStartDate();

        //should be removed later
        String strDate = "00.00.0000";
        if (date != null)
            strDate = formatDate("" + date);

        String strLane = data.getLane();
        String strMachine = data.getSequencer();
        String strFlowcell = data.getFlowcell();
        String strBarcode = data.getBarcode();
        strBarcode = adjustBarcode(strBarcode);
        String strSample = data.getExternalSampleID();
        String strCapturing = data.getCapturingKit();
        String strProject = data.getProject();



        //application for the whole workflow
        wholeWorkflowApp = new ComputeApplication();

        //we have only one workflow
        Workflow workflow = db.query(Workflow.class).find().get(0);
        wholeWorkflowApp.setProtocol(workflow);
        wholeWorkflowApp.setInterpreter("WorkflowInterpreter");

        //take all compute features, not so many for ngs pipeline
        // having one workflow makes life easy
        //otherwise - select all features of workflow
        allComputeFeatures = db.query(ComputeFeature.class).find();
        System.out.println("we have so many features: " + allComputeFeatures.size());

        //creating map feature/value
        for (ComputeFeature computeFeature : allComputeFeatures)
        {
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
                    case project:
                        userValues.put(computeFeature.getName(), strProject);
                        break;
                }
            }
        }

        System.out.println("workflow" + workflow.getName());

        //our current targer is FlowcellLaneSample
        target = db.query(LibraryLane.class).equals(LibraryLane.LANE, strLane).
                equals(LibraryLane.FLOWCELL_NAME, strFlowcell).
                equals(LibraryLane.SAMPLE_NAME, strSample).find().get(0);


        //add few parameters
        // wholeWorkflowApp.setComputeResource("cluster");//for time being
        wholeWorkflowApp.setTime(now());

        //set app name everywhere and add to database
        sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        String appName = strLane + "_" + strFlowcell + "_" + strSample + "_" + sdf.format(now());
        wholeWorkflowApp.setName(appName);
        pipeline.setId(appName);
        weaver.setJobID(appName);
//        db.beginTx();
        db.add(wholeWorkflowApp);

        //process workflow elements
        List<WorkflowElement> workflowElements = db.query(WorkflowElement.class).equals(WorkflowElement.WORKFLOW, workflow.getId()).find();

        for (int i = 0; i < workflowElements.size(); i++)
        {
            WorkflowElement workflowElement = workflowElements.get(i);
            processWorkflowElement(db, request, workflowElement);
        }

        String logfile = weaver.getLogfilename();

        pipeline.setPipelinelogpath(logfile);

        db.commitTx();
        getModel().setSuccess("update succesfull");

        executePipeline(db, pipeline);
    }

    private String adjustBarcode(String strBarcode)
    {
        int start = strBarcode.lastIndexOf(" ");
        String result =  strBarcode.substring(start + 1);
        return result;
    }

    private void executePipeline(Database db, Pipeline pipeline)
    {
        if (mcf != null && !flagJustGenerate)
        {
            mcf.setPipeline(pipeline);

            //start monitoring for database update
            if (!updater.isStarted())
            {
                updater.setSettings(10, 10);
                updater.setDatabase(db);
                updater.setMCF(mcf);
                updater.start();
            }
        }
        else
            System.out.println(pipeline.toString());

    }


    private void processWorkflowElement(Database db, Tuple request, WorkflowElement workflowElement)
            throws DatabaseException, ParseException, IOException
    {

        weavingValues = new Hashtable<String, String>();
        weavingValues.putAll(userValues);

        System.out.println(">>> workflow element: " + workflowElement.getName());

        //create complex features, which will be processed after simple features
        Vector<ComputeFeature> featuresToDerive = new Vector<ComputeFeature>();

        //get protocol and template
        ComputeProtocol protocol = db.findById(ComputeProtocol.class, workflowElement.getProtocol_Id());

        //fill vectors/hashtable with complex/simple compute features
        //only one feature to iterate over is hardcoded
        ComputeFeature fastqcFeature = null;

        for (ComputeFeature computeFeature : allComputeFeatures)
        {
//            System.out.println("input feature: " + computeFeature.getName() + " --> " + computeFeature.getDefaultValue());
            if (computeFeature.getIsUser())// || computeFeature.getDefaultValue().equalsIgnoreCase(PARAMETER))
                continue;
            else if (computeFeature.getIsDerived())
            {
                featuresToDerive.addElement(computeFeature);
            }
            else if (computeFeature.getIterateOver())
            {
                fastqcFeature = computeFeature;
                weavingValues.put(computeFeature.getName(), computeFeature.getDefaultValue());
            }
            else
            {
                weavingValues.put(computeFeature.getName(), computeFeature.getDefaultValue());
            }
        }


        //find value of workflowelementparameter
        List<WorkflowElementParameter> workflowElementParameters = db.query(WorkflowElementParameter.class).
                equals(WorkflowElementParameter.WORKFLOWELEMENT, workflowElement.getId()).find();

        for (WorkflowElementParameter par : workflowElementParameters)
        {
//            System.out.println("par: " + par.getFeature_Name() + " --> " + par.getTarget_Name());
            ComputeFeature feature = computeFeatures.get(par.getTarget_Name());
//            System.out.println("par: " + par.getFeature_Name() + " --> " + feature.getDefaultValue());
            weavingValues.put(par.getFeature_Name(), feature.getDefaultValue());
        }

        //create compute applications
        //todo iterate over several features
        //now just hardcoded for fastqc index (only one feature to iterate over)
        if (workflowElement.getName().equalsIgnoreCase("FastqcElement"))
        {
            for (int i = 1; i < 3; i++)
            {
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
            throws IOException, DatabaseException, ParseException
    {
        ComputeApplication app = new ComputeApplication();
        app.setProtocol(protocol);
        //test setting of workflow element
        app.setWorkflowElement(workflowElement);
        app.setTime(now());
        //app.setComputeResource("cluster");

        String appName = "ngs_" + pipeline.getId() + "_" + workflowElement.getName() + "_" + pipelineElementNumber;
        app.setName(appName);
        System.out.println("---application---> " + appName);

        String protocolTemplate = protocol.getScriptTemplate();

//        System.out.println("--- template \n" + protocolTemplate);

        //weave complex features
        for (int i = 0; i < featuresToDerive.size(); i++)
        {
            ComputeFeature feature = featuresToDerive.elementAt(i);
            String featureName = feature.getName();
            String featureTemplate = feature.getDefaultValue();

            String featureValue = weaver.weaveFreemarker(featureTemplate, weavingValues);
//            System.out.println("complex-feature: " + featureName + " --> " + featureValue);
            weavingValues.put(featureName, featureValue);
        }

        String result = weaver.weaveFreemarker(protocolTemplate, weavingValues);
        app.setComputeScript(result);
        app.setInterpreter("bash");
        db.add(app);

        List<ComputeApplication> res = db.query(ComputeApplication.class).equals(ComputeApplication.NAME, app.getName()).find();
        if (res.size() != 1)
            throw new DatabaseException("ERROR while inserting into db");

        app = res.get(0);

        Set entries = weavingValues.entrySet();
        Iterator it = entries.iterator();

        //this is used for database update with ComputeAppPaths
        Vector<String> logpathfiles = new Vector<String>();

        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();
            String name = (String) entry.getKey();
            String value = (String) entry.getValue();

            ObservedValue observedValue = new ObservedValue();
            observedValue.setValue(value);
            observedValue.setProtocolApplication(app);
            observedValue.setTarget(target);
            ComputeFeature feature = computeFeatures.get(name);
            if (feature.getFeatureType().equalsIgnoreCase(LOG))
            {
                logpathfiles.addElement(value);
            }

            observedValue.setFeature(feature);
            db.add(observedValue);
        }

        System.out.println("script \n " + result);

        pipelineElementNumber++;

        //create compute pipeline
        String scriptID = app.getName();
        weaver.setScriptID(scriptID);
        weaver.setActualCommand(result);

        weaver.setDefaults();

        if(protocol.getWalltime() != null)
            weaver.setWalltime(protocol.getWalltime());
        if(protocol.getClusterQueue() != null)
            weaver.setClusterQueue(protocol.getClusterQueue());
        if(protocol.getCores() != null)
            weaver.setCores(protocol.getCores()+"");
        if(protocol.getMemoryReq() != null)
            weaver.setMemoryReq(protocol.getMemoryReq()+"");

        //extra for verification test (count # reads)
//        if (app.getWorkflowElement_Name().equalsIgnoreCase("BamIndexElement1"))
//        {
//            String scriptVerification = weaver.makeVerificationScript();
//            weaver.setVerificationCommand(scriptVerification);
//        }
//        else
        weaver.setVerificationCommand("\n");
        //finish extra

        String remoteLocation = "/data/gcc/test_george/";
        //String remoteLocation = "/target/gpfs2/gcc/home/fvandijk/computescripts/";

        weaver.setDatasetLocation(remoteLocation);

        //write file for testing purposes
        String scriptFile = weaver.makeScript();

        String logfile = weaver.getLogfilename();
        pipeline.setPipelinelogpath(logfile);

        //weaver.writeToFile("/test/" + pipelineElementNumber + scriptID, scriptFile);
        weaver.writeToFile("/home/gbyelas/test/" + pipelineElementNumber + scriptID, scriptFile);
        //weaver.writeToFile("/home/fvandijk/test/" + pipelineElementNumber + scriptID, scriptFile);

        //todo rewrite pipeline generation
        //look into proper choose of logfile and all path settings
        List<String> strPreviousWorkflowElements = workflowElement.getPreviousSteps_Name();

        String scriptRemoteLocation = remoteLocation + "/scripts/";

         Script pipelineScript = new Script(scriptID, scriptRemoteLocation, scriptFile.getBytes());

         if(protocol.getClusterQueue().equalsIgnoreCase("short"))
            pipelineScript.setShort(true);

        if (strPreviousWorkflowElements.size() == 0)//script does not depend on other scripts
        {
            if (currentStep == null) //it is a first script in the pipeline
            {
                //Step step = new Step("step_" + app.getName());
                Step step = new Step(workflowElement.getName());
                currentStep = step;
                pipeline.addStep(step);
            }

            System.out.println("scriptID" + scriptID);

            currentStep.addScript(pipelineScript);
        }
        else //scripts depends on previous scripts
        {
            String strPrevious = strPreviousWorkflowElements.get(0);

            if (!strPrevious.equalsIgnoreCase(strCurrentPipelineStep))
            {
                //Step step = new Step("step_" + app.getName());
                Step step = new Step(workflowElement.getName());
                currentStep = step;
                pipeline.addStep(step);
            }

            currentStep.addScript(pipelineScript);
            strCurrentPipelineStep = strPrevious;
        }

        //here ComputeAppPaths generation
        ComputeAppPaths appPaths = new ComputeAppPaths();
        appPaths.setApplication(app);
        appPaths.setErrpath(weaver.getErrfilename());
        appPaths.setOutpath(weaver.getOutfilename());
        appPaths.setExtralog(weaver.getExtralogfilename());

        if (logpathfiles.size() > 0)
            for (int iii = 0; iii < logpathfiles.size(); iii++)
                appPaths.addLogpath(logpathfiles.elementAt(iii));

        updater.addComputeAppPath(appPaths);
    }

    //returns format yymmdd
    private String formatDate(String date)
    {
        return date.substring(2, 4) + date.substring(5, 7) + date.substring(8, 10);
    }

    public Date now()
    {
        Calendar cal = Calendar.getInstance();
        return cal.getTime();
   }

}
