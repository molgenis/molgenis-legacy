package org.molgenis.compute.commandline;

import org.molgenis.compute.ComputeJob;
import org.molgenis.compute.ComputeParameter;
import org.molgenis.compute.ComputeProtocol;
import org.molgenis.compute.pipelinemodel.*;
import org.molgenis.protocol.WorkflowElement;
import org.molgenis.protocol.WorkflowElementParameter;

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
public class WorkflowGeneratorCommandLine
{
    private static final String INTERPRETER_BASH = "bash";
    private static final String INTERPRETER_R = "R";
    private static final String INTERPRETER_JDL = "jdl";

    public static final String GRID = "grid";
    public static final String CLUSTER = "cluster";

    private static final String LOG = "log";// reserved word for logging feature type used in ComputeFeature

    private static final String DATE_FORMAT_NOW = "yyyy-MM-dd-HH-mm-ss";
    private SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);

    //format to run pipeline in compute
    private Pipeline pipeline = null;
    private Step currentStep = null;
    private String strCurrentPipelineStep = "INITIAL";

    private int stepNumber = 0;

    private List<ComputeParameter> allComputeParameters = null;

    //map of all compute features/values
    private Hashtable<String, String> weavingValues = null;
    Hashtable<String, String> userValues = null;

    //some necessary values
    private String applicationName = null;

    private ParameterWeaverCommandLine weaver = null;

    private boolean isToWriteLocally = false;
    private String localLocation = "/";

    private List<ComputeProtocol> cProtocols = null;
    private List<WorkflowElementParameter> wfeParameters = null;

    //quick solution
    //can be refactored later to have the same basis for command-line and db solutions

    private List<ComputeJob> applications = null;

    private String submit = null;
    //for the submission script a job should have an id number
    private Hashtable<String, String> submitIDs = null;
    int intSubmitID = -1;

    private String backend = null;

    public void processSingleWorksheet(ComputeBundle bundle,
                                       Hashtable<String, String> userValues,
                                       String applicationName /* should be unique somehow? */, String backend, String templateDir) throws IOException, ParseException
    {
        applications = new Vector<ComputeJob>();

        submitIDs = new Hashtable<String, String>();

        this.cProtocols = bundle.getComputeProtocols();
        this.wfeParameters = bundle.getWorkflowElementParameters();
        this.userValues = userValues;
        this.applicationName = applicationName;
        this.backend = backend;
        weaver = new ParameterWeaverCommandLine(templateDir);


        System.out.println(">>> generate apps");

        //create new pipeline and set current step to null
        pipeline = new Pipeline();
        currentStep = null;
        stepNumber = 0;

        //it would be nice to select compute features of only selected workflow
        allComputeParameters = bundle.getComputeParameters();
        System.out.println("we have so many features: " + allComputeParameters.size());

        //add few parameters
        //wholeWorkflowApp.setTime(now());

        pipeline.setId(applicationName);
        weaver.setJobID(applicationName);

        //process workflow elements
        List<WorkflowElement> workflowElements = bundle.getWorkflowElements();

        for (int i = 0; i < workflowElements.size(); i++)
        {
            WorkflowElement workflowElement = workflowElements.get(i);
            processWorkflowElement(workflowElement);
        }

        String logfile = weaver.getLogfilename();

        pipeline.setPipelinelogpath(logfile);
        //executePipeline(db, pipeline);
    }

    public Date now()
    {
        Calendar cal = Calendar.getInstance();
        return cal.getTime();
    }

    private void processWorkflowElement(WorkflowElement workflowElement)
            throws ParseException, IOException
    {

        weavingValues = new Hashtable<String, String>();
        weavingValues.putAll(userValues);

        System.out.println(">>> workflow element: " + workflowElement.getName());

        //create complex features, which will be processed after simple features
        Vector<ComputeParameter> featuresToDerive = new Vector<ComputeParameter>();

        //get protocol and template
        ComputeProtocol protocol = findProtocol(workflowElement.getProtocol_Name());


        //process compute features
        for (ComputeParameter computeFeature : allComputeParameters)
        {
            if (computeFeature.getDefaultValue() == null)
                continue;
            else if (computeFeature.getDefaultValue().contains("${"))
            {
                featuresToDerive.addElement(computeFeature);
            }
            else
            {
                weavingValues.put(computeFeature.getName(), computeFeature.getDefaultValue() != null ? computeFeature.getDefaultValue() : "");
            }
        }


        //process workflow element parameters
        List<WorkflowElementParameter> workflowElementParameters = findWorkflowElementParameters(workflowElement.getName());

        for (WorkflowElementParameter par : workflowElementParameters)
        {
            ComputeParameter feature = findComputeFeature(par.getParameter_Name());
            weavingValues.put(par.getParameter_Name(), feature.getDefaultValue());
        }

        generateComputeApplication(workflowElement, protocol, weavingValues, featuresToDerive);
    }

    private List<WorkflowElementParameter> findWorkflowElementParameters(String name)
    {
        List<WorkflowElementParameter> result = new Vector<WorkflowElementParameter>();
        for (WorkflowElementParameter p : wfeParameters)
        {
            if (p.getWorkflowElement_Name().equalsIgnoreCase(name))
                result.add(p);
        }
        return result;
    }

    private ComputeProtocol findProtocol(String protocol_name)
    {
        for (ComputeProtocol c : cProtocols)
        {
            if (c.getName().equalsIgnoreCase(protocol_name))
                return c;
        }
        return null;
    }

    private ComputeParameter findComputeFeature(String targetName)
    {
        for (ComputeParameter f : allComputeParameters)
        {
            if (f.getName().equalsIgnoreCase(targetName))
                return f;
        }
        return null;
    }

    private void generateComputeApplication(WorkflowElement workflowElement,
                                            ComputeProtocol protocol,
                                            Hashtable<String, String> weavingValues,
                                            Vector<ComputeParameter> featuresToDerive)
            throws IOException, ParseException
    {
        ComputeJob app = new ComputeJob();
        app.setProtocol(protocol);
        app.setWorkflowElement(workflowElement);
        app.setTime(now());

        String runId = this.weavingValues.get("runID");

        //String appName = applicationName + "_" + workflowElement.getName();// + "_" + pipelineElementNumber;
        String appName = runId + applicationName +"_" + workflowElement.getName();// + "_" + pipelineElementNumber;

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

            System.out.println("featureName: " + featureName);
            System.out.println("featurevalue: " + featureValue);

        }

        app.setInterpreter(protocol.getInterpreter());
        applications.add(app);

        String remoteLocation = this.weavingValues.get("outputdir");

        //create compute pipeline
        String scriptID = app.getName();
        weaver.setScriptID(scriptID);

        weaver.setDefaults();

        weaver.setWalltime(protocol.getWalltime());
        weaver.setCores(protocol.getCores() + "");
        weaver.setMemoryReq(protocol.getMem() + "");
        weaver.setClusterQueue(protocol.getClusterQueue());

        //at some point of time can be added for the verification
        weaver.setVerificationCommand("\n");

        weaver.setDatasetLocation(remoteLocation);
        String scriptRemoteLocation = remoteLocation;// + "scripts/";

        String logfile = weaver.getLogfilename();

        Script pipelineScript = null;

        if (backend.equalsIgnoreCase(WorkflowGeneratorCommandLine.CLUSTER))
        {
            String result = weaver.weaveFreemarker(protocolTemplate, weavingValues);
            app.setComputeScript(result);

            if (protocol.getInterpreter().equalsIgnoreCase(INTERPRETER_BASH))
            {
                pipelineScript = makeShScript(scriptID, scriptRemoteLocation, result);
            }
            else if (protocol.getInterpreter().equalsIgnoreCase(INTERPRETER_R))
            {
                pipelineScript = makeRScript(scriptID, scriptRemoteLocation, result);
                //TODO here also set script result to app like in cluster version
            }
        }
       else if (backend.equalsIgnoreCase(WorkflowGeneratorCommandLine.GRID))
        {
            pipelineScript = makeJDLScript(scriptID, scriptRemoteLocation, protocolTemplate, weavingValues);
        }

        pipeline.setPipelinelogpath(logfile);

        submitIDs.put(workflowElement.getName(), "" + intSubmitID);

        if (isToWriteLocally)
        {
            weaver.writeToFile(localLocation + scriptID + ".sh", new String(pipelineScript.getScriptData()));
        }

        List<String> strPreviousWorkflowElements = workflowElement.getPreviousSteps_Name();

        String strDependancy = "";
        for (String previousWorkflowElement : strPreviousWorkflowElements)
        {
            String jobSubmitID = submitIDs.get(previousWorkflowElement);

            if (strDependancy.equalsIgnoreCase(""))
                strDependancy += "$job_" + jobSubmitID;
            else
                strDependancy += ":$job_" + jobSubmitID;
        }

        if (!strDependancy.equalsIgnoreCase(""))
            strDependancy = "-W depend=afterok:" + strDependancy;

        weaver.setSubmitID("" + intSubmitID);
        weaver.setDependancy(strDependancy);

        String depend = weaver.makeSumbit();

        submit += depend;


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
                Step step = new Step(workflowElement.getName());
                step.setNumber(stepNumber);
                stepNumber++;
                currentStep = step;
                pipeline.addStep(step);
            }

            currentStep.addScript(pipelineScript);
            strCurrentPipelineStep = strPrevious;
        }
        intSubmitID++;
    }

    //here the first trial of generation for the grid
    //will be refactored later
    private Script makeJDLScript(String scriptID, String scriptRemoteLocation, String template, Hashtable<String, String> weavingValues)
    {
        String gridHeader = weaver.makeGridHeader();

        String downloadTop = weaver.processGridHeader(ParameterWeaverCommandLine.DO_DOWNLOAD, template, weavingValues);
        String executablesTop = weaver.processGridHeader(ParameterWeaverCommandLine.DO_EXECUTABLE, template, weavingValues);
        String uploadBottom = weaver.processGridHeader(ParameterWeaverCommandLine.DO_UPLOAD, template, weavingValues);

        String result = weaver.weaveFreemarker(template, weavingValues);

        //some special fields should be specified for the jdl file
        //error and output logs
        this.weavingValues.put("error_log", "err_" + scriptID + ".log");
        this.weavingValues.put("output_log", "out_" + scriptID + ".log");
        //extra files to be download and upload - now empty
        this.weavingValues.put("extra_inputs", "");
        this.weavingValues.put("extra_outputs", "");
        this.weavingValues.put("script_name", scriptID);
        this.weavingValues.put("script_location", scriptRemoteLocation);

        String jdlfile = weaver.makeJDL(this.weavingValues);

        System.out.println("name: " + scriptID);
        System.out.println("remote location: " + scriptRemoteLocation);
        System.out.println("command: " + result);

        String script = gridHeader + "\n"
                + downloadTop + "\n"
                + executablesTop + "\n"
                + result + "\n"
                + uploadBottom;

        System.out.println("jdl-file: \n" + jdlfile);
        System.out.println("-------\nscript: \n" + script);

        Script scriptFile = new GridScript(scriptID, scriptRemoteLocation, script.getBytes());
        FileToSaveRemotely jdlFile = new FileToSaveRemotely(scriptID + ".jdl", jdlfile.getBytes());
        scriptFile.addFileToTransfer(jdlFile);

        weaver.writeToFile(localLocation + scriptID + ".jdl", new String(jdlfile.getBytes()));

        return scriptFile;
    }

    private Script makeRScript(String scriptID, String scriptRemoteLocation, String result)
    {
        weaver.setActualCommand("cd " + scriptRemoteLocation + "\n R CMD BATCH " + scriptRemoteLocation + "myscript.R");
        String scriptFile = weaver.makeScript();
        Script script = new ClusterScript(scriptID, scriptRemoteLocation, scriptFile.getBytes());
        FileToSaveRemotely rScript = new FileToSaveRemotely("myscript.R", result.getBytes());
        script.addFileToTransfer(rScript);
        System.out.println("Rscript" + result);
        return script;
    }

    private Script makeShScript(String scriptID, String scriptRemoteLocation, String result)
    {
        weaver.setActualCommand(result);
        String scriptFile = weaver.makeScript();
        return new ClusterScript(scriptID, scriptRemoteLocation, scriptFile.getBytes());
    }

    public void setToWriteLocally(boolean toWriteLocally)
    {
        isToWriteLocally = toWriteLocally;
    }

    public void setLocalLocation(String localLocation)
    {
        this.localLocation = localLocation;
    }

    public String getFormattedTime()
    {
        //SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(now());
    }

    public List<ComputeJob> getComputeApplications()
    {
        return applications;
    }


    public void setNewRun()
    {
        submit = new String();
        intSubmitID = 1;
    }

    public void flashSumbitScript()
    {
        weaver.writeToFile(localLocation + "submit.sh", submit);
    }
}
