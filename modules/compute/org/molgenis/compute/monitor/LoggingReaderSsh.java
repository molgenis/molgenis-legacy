package org.molgenis.compute.monitor;

import org.molgenis.compute.pipelinemodel.Script;
import org.molgenis.compute.ssh.SshData;
import org.molgenis.util.Ssh;

import java.io.IOException;


public abstract class LoggingReaderSsh extends LoggingReader
{
    protected Ssh ssh = null;

    public LoggingReaderSsh()
    {
        startSsh();
    }

    protected abstract void startSsh();

    public boolean isStepFinished()
    {

        try
        {
            logging = ssh.downloadFileIntoString(log_location);
        }
        catch (IOException e)
        {
            //e.printStackTrace();
        }

        //log file is created by the first script of the pipeline;
        if(logging.equalsIgnoreCase(RemoteFileReader.FILE_IS_NOT_EXISTS))
        {
            System.out.println(">> LOG file is not created yet");
            return false;
        }

        summary.scripts_started = 0;
        summary.scripts_finished = 0;
        summary.scripts_all = currentStep.getNumberOfScripts();


        for (int i = 0; i < summary.scripts_all; i++)
        {
            Script script = currentStep.getScript(i);
            String script_id = script.getID();

            int index_started = logging.indexOf(script_id + _STARTED);
            int index_finished = logging.indexOf(script_id + _FINISHED);

            //todo in principle it is not the correct place to set the script as being started
            //if does not work move to ClusterPipelineThreadGridGain!!!
            if (index_started > -1)
            {
                script.setStarted(true);
                summary.scripts_started++;
            }
            if (index_finished > -1)
            {
                //script.setStarted(false);
                script.setFinished(true);
                summary.scripts_finished++;
            }
        }

        currentStep.setScriptsStarted(summary.scripts_started);
        currentStep.setScriptsFinished(summary.scripts_finished);

        System.out.println(pipelineName +"  scripts started: " + summary.scripts_started + "\t|  finished: "
                + summary.scripts_finished + "\t| out of " + summary.scripts_all);

        if (summary.scripts_finished == summary.scripts_all)
        {
            return true;
        }
        

        return false;
    }

    public String getLogFile()
    {
        return logging;
    }
}
