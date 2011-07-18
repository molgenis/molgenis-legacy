package org.molgenis.compute.monitor;

import org.gridgain.grid.Grid;
import org.molgenis.compute.pipelinemodel.Pipeline;
import org.molgenis.compute.pipelinemodel.Script;
import org.molgenis.compute.pipelinemodel.Step;
import org.molgenis.compute.remoteexecutor.RemoteResult;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;



public class LoggingReader
{
    private Grid grid = null;
    private ExecutorService exec = null;

    public static final String _STARTED = "_started";
    public static final String _FINISHED = "_finished";

    private String log_location = null;

    private Step currentStep = null;
    //private boolean isStepFinished = false;

    private Summary summary = new Summary();
    private String pipelineName;

    private String logging = RemoteFileReader.FILE_IS_NOT_EXISTS;

    public void setGrid(Grid grid)
    {
        this.grid = grid;
        exec = grid.newGridExecutorService();
    }

    public void setPipeline(Pipeline pipeline)
    {
        pipelineName = pipeline.getId();
    }

    public class Summary
    {
        public int scripts_started;
        public int scripts_finished;
        public int scripts_all;
    }


    public void setStep(Step step)
    {
        currentStep = step;
        //isStepFinished = false;
        currentStep.setScriptsStarted(0);
        currentStep.setScriptsFinished(0);
    }

    public void setLogFile(String demoLog)
    {
        log_location = demoLog;
    }

    public boolean isNotFinishedStep()
    {
        //return isStepFinished;
        return currentStep.isFinished();
    }

    public boolean isStepFinished()
    {
        Future<RemoteResult> future = exec.submit(new RemoteFileReader(log_location));

        RemoteResult back = null;

        try
        {
            back = future.get();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }

        logging = new String(back.getData());

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
            //if does not work move to PipelineThread!!!
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

    //temporary for demo purposes
    public String getTestSummary()
    {
        if(currentStep != null)
        return "current Step: " + currentStep.getId() + "   subjobs started-" + summary.scripts_started +
                "    finished-" + summary.scripts_finished + "    from-" + summary.scripts_all;
        return "... starting";
    }

    public Summary getStepSummary()
    {
        return summary;
    }

    public String getLogFile()
    {
        return logging;
    }
}
