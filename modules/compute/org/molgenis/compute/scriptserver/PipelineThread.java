package org.molgenis.compute.scriptserver;

import org.gridgain.grid.Grid;
import org.molgenis.compute.monitor.LoggingReader;
import org.molgenis.compute.pipelinemodel.Pipeline;
import org.molgenis.compute.pipelinemodel.Script;
import org.molgenis.compute.pipelinemodel.Step;
import org.molgenis.compute.remoteexecutor.RemoteResult;
import org.molgenis.compute.remoteexecutor.RemoteScriptSubmitter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;


//thread class for a pipeline; every pipeline runs in the separated thread
public class PipelineThread implements Runnable
{
    private Pipeline pipeline = null;
    private LoggingReader monitor = new LoggingReader();
    private Grid grid = null;
    private ExecutorService exec = null;

    public PipelineThread(Pipeline pipeline, Grid grid, String logfile)
    {
        this.pipeline = pipeline;
        this.grid = grid;

        //to monitor pipeline execution from outside
        pipeline.setMonitor(monitor);

        monitor.setLogFile(logfile);
        monitor.setGrid(grid);
    }

    public PipelineThread(Pipeline pipeline, Grid grid)
    {
        this.pipeline = pipeline;
        this.grid = grid;

        //to monitor pipeline execution from outside
        pipeline.setMonitor(monitor);

        //set pipeline for demo purposes
        monitor.setPipeline(pipeline);

        monitor.setLogFile(pipeline.getPipelinelogpath());
        monitor.setGrid(grid);
    }

    public void run()
    {
        exec = grid.newGridExecutorService();

        int numberOfSteps = pipeline.getNumberOfSteps();

        System.out.println(">>> start pipeline " + pipeline.getId());

        for (int i = 0; i < numberOfSteps; i++)
        {
            Step step = pipeline.getStep(i);
            step.setActive(true);

            System.out.println(">>> start step " + pipeline.getId() + " - " + step.getId());

            for (int j = 0; j < step.getNumberOfScripts(); j++)
            {
               Script script = step.getScript(j);
               boolean error = submitScript(script);

                while (error)
                {
                    System.out.println("error occurs");
                    try
                    {
                        Thread.sleep(3000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    error = submitScript(script);
                }

            }

            //here monitor step execution
            monitor.setStep(step);

            while (!step.isFinished())
            {
                boolean isFinished = monitor.isStepFinished();

                if (isFinished)
                {
                    step.setFinished(true);
                    step.setActive(false);
                }

                try
                {
                    // set 5000 or more for cluster
                    Thread.sleep(5000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

        }
        exec.shutdown();
        pipeline.setFinished(true);
        System.out.println("... pipeline " + pipeline.getId() + "finished");

    }

    private boolean submitScript(Script script)
    {
        Future<RemoteResult> future = exec.submit(new RemoteScriptSubmitter(script));

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

        String output = back.getOutString();
        String error = back.getErrString();

        System.out.println("output: " + output);
        System.out.println("error: " + error);

        if(error.toCharArray().length > 0 || output.toCharArray().length == 0)
            return true;
        return false;
    }
}
