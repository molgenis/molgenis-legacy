package org.molgenis.compute.commandline;

import org.molgenis.compute.commandline.options.Options;
import org.molgenis.util.Tuple;

import java.io.File;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA. User: georgebyelas Date: 02/11/2011 Time: 14:36 To
 * change this template use File | Settings | File Templates.
 */
public class StartCommandLineGeneration
{
    private File workingDir = null;
    private File workflowDir = null;
    private File fileWorksheet = null;
    private String applicationName = null;
    private ComputeBundle computeBundle = null;
    private Hashtable<String, String> userValues = new Hashtable<String, String>();


    private void run() throws Exception
    {
        computeBundle = new ComputeBundleFromDirectory(workflowDir,
                fileWorksheet);
        WorkflowGeneratorCommandLine generator = new WorkflowGeneratorCommandLine();

        // set where to write scripts
        generator.setToWriteLocally(true);
        String path = workingDir.getAbsolutePath();
        (new File(path + System.getProperty("file.separator") + applicationName))
                .mkdir();
        generator.setLocalLocation(path + System.getProperty("file.separator")
                + applicationName + System.getProperty("file.separator"));
        //generator.setRemoteLocation("remote");

        // here the loop over samples/lanes
        generator.setNewRun();
        for (Tuple target : computeBundle.getUserParameters())
        {
            // ugly copy from tuple to hashtable
            for (String field : target.getFields())
            {
                if (!target.isNull(field))
                {
                    userValues.put(field, target.getString(field));
                }
            }

            generator.processSingleWorksheet(computeBundle, userValues,
                    applicationName);
            // add generated applications to the bundle
            computeBundle.setComputeJobs(generator.getComputeApplications());

            // every sample can be processed and monitored in the separated
            // pipeline
            // MCF mcf = new MCFServerSsh();
            // mcf.setClusterPipeline(generator.getPipeline());
        }
        generator.flashSumbitScript();

    }

    /*
      * arg0 - file with worksheet elements arg1 - name of application (run) e.g.
      * testrun
      */
    public static void main(String[] args)
    {

        Options opt = new Options(args, Options.Prefix.DASH, Options.Multiplicity.ONCE, 1);

        opt.getSet().addOption("inputlist", false, Options.Separator.EQUALS);
        opt.getSet().addOption("inputworkflow", false, Options.Separator.EQUALS);
        opt.getSet().addOption("outputscriptsdir", false, Options.Separator.EQUALS);

        boolean isCorrect = opt.check();

        System.out.println("Read input parameters: " + isCorrect);
        System.out.println("Check result:");
        System.out.println(opt.getCheckErrors());

        String generationID = opt.getSet().getData().get(0);

        if (!isCorrect)
        {
            System.out.println("command line format -inputlist=<InputWorksheet> " +
                    "-inputworkflow=<WorkflowDescriptionDir> " +
                    "-outputscriptsdir=<OutputDir> " +
                    "<GererationID>");
            System.exit(1);

        }

        StartCommandLineGeneration generation = new StartCommandLineGeneration();

        System.out.println("input worksheet: " + opt.getSet().getOption("inputlist").getResultValue(0));
        generation.setFileWorksheet(new File(opt.getSet().getOption("inputlist").getResultValue(0)));
        System.out.println("input workflow: " + opt.getSet().getOption("inputworkflow").getResultValue(0));
        System.out.println("output directory: "+ opt.getSet().getOption("outputscriptsdir").getResultValue(0));
        generation.setApplicationName(opt.getSet().getOption("outputscriptsdir").getResultValue(0));
        generation.setWorkflowDir(new File(opt.getSet().getOption("inputworkflow").getResultValue(0)));
        generation.setGenerationID(generationID);
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

    }

    private void setWorkingDir(File file)
    {
        this.workingDir = file;
    }

    public void setFileWorksheet(File fileWorksheet)
    {
        this.fileWorksheet = fileWorksheet;
    }

    public void setApplicationName(String applicationName)
    {
        this.applicationName = applicationName;
    }

    public void setWorkflowDir(File workflowDir)
    {
        this.workflowDir = workflowDir;
    }

    public void setGenerationID(String str)
    {
        this.userValues.put("runID", str);
    }

}
