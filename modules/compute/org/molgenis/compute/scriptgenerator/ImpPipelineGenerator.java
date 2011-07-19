package org.molgenis.compute.scriptgenerator;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.molgenis.compute.pipelinemodel.Pipeline;
import org.molgenis.compute.pipelinemodel.Script;
import org.molgenis.compute.pipelinemodel.Step;
import org.molgenis.compute.scriptserver.Constants;
import org.molgenis.compute.sysexecutor.SysCommandExecutor;

import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;



public class ImpPipelineGenerator
{

    //templates locations
    public static final String TEMPLATE_0_2 = "templates/imputation/template-step0-2";
    public static final String TEMPLATE_3 = "templates/imputation/template-step3";
    public static final String TEMPLATE_4 = "templates/imputation/template-step4";
    public static final String TEMPLATE_5 = "templates/imputation/template-step5";
    public static final String TEMPLATE_6 = "templates/imputation/template-step6";
    public static final String TEMPLATE_7 = "templates/imputation/template-step7";

    //output directories
    private String outStep2 = "step02";
    private String outStep3 = "step3";
    private String outStep4 = "step4";
    private String outStep5 = "step5";
    private String outStep6 = "step6";
    private String outStep7 = "step7";

    ScriptGenerator scriptGenerator = new ScriptGenerator();

    private char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    public static void main(String[] args)
    {
        System.out.println(">>> start");
        String jobID = null, datasetLocation = null, outputLocation = null, hapmapLocation = null, hapmapBeagleLocation = null;
        int numberSamples = -1;

        if (args.length > 1)
        {
            jobID = args[0];
            datasetLocation = args[1];

            outputLocation = args[2];
            numberSamples = Integer.parseInt(args[3]);

            hapmapLocation = args[4];
            hapmapBeagleLocation = args[5];

        } else
        {
            System.exit(1);
        }


        ImpPipelineGenerator generator = new ImpPipelineGenerator();
        try
        {
            generator.generate(jobID, datasetLocation, hapmapLocation, hapmapBeagleLocation, numberSamples, outputLocation);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("... finish");
    }

    //scripts generation
    private void generate(String jobID, String datasetLocation, String hapmapLocation, String hapmapBeagleLocation, int numberSamples, String outputLocation) throws Exception
    {
        Hashtable parameters = new Hashtable();

        parameters.put(Constants.JOB_ID, jobID);
        parameters.put(Constants.DATASET_LOCATION, datasetLocation);
        parameters.put(Constants.HAPMAP_LOCATION, hapmapLocation);
        parameters.put(Constants.HAPMAP_BEAGLE_LOCATION, hapmapBeagleLocation);
        String strSize = "" + (int) Math.floor((double) (numberSamples/300));
        parameters.put(Constants.SIZE, strSize);

        SysCommandExecutor executor = new SysCommandExecutor();

        System.out.println(">>> generate step 0-2");
        executor.runCommand("mkdir " + outputLocation + System.getProperty("file.separator") + outStep2);
        scriptGenerator.generateAStep(TEMPLATE_0_2, parameters, "step02", outputLocation + System.getProperty("file.separator") + outStep2, jobID);

        System.out.println(">>> generate step 3");
        executor.runCommand("mkdir " + outputLocation + System.getProperty("file.separator") + outStep3);
        scriptGenerator.generateAStep(TEMPLATE_3, parameters, "step3", outputLocation + System.getProperty("file.separator") + outStep3, jobID, 23);

        System.out.println(">>> generate step 4");
        executor.runCommand("mkdir " + outputLocation + System.getProperty("file.separator") + outStep4);
        scriptGenerator.generateAStep(TEMPLATE_4, parameters, "step4", outputLocation + System.getProperty("file.separator") + outStep4, jobID, 23, numberSamples);

        System.out.println(">>> generate step 5");
        executor.runCommand("mkdir " + outputLocation + System.getProperty("file.separator") + outStep5);
        scriptGenerator.generateAStep(TEMPLATE_5, parameters, "step5", outputLocation + System.getProperty("file.separator") + outStep5, jobID, 23, numberSamples);

        System.out.println(">>> generate step 6");
        executor.runCommand("mkdir " + outputLocation + System.getProperty("file.separator") + outStep6);
        scriptGenerator.generateAStep(TEMPLATE_6, parameters, "step6", outputLocation + System.getProperty("file.separator") + outStep6, jobID);

        System.out.println(">>> generate step 7");
        executor.runCommand("mkdir " + outputLocation + System.getProperty("file.separator") + outStep7);
        scriptGenerator.generateAStep(TEMPLATE_7, parameters, "step7", outputLocation + System.getProperty("file.separator") + outStep7, jobID);
    }

    //pipeline hardcoded generation
    public Pipeline getImputationPipeline(Hashtable parameters, String remoteOutputLocation) throws Exception
    {
        Step step = null;
        Script script = null;
        Vector<Script> scripts = null;
        String stepID = null;

        String jobID = (String) parameters.get(Constants.JOB_ID);
        Pipeline pipeline = new Pipeline();
        pipeline.setId(jobID);

        stepID = "step02";
        System.out.println(">>> generate step " + stepID);
        step = new Step(stepID);
        script = scriptGenerator.generateScript(TEMPLATE_0_2, parameters, stepID, remoteOutputLocation);
        step.addScript(script);
        pipeline.addStep(step);

        stepID = "step3";
        System.out.println(">>> generate step " + stepID);
        step = new Step(stepID);
        scripts = scriptGenerator.generateScripts(TEMPLATE_3, parameters, stepID, remoteOutputLocation, 23);
        step.addScripts(scripts);
        pipeline.addStep(step);

        stepID = "step4";
        System.out.println(">>> generate step " + stepID);
        step = new Step(stepID);
        scripts = scriptGenerator.generateScripts(TEMPLATE_4, parameters, stepID, remoteOutputLocation, 23, 300);
        step.addScripts(scripts);
        pipeline.addStep(step);

        stepID = "step5";
        System.out.println(">>> generate step " + stepID);
        scripts = scriptGenerator.generateScripts(TEMPLATE_5, parameters, stepID, remoteOutputLocation, 23, 300);
        step.addScripts(scripts);
        step = new Step(stepID);
        pipeline.addStep(step);

        stepID = "step6";
        System.out.println(">>> generate step " + stepID);
        scripts = scriptGenerator.generateScripts(TEMPLATE_6, parameters, stepID, remoteOutputLocation, 23, 300);
        step.addScripts(scripts);
        step = new Step(stepID);
        pipeline.addStep(step);

        stepID = "step7";
        System.out.println(">>> generate step " + stepID);
        script = scriptGenerator.generateScript(TEMPLATE_7, parameters, stepID, remoteOutputLocation);
        step.addScript(script);
        step = new Step(stepID);
        pipeline.addStep(step);
        
        return pipeline;
    }
}
