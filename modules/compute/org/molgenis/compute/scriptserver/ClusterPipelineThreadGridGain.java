package org.molgenis.compute.scriptserver;

import org.gridgain.grid.Grid;
import org.molgenis.compute.monitor.ClusterLoggingReaderGridGain;
import org.molgenis.compute.pipelinemodel.Pipeline;
import org.molgenis.compute.pipelinemodel.Script;
import org.molgenis.compute.remoteexecutor.RemoteResult;
import org.molgenis.compute.remoteexecutor.RemoteScriptSubmitter;

import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;


//thread class for a pipeline; every pipeline runs in the separated thread
public class ClusterPipelineThreadGridGain extends PipelineThread
{

    public ClusterPipelineThreadGridGain(Pipeline pipeline, Grid grid)
    {
        this.pipeline = pipeline;
        exec = grid.newGridExecutorService();

        monitor = new ClusterLoggingReaderGridGain();
        //to monitor pipeline execution from outside
        pipeline.setMonitor(monitor);

        //set pipeline for demo purposes
        monitor.setPipeline(pipeline);

        monitor.setLogFile(pipeline.getPipelinelogpath());
        ((ClusterLoggingReaderGridGain) monitor).setGrid(grid);
    }

    protected boolean submitScript(Script script)
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

        if (error.toCharArray().length > 0 || output.toCharArray().length == 0)
            return true;
        return false;
    }
}
