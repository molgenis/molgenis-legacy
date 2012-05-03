package org.molgenis.commandline;

import org.molgenis.compute.ComputeJob;
import org.molgenis.compute.commandline.options.Options;
import org.molgenis.generator.Compute3JobGenerator;
import org.molgenis.generator.JobGenerator;
import org.molgenis.generator.ModelLoader;
import org.molgenis.protocol.Workflow;
import org.molgenis.util.Tuple;

import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 05/04/2012
 * Time: 09:35
 * To change this template use File | Settings | File Templates.
 */
public class CommandLineGenerator
{
    //here command-line parameters flags-synonyms
    private static final String PARAMETER_1 = "parametersfile";
    private static final String PARAMETER_2 = "p";
    private static final String WORKFLOW_1 = "workflowfile";
    private static final String WORKFLOW_2 = "w";
    private static final String WORKSHEET_1 = "worksheet";
    private static final String WORKSHEET_2 = "s";
    private static final String PROTOCOL_1 = "protocoldir";
    private static final String PROTOCOL_2 = "d";
    private static final String OUTPUT_1 = "outputscriptsdir";
    private static final String OUTPUT_2 = "o";
    private static final String BACKEND_1 = "cluster";
    private static final String BACKEND_2 = "grid";
    private static final String BACKEND_3 = "c";
    private static final String BACKEND_4 = "g";
    private static final String TEMP_1 = "templatesdir";
    private static final String TEMP_2 = "t";

    private static final String ERROR_MESSAGE = "command line parameters format: " +
            "-parametersfile|p=<file with parameters> \n" +
            "-workflowfile|w=<file with workflow description> \n" +
            "-worksheet|s=<input worksheet> \n" +
            "-protocoldir|d=<directory with protocol of the workflow> \n" +
            "-templatesdir|t=<directory with templates> \n" +
            "-cluster|c|grid|g=<the selected backend and remote directory where> \n" +
            "-outputscriptsdir|o=<output directory to write scripts> \n" +
            "<ID of the generation run>";

    private File fileWorksheet = null, fileParameters = null, fileWorkflow = null, dirProtocol = null;
    private Hashtable<String, String> config = new Hashtable<String, String>();

    private String backend = null;


    private void run() throws Exception
    {
        //load compute workflow
        ModelLoader loader = new ModelLoader();
        Workflow workflow = loader.loadWorkflowFromFiles(fileWorkflow, dirProtocol, fileParameters);

        //read worksheet
        List<Tuple> worksheet = loader.loadWorksheetFromFile(fileWorksheet);

        JobGenerator jobGenerator = new Compute3JobGenerator();
        //set configuration settings
        jobGenerator.setConfig(config);
        jobGenerator.setWorksheet(worksheet);

        //generate compute jobs
        //here, ComputeJobs can be generated also from DB given list of Targets
        Vector<ComputeJob> computeJobs = jobGenerator.generateComputeJobsFoldedWorksheet(workflow, worksheet, backend);
        //Vector<ComputeJob> computeJobs = jobGenerator.generateComputeJobsWorksheetWithFoldingNew(workflow, worksheet, backend);

        //generate actual analysis files
        boolean status = jobGenerator.generateActualJobs(computeJobs, backend, config);
    }

    public static void main(String[] args)
    {

        if (args.length == 0)
        {
            System.out.println("Error: provide parameters");
            System.out.println(ERROR_MESSAGE);
            System.exit(1);
        }

        //specifying command-line settings

        Options opt = new Options(args, Options.Prefix.DASH, Options.Multiplicity.ZERO_OR_ONE, 1);
        opt.getSet().addOption(PARAMETER_1, false, Options.Separator.EQUALS);
        opt.getSet().addOption(PARAMETER_2, false, Options.Separator.EQUALS);
        opt.getSet().addOption(WORKFLOW_1, false, Options.Separator.EQUALS);
        opt.getSet().addOption(WORKFLOW_2, false, Options.Separator.EQUALS);
        opt.getSet().addOption(WORKSHEET_1, false, Options.Separator.EQUALS);
        opt.getSet().addOption(WORKSHEET_2, false, Options.Separator.EQUALS);
        opt.getSet().addOption(PROTOCOL_1, false, Options.Separator.EQUALS);
        opt.getSet().addOption(PROTOCOL_2, false, Options.Separator.EQUALS);
        opt.getSet().addOption(OUTPUT_1, false, Options.Separator.EQUALS);
        opt.getSet().addOption(OUTPUT_2, false, Options.Separator.EQUALS);
        opt.getSet().addOption(BACKEND_1, false, Options.Separator.EQUALS);
        opt.getSet().addOption(BACKEND_2, false, Options.Separator.EQUALS);
        opt.getSet().addOption(BACKEND_3, false, Options.Separator.EQUALS);
        opt.getSet().addOption(BACKEND_4, false, Options.Separator.EQUALS);
        opt.getSet().addOption(TEMP_1, false, Options.Separator.EQUALS);
        opt.getSet().addOption(TEMP_2, false, Options.Separator.EQUALS);

        //checking for correctness

        boolean isCorrect = opt.check();

        String generationID = opt.getSet().getData().get(0);

        if (!isCorrect)
        {
            System.out.println(opt.getCheckErrors());
            System.out.println(ERROR_MESSAGE);
            System.exit(1);
        }

        CommandLineGenerator generator = new CommandLineGenerator();

        checkSynonymParameters(generator, opt, "parameters file", PARAMETER_1, PARAMETER_2);
        checkSynonymParameters(generator, opt, "workflow file", WORKFLOW_1, WORKFLOW_2);
        checkSynonymParameters(generator, opt, "worksheet", WORKSHEET_1, WORKSHEET_2);
        checkSynonymParameters(generator, opt, "protocol directory", PROTOCOL_1, PROTOCOL_2);
        checkSynonymParameters(generator, opt, "output directory", OUTPUT_2, OUTPUT_1);
        checkSynonymParameters(generator, opt, "backend", BACKEND_1, BACKEND_2, BACKEND_3, BACKEND_4);
        checkSynonymParameters(generator, opt, "template directory", TEMP_1, TEMP_2);

        System.out.println("... command line parameters are parsed successfully");

        generator.setGenerationID(generationID);

        //actual generation
        try
        {
            generator.run();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("... done");
        System.exit(0);
    }

    private void setParametersFile(String resultValue)
    {
        fileParameters = new File(resultValue);
    }

    //lazy code
    private static void checkSynonymParameters(CommandLineGenerator generation, Options opt, String backend, String backend1, String backend2, String backend3, String backend4)
    {
        if (!(opt.getSet().isSet(backend1) ^ opt.getSet().isSet(backend2) ^ opt.getSet().isSet(backend3) ^ opt.getSet().isSet(backend4)))
        {
            System.out.println(backend + " is not correctly specified");
            System.out.println(ERROR_MESSAGE);
            System.exit(1);
        }

        String result = null;

        if (opt.getSet().isSet(backend1))
        {
             generation.setBackEndDirectory(opt.getSet().getOption(backend1).getResultValue(0));
            generation.setBackEnd(JobGenerator.CLUSTER);
        }
        else if (opt.getSet().isSet(backend2))
        {
            generation.setBackEndDirectory(opt.getSet().getOption(backend2).getResultValue(0));
            generation.setBackEnd(JobGenerator.GRID);

        }
        else if (opt.getSet().isSet(backend3))
        {
            generation.setBackEndDirectory(opt.getSet().getOption(backend3).getResultValue(0));
            generation.setBackEnd(JobGenerator.CLUSTER);
        }
        else if (opt.getSet().isSet(backend4))
        {
            generation.setBackEndDirectory(opt.getSet().getOption(backend4).getResultValue(0));
            generation.setBackEnd(JobGenerator.GRID);

        }
    }

    private void setBackEndDirectory(String result)
    {
        //backEndDir = result;
        this.config.put(JobGenerator.BACK_END_DIR, result);
    }

    private static void checkSynonymParameters(CommandLineGenerator generation, Options opt, String parameter, String str1, String str2)
    {

        if (!(opt.getSet().isSet(str1) ^ opt.getSet().isSet(str2)))
        {
            System.out.println(parameter + " is not correctly specified");
            System.out.println(ERROR_MESSAGE);
            System.exit(1);
        }

        String result = null;

        if (opt.getSet().isSet(str1))
            result = opt.getSet().getOption(str1).getResultValue(0);
        else if (opt.getSet().isSet(str2))
            result = opt.getSet().getOption(str2).getResultValue(0);

        if (parameter.equalsIgnoreCase("parameters file"))
        {
            System.out.println("parameters file: " + result);
            generation.setParametersFile(result);
        }
        else if (parameter.equalsIgnoreCase("workflow file"))
        {
            System.out.println("workflow file: " + result);
            generation.setWorkflowFile(result);

        }
        else if (parameter.equalsIgnoreCase("worksheet"))
        {
            System.out.println("worksheet file: " + result);
            generation.setWorksheet(result);
        }
        else if (parameter.equalsIgnoreCase("protocol directory"))
        {
            System.out.println("protocol directory: " + result);
            generation.setProtocolDir(result);
        }
        else if (parameter.equalsIgnoreCase("output directory"))
        {
            System.out.println("output directory: " + result);
            generation.setOutputDir(result);

        }
        else if (parameter.equalsIgnoreCase("template directory"))
        {
            System.out.println("template directory: " + result);
            generation.setTemplatesDir(result);
        }

    }

    private void setProtocolDir(String result)
    {
        dirProtocol = new File(result);
    }

    private void setWorkflowFile(String result)
    {
        fileWorkflow = new File(result);
    }


    private void setTemplatesDir(String templatesdir)
    {
        this.config.put(JobGenerator.TEMPLATE_DIR, templatesdir);
    }

    private void setOutputDir(String resultValue)
    {
        this.config.put(JobGenerator.OUTPUT_DIR, resultValue);
    }

    private void setBackEnd(String backend)
    {
        this.backend = backend;
    }


    public void setWorksheet(String str)
    {
        fileWorksheet = new File(str);
    }

    public void setGenerationID(String str)
    {
        this.config.put(JobGenerator.GENERATION_ID, str);
    }

}
