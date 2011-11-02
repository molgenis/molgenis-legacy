package org.molgenis.compute.commandline;

import org.molgenis.compute.ComputeBundle;
import org.molgenis.compute.ComputeBundleFromDirectory;
import org.molgenis.compute.WorksheetHelper;
import org.molgenis.compute.workflowgenerator.WorkflowGeneratorCommandLine;
import org.molgenis.util.Tuple;

import java.io.File;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 02/11/2011
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
public class StartCommandLineGeneration
{
    private String fileWorksheet = null;
    private String applicationName = null;
    private ComputeBundle computeBundle = null;

    private void run() throws Exception
    {
        List<Tuple> worksheet = new WorksheetHelper().readTuplesFromFile(new File(fileWorksheet));

        computeBundle = new ComputeBundleFromDirectory(new File("."));
        WorkflowGeneratorCommandLine generator = new WorkflowGeneratorCommandLine();

        //set where to write scripts
        generator.setToWriteLocally(true);
        String path = new java.io.File(".").getCanonicalPath();
        (new File(path + System.getProperty("file.separator") + applicationName)).mkdir();
        generator.setLocalLocation(path + System.getProperty("file.separator") + applicationName + System.getProperty("file.separator"));
        generator.setRemoteLocation("/data/gcc/test_george/scripts");

        //here the loop over samples/lanes
        //for()
        {
            Hashtable<String, String> userValues = new Hashtable<String, String>();
            generator.processSingleWorksheet(computeBundle, userValues, "findVariants", applicationName);

            //every sample can be processed and monitored in the separated pipeline
//            MCF mcf = new MCFServerSsh();
//            mcf.setPipeline(generator.getPipeline());
        }

    }

    /*
    * arg0 - file with worksheet elements
    * arg1 - name of application (run) e.g. testrun
    * */
    public static void main(String[] args)
    {
        if(args.length != 2)
        {
            System.out.println("\n" +
                    "    /*\n" +
                    "    * arg0 - file with worksheet elements\n" +
                    "    * arg1 - name of application (run) e.g. testrun\n" +
                    "    * */");
            System.exit(1);
        }
        else
        {
            StartCommandLineGeneration generation = new StartCommandLineGeneration();

            generation.setFileWorksheet(args[0]);
            generation.setApplicationName(args[1]);

            try
            {
                generation.run();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void setFileWorksheet(String fileWorksheet)
    {
        this.fileWorksheet = fileWorksheet;
    }

    public void setApplicationName(String applicationName)
    {
        this.applicationName = applicationName;
    }
}
