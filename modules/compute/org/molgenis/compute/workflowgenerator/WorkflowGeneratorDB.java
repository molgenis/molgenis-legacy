package org.molgenis.compute.workflowgenerator;

import org.molgenis.compute.ComputeJob;
import org.molgenis.compute.ComputeParameter;
import org.molgenis.compute.ComputeProtocol;
import org.molgenis.compute.pipelinemodel.*;
import org.molgenis.compute.scriptserver.MCF;
import org.molgenis.compute.ui.ComputeAppPaths;
import org.molgenis.compute.ui.DatabaseUpdater;
import org.molgenis.compute.ui.DatabaseUpdaterGridGain;
import org.molgenis.compute.ui.DatabaseUpdaterSsh;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Workflow;
import org.molgenis.protocol.WorkflowElement;
import org.molgenis.protocol.WorkflowElementParameter;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 18/10/2011
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowGeneratorDB
{
    private static final String INTERPRETER_BASH = "bash";
    private static final String INTERPRETER_R = "R";
    private static final String INTERPRETER_JDL = "jdl";

    public static final String ENV_CLUSTER = "cluster";
    public static final String ENV_GRID = "grid";

    private String env = null;
    
    private boolean flagJustGenerate = false;

    private static final String LOG = "log";// reserved word for logging feature type used in ComputeFeature

    private static final String DATE_FORMAT_NOW = "yyyy-MM-dd-HH-mm-ss";
    private SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);

    //format to run pipeline in compute
    private Pipeline pipeline = null;
    private Step currentStep = null;
    private String strCurrentPipelineStep = "INITIAL";
    private int pipelineElementNumber = 0;

    private int stepNumber = 0;

    private List<ComputeParameter> allComputeParameters = null;

    //compute
    private MCF mcf = null;
    private DatabaseUpdater updater = null;

    //map of all compute features/values
    private Hashtable<String, String> weavingValues = null;
    Hashtable<String, String> userValues = null;

    //whole workflow application
    private ComputeJob wholeWorkflowApp = null;

    //some necessary values
    private Workflow target = null;
    private String applicationName = null;

    private ParameterWeaver weaver = new ParameterWeaver();

    private String remoteLocation = null;
    private boolean isToWriteLocally = false;
    private String localLocation = "/";


    public void processSingleWorksheet(Database db, Tuple request,
                                       Hashtable<String, String> userValues,
                                       Workflow workflow,
                                       String applicationName /* should be unique somehow */,
                                       String environment) throws Exception
    {
        this.env = environment;

        this.userValues = userValues;
        this.target = workflow;
        this.applicationName = applicationName;

        if (!db.inTx())
            db.beginTx();

        if (mcf == null)
        {
            HttpServletRequestTuple req = (HttpServletRequestTuple) request;
            ServletContext servletContext = req.getRequest().getSession().getServletContext();
            mcf = (MCF) servletContext.getAttribute("MCF");

            createDatabaseUpdater(mcf);
        }

        System.out.println(">>> generate apps");

        //create new pipeline and set current step to null
        pipeline = new Pipeline();
        currentStep = null;
        stepNumber = 0;

        pipelineElementNumber = 0;

        //application for the whole workflow
        wholeWorkflowApp = new ComputeJob();

        //get the chosen workflow
//        Workflow workflow = db.query(Workflow.class).find().get(0);
        wholeWorkflowApp.setProtocol(workflow);
        wholeWorkflowApp.setInterpreter("WorkflowInterpreter");

        //it would be nice to select compute features of only selected workflow
        allComputeParameters = db.query(ComputeParameter.class).equals(ComputeParameter.WORKFLOW, workflow.getId()).find();
        //allComputeParameters = db.query(ComputeParameter.class).find();
        System.out.println("we have so many features: " + allComputeParameters.size());

        System.out.println("workflow" + workflow.getName());

        //add few parameters
        wholeWorkflowApp.setTime(now());

        //set app name everywhere and add to database
        wholeWorkflowApp.setName(applicationName);
        pipeline.setId(applicationName);
        weaver.setJobID(applicationName);
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
        executePipeline(db, pipeline);
    }

    private void createDatabaseUpdater(MCF mcf)
    {
        if (mcf.getBasis().equalsIgnoreCase(MCF.GRID))
            updater = new DatabaseUpdaterGridGain(mcf);
        else if ((mcf.getBasis().equalsIgnoreCase(MCF.SSH)))
            updater = new DatabaseUpdaterSsh(mcf);
    }

    public Date now()
    {
        Calendar cal = Calendar.getInstance();
        return cal.getTime();
    }

    public void executePipeline(Database db, Pipeline pipeline)
    {
        if (mcf != null && !flagJustGenerate)
        {
            mcf.setClusterPipeline(pipeline);

            if (!updater.isStarted())
            {
                updater.setSettings(20, 20);
                updater.setDatabase(db);
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
        Vector<ComputeParameter> featuresToDerive = new Vector<ComputeParameter>();

        //get protocol and template
        ComputeProtocol protocol = db.findById(ComputeProtocol.class, workflowElement.getProtocol_Id());


        //process compute features
        for (ComputeParameter computeFeature : allComputeParameters)
        {
            if (computeFeature.getIsUser())
                continue;
            else if (computeFeature.getDefaultValue().contains("${"))
            {
                featuresToDerive.addElement(computeFeature);
            }
            else
            {
                weavingValues.put(computeFeature.getName(), computeFeature.getDefaultValue());
            }
        }


        //process workflow element parameters
        List<WorkflowElementParameter> workflowElementParameters = db.query(WorkflowElementParameter.class).
                equals(WorkflowElementParameter.WORKFLOWELEMENT, workflowElement.getId()).find();

        for (WorkflowElementParameter par : workflowElementParameters)
        {
            ComputeParameter feature = findComputeFeature(par.getParameter_Name());
            weavingValues.put(par.getParameter_Name(), feature.getDefaultValue());
        }

        generateComputeApplication(db, request, workflowElement, protocol, weavingValues, featuresToDerive);
    }

    private ComputeParameter findComputeFeature(String targetName)
    {
        for(ComputeParameter f : allComputeParameters)
        {
            if(f.getName().equalsIgnoreCase(targetName))
                return f;
        }
        return null;
    }

    private void generateComputeApplication(Database db, Tuple request,
                                            WorkflowElement workflowElement,
                                            ComputeProtocol protocol,
                                            Hashtable<String, String> weavingValues,
                                            Vector<ComputeParameter> featuresToDerive)
            throws IOException, DatabaseException, ParseException
    {
        ComputeJob app = new ComputeJob();
        app.setProtocol(protocol);
        app.setWorkflowElement(workflowElement);
        app.setTime(now());

        String appName = applicationName + "_" + workflowElement.getName() + "_" + pipelineElementNumber;
        app.setName(appName);
        System.out.println("---application---> " + appName);

        String protocolTemplate = protocol.getScriptTemplate();

        //weave complex features
        for (int i = 0; i < featuresToDerive.size(); i++)
        {
            ComputeParameter feature = featuresToDerive.elementAt(i);
            String featureName = feature.getName();
            String featureTemplate = feature.getDefaultValue();

            String featureValue = weaver.weaveFreemarker(featureTemplate, weavingValues);
            weavingValues.put(featureName, featureValue);
        }

        String result = weaver.weaveFreemarker(protocolTemplate, weavingValues);
        app.setComputeScript(result);
        app.setInterpreter(protocol.getInterpreter());
        db.add(app);

        List<ComputeJob> res = db.query(ComputeJob.class).equals(ComputeJob.NAME, app.getName()).find();
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
            observedValue.setTarget(target.getId());
            ComputeParameter feature = findComputeFeature(name);
            if (feature.getDataType().equalsIgnoreCase(LOG))
            {
                logpathfiles.addElement(value);
            }

            observedValue.setFeature(feature.getId());
            System.out.println(feature.getName() + "->" + value);

            db.add(observedValue);
        }

        pipelineElementNumber++;

        //create compute pipeline
        String scriptID = app.getName();
        weaver.setScriptID(scriptID);


        weaver.setDefaults();

        if (protocol.getWalltime() != null)
        {
            weaver.setWalltime(protocol.getWalltime());

            //quick fix for the cluster queue
            if(protocol.getWalltime().equalsIgnoreCase("00:30:00"))
            {
                weaver.setClusterQueue("short");
            }
            else
                weaver.setClusterQueue("nodes");
        }
        if (protocol.getCores() != null)
            weaver.setCores(protocol.getCores() + "");
        if (protocol.getMem() != null)
            weaver.setMemoryReq(protocol.getMem() + "");

        //at some point of time can be added for the verification
        weaver.setVerificationCommand("\n");

        weaver.setDatasetLocation(remoteLocation);
        String scriptRemoteLocation = remoteLocation + "scripts/";

        String logfile = weaver.getLogfilename();

        Script pipelineScript = null;
        
        if(protocol.getInterpreter().equalsIgnoreCase(INTERPRETER_BASH))
        {
            pipelineScript = makeShScript(scriptID, scriptRemoteLocation, result);
        }
        else if(protocol.getInterpreter().equalsIgnoreCase(INTERPRETER_R))
        {
            pipelineScript = makeRScript(scriptID, scriptRemoteLocation, result);
        }
        else if(protocol.getInterpreter().equalsIgnoreCase(INTERPRETER_JDL))
        {
            pipelineScript = makeJDLScript(scriptID, scriptRemoteLocation, result);
        }

        pipeline.setPipelinelogpath(logfile);

        if(isToWriteLocally)
            weaver.writeToFile(localLocation + pipelineElementNumber + scriptID, new String(pipelineScript.getScriptData()));

        List<String> strPreviousWorkflowElements = workflowElement.getPreviousSteps_Name();

        if (strPreviousWorkflowElements.size() == 0)//script does not depend on other scripts
        {
            if (currentStep == null) //it is a first script in the pipeline
            {
                Step step = new Step(workflowElement.getName());
                step.setNumber(stepNumber);
                stepNumber++;
                currentStep = step;
                pipeline.addStep(step);
            }

            currentStep.addScript(pipelineScript);
        }
        else //scripts depends on previous scripts
        {
            String strPrevious = strPreviousWorkflowElements.get(0);

            if (!strPrevious.equalsIgnoreCase(strCurrentPipelineStep))
            {
                //Step step = new Step("step_" + app.getName());
                Step step = new Step(workflowElement.getName());
                step.setNumber(stepNumber);
                stepNumber++;
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

    //here the first trial of generation for the grid
    //will be refactored later
    private Script makeJDLScript(String scriptID, String scriptRemoteLocation, String result)
    {
        String gridHeader = weaver.makeGridHeader();

        String downloadTop = weaver.makeGridDownload(weavingValues);
        String uploadBottom = weaver.makeGridUpload(weavingValues);

        //while testing - hardcoded
        //some special fields should be specified for the jdl file
        //error and output logs
        weavingValues.put("error_log", "err_" + scriptID +".log");
        weavingValues.put("output_log", "out_" + scriptID + ".log");
        //extra files to be download and upload - now empty
        weavingValues.put("extra_inputs","");
        weavingValues.put("extra_outputs","");

        weavingValues.put("script_location", scriptRemoteLocation);
        //
        String jdlfile = weaver.makeJDL(weavingValues);

        System.out.println("name: " + scriptID);
        System.out.println("remote location: " +scriptRemoteLocation);
        System.out.println("command: " + result);

        String script = gridHeader + "\n"
                      + downloadTop + "\n"
                      + result + "\n"
                      + uploadBottom;

        System.out.println("jdl-file: \n" +jdlfile);
        System.out.println("-------\nscript: \n" + script);

        Script scriptFile = new GridScript(scriptID, scriptRemoteLocation, script.getBytes());
        FileToSaveRemotely jdlFile = new FileToSaveRemotely(scriptID + ".jdl", jdlfile.getBytes());
        scriptFile.addFileToTransfer(jdlFile);

        return scriptFile;
    }

    private Script makeRScript(String scriptID, String scriptRemoteLocation, String result)
    {
        weaver.setActualCommand("cd " + scriptRemoteLocation + "\n R CMD BATCH "+ scriptRemoteLocation +"myscript.R");
        String scriptFile = weaver.makeScript();
        Script script = new ClusterScript(scriptID, scriptRemoteLocation, scriptFile.getBytes());
        FileToSaveRemotely rScript = new FileToSaveRemotely("myscript.R", result.getBytes());
        script.addFileToTransfer(rScript);
        System.out.println(script.toString());
        return script;

    }

    private Script makeShScript(String scriptID, String scriptRemoteLocation, String result)
    {
        weaver.setActualCommand(result);
        String scriptFile = weaver.makeScript();
        return new ClusterScript(scriptID, scriptRemoteLocation, scriptFile.getBytes());
    }

    //root remote location should be set
    public void setRemoteLocation(String remoteLocation)
    {
        this.remoteLocation = remoteLocation;
    }

    public void setToWriteLocally(boolean toWriteLocally)
    {
        isToWriteLocally = toWriteLocally;
    }

    public void setLocalLocation(String localLocation)
    {
        this.localLocation = localLocation;
    }

    public Pipeline getCurrectPipeline()
    {
        return pipeline;
    }

    public void setFlagJustGenerate(boolean b)
    {
        flagJustGenerate = b;
    }

    public String getFormattedTime()
    {
        //SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(now());
    }
}
