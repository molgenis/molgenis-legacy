package org.molgenis.gridhandler;

import org.molgenis.compute.ComputeJob;
import org.molgenis.generator.JobGenerator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 03/05/2012
 * Time: 15:05
 * To change this template use File | Settings | File Templates.
 */
public class CommandLineImputationGridHandler extends CommandLineGridHandler
{
    private List<ComputeJob> jobs = new ArrayList<ComputeJob>();

    private BufferedWriter outList = null;
    private BufferedWriter outDownload = null;


    public int getNextJobID()
    {
        return 1;
    }

    public void setComputeJob(ComputeJob job, Hashtable<String, String> config)
    {
        jobs.add(job);
    }

    //write to file jobIDs and their log files
    public void writeJobsLogsToFile(Hashtable<String, String> config)
    {
        String filenameList = config.get(JobGenerator.OUTPUT_DIR)
                        + System.getProperty("file.separator")
                        + config.get(JobGenerator.GENERATION_ID) + ".list";

        String filenameDownload = config.get(JobGenerator.OUTPUT_DIR)
                        + System.getProperty("file.separator")
                        + config.get(JobGenerator.GENERATION_ID) + ".download.sh";;

        try
        {
            outList = new BufferedWriter(new FileWriter(filenameList));
            outDownload = new BufferedWriter(new FileWriter(filenameDownload));

            processJobs(config);

            outList.close();
            outDownload.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private void processJobs(Hashtable<String, String> config) throws IOException
    {
        for(ComputeJob job: jobs)
        {
            String jobName = config.get(JobGenerator.BACK_END_DIR) + System.getProperty("file.separator") + job.getName() + ".job";
            String log = job.getLogFile();
            String output = job.getOutputFile();

            outList.write(jobName + "\t" + log + "\t" + output +"\n");
            outList.flush();

            outDownload.write("lcg-cp " + log + " " + config.get(JobGenerator.BACK_END_DIR) + "/logs/" + giveJustName(log) + "\n");
            outDownload.flush();
        }
    }

    private String giveJustName(String actualName)
    {
        int posSlash = actualName.lastIndexOf("/");
        String justName = actualName.substring(posSlash + 1);
        return justName;
    }

}
