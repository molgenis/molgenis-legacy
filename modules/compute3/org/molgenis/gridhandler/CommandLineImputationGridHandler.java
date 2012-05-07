package org.molgenis.gridhandler;

import org.molgenis.compute.ComputeJob;
import org.molgenis.generator.JobGenerator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
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
    private List<Hashtable<String, String>> listImputationTargetsResults = new ArrayList<Hashtable<String, String>>();
    private Hashtable<String, String> jobsLogs = new Hashtable<String, String>();

    private BufferedWriter outList = null;
    private BufferedWriter outDownload = null;


    public int getNextJobID()
    {
        return 1;
    }

    public void setTargetComputeJob(String target, ComputeJob job)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setComputeJob(ComputeJob job, Hashtable<String, String> config)
    {
        String jobName = config.get(JobGenerator.BACK_END_DIR) + System.getProperty("file.separator") + job.getName() + ".job";
        jobsLogs.put(jobName, job.getLogFile());
    }

    public void writeCurrentTupleToFile()
    {

    }

    //write to file jobIDs and their log files
    public void writeJobsLogsToFile(Hashtable<String, String> config)
    {
        String filename = config.get(JobGenerator.OUTPUT_DIR)
                        + System.getProperty("file.separator")
                        + config.get(JobGenerator.GENERATION_ID) + ".list";

        String filename1 = config.get(JobGenerator.OUTPUT_DIR)
                        + System.getProperty("file.separator")
                        + config.get(JobGenerator.GENERATION_ID) + ".download.sh";;

        try
        {
            outList = new BufferedWriter(new FileWriter(filename));
            processJobsLogs();
            outList.close();

            outDownload = new BufferedWriter(new FileWriter(filename1));
            processDownload(config);
            outDownload.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private void processDownload(Hashtable<String, String> config) throws IOException
    {
        Enumeration keys = jobsLogs.keys();
        while (keys.hasMoreElements())
        {
            String key = (String) keys.nextElement();
            String log = jobsLogs.get(key);
            outDownload.write("lcg-cp " + log + " " + config.get(JobGenerator.BACK_END_DIR) + "/logs/" + giveJustName(log) + "\n");
            outDownload.flush();
        }
    }

    private void processJobsLogs() throws IOException
    {
        Enumeration keys = jobsLogs.keys();
        while (keys.hasMoreElements())
        {
            String key = (String) keys.nextElement();
            String log = jobsLogs.get(key);
            outList.write(key + "\t" + log + "\n");
            outList.flush();
        }

    }

    private String giveJustName(String actualName)
    {
        int posSlash = actualName.lastIndexOf("/");
        String justName = actualName.substring(posSlash + 1);
        return justName;
    }

}
