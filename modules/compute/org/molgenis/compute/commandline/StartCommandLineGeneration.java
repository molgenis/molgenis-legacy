package org.molgenis.compute.commandline;

import org.molgenis.compute.commandline.options.Options;

import java.io.File;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA. User: georgebyelas Date: 02/11/2011 Time: 14:36 To
 * change this template use File | Settings | File Templates.
 */
public class StartCommandLineGeneration
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
            "-cluster|c|grid|g=<the selected backend> \n" +
            "-outputscriptsdir|o=<output directory to write scripts> \n" +
            "<ID of the generation run>";

    private File workingDir = null;
    //private File workflowDir = null;
    private File fileWorksheet = null, fileParameters = null, fileWorkflow = null, dirProtocol = null;
    private String applicationName = null;
    private ComputeBundle computeBundle = null;
    private Hashtable<String, String> userValues = new Hashtable<String, String>();

    private String backend = null;
    private String templateDir = null;
    private String backEndDir   = null;

    private void run() throws Exception
    {
        //computeBundle = new ComputeBundleFromDirectory(workflowDir, fileWorksheet);
        computeBundle = new ComputeBundleFromDirectory(fileParameters, fileWorkflow, fileWorksheet, dirProtocol);

        WorkflowGeneratorCommandLine generator = new WorkflowGeneratorCommandLine();

        // set where to write scripts
        generator.setToWriteLocally(true);
        String path = workingDir.getAbsolutePath();
        (new File(path + System.getProperty("file.separator") + applicationName))
                .mkdir();
        generator.setLocalLocation(path + System.getProperty("file.separator")
                + applicationName + System.getProperty("file.separator"));

        // here the loop over samples/lanes
        generator.setNewRun();

        generator.processSingleWorksheet(computeBundle, userValues,
                ""/* + iii*/, this.backend, templateDir);
        // add generated applications to the bundle
        computeBundle.setComputeJobs(generator.getComputeApplications());
//            iii++;
//        }

        if (backend.equalsIgnoreCase(WorkflowGeneratorCommandLine.CLUSTER))
            generator.flashSumbitScript();
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

        StartCommandLineGeneration generation = new StartCommandLineGeneration();

        checkSynonymParameters(generation, opt, "parameters file", PARAMETER_1, PARAMETER_2);
        checkSynonymParameters(generation, opt, "workflow file", WORKFLOW_1, WORKFLOW_2);
        checkSynonymParameters(generation, opt, "worksheet", WORKSHEET_1, WORKSHEET_2);
        checkSynonymParameters(generation, opt, "protocol directory", PROTOCOL_1, PROTOCOL_2);
        checkSynonymParameters(generation, opt, "output directory", OUTPUT_2, OUTPUT_1);
        checkSynonymParameters(generation, opt, "backend", BACKEND_1, BACKEND_2, BACKEND_3, BACKEND_4);
        checkSynonymParameters(generation, opt, "template directory", TEMP_1, TEMP_2);

        System.out.println("... command line parameters are parsed successfully");

        generation.setGenerationID(generationID);
        generation.setApplicationName(generationID);
        generation.setWorkingDir(new File("."));

        try
        {
            generation.run();
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
    private static void checkSynonymParameters(StartCommandLineGeneration generation, Options opt, String backend, String backend1, String backend2, String backend3, String backend4)
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
            generation.setBackEnd(WorkflowGeneratorCommandLine.CLUSTER);
        }
        else if (opt.getSet().isSet(backend2))
        {
                    generation.setBackEndDirectory(opt.getSet().getOption(backend2).getResultValue(0));
            generation.setBackEnd(WorkflowGeneratorCommandLine.GRID);

        }
        else if (opt.getSet().isSet(backend3))
        {
                    generation.setBackEndDirectory(opt.getSet().getOption(backend3).getResultValue(0));
            generation.setBackEnd(WorkflowGeneratorCommandLine.CLUSTER);
        }
        else if (opt.getSet().isSet(backend4))
        {
                    generation.setBackEndDirectory(opt.getSet().getOption(backend4).getResultValue(0));
            generation.setBackEnd(WorkflowGeneratorCommandLine.GRID);

        }
    }

    private void setBackEndDirectory(String result)
    {
        //backEndDir = result;
        this.userValues.put("outputdir", result);
    }

    private static void checkSynonymParameters(StartCommandLineGeneration generation, Options opt, String parameter, String str1, String str2)
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
        this.templateDir = templatesdir;
    }

    private void setOutputDir(String resultValue)
    {
        System.out.println("do we need output dir?");
        //this.userValues.put("outputdir", resultValue);
    }

    private void setBackEnd(String backend)
    {
        this.backend = backend;
    }

    private void setWorkingDir(File file)
    {
        this.workingDir = file;
    }

    public void setWorksheet(String str)
    {
        fileWorksheet = new File(str);
    }

    public void setGenerationID(String str)
    {
        this.userValues.put("runID", str);
    }

    public void setApplicationName(String applicationName)
    {
        this.applicationName = applicationName;
    }

}
