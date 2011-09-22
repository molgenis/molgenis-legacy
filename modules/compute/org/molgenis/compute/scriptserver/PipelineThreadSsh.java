package org.molgenis.compute.scriptserver;

import org.molgenis.compute.monitor.LoggingReaderSsh;
import org.molgenis.compute.pipelinemodel.Pipeline;
import org.molgenis.compute.pipelinemodel.Script;
import org.molgenis.compute.remoteexecutor.RemoteScriptSubmitter;
import org.molgenis.compute.ssh.SshData;
import org.molgenis.util.Ssh;
import org.molgenis.util.SshResult;

import java.io.IOException;


//thread class for a pipeline; every pipeline runs in the separated thread
public class PipelineThreadSsh extends PipelineThread
{
    private Ssh ssh = null;

    public PipelineThreadSsh(Pipeline pipeline)
    {
        this.pipeline = pipeline;

        monitor = new LoggingReaderSsh();

        //to monitor pipeline execution from outside
        pipeline.setMonitor(monitor);

        //set pipeline for demo purposes
        monitor.setPipeline(pipeline);

        monitor.setLogFile(pipeline.getPipelinelogpath());
        startSsh();
    }

    private void startSsh()
    {
        try
        {
            ssh = new Ssh(SshData.SERVER, SshData.USER, SshData.PASS);
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    @Override
    protected boolean submitScript(Script script)
    {
        String output = null, error = null;
        String remoteName = script.getRemoteDir() + script.getRemotename();

        try
        {
            ssh.uploadStringToFile(new String(script.getScriptData()), script.getRemotename(), script.getRemoteDir());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        String command;
        if (script.isShort())
            command = RemoteScriptSubmitter.SUB_SHORT + remoteName;
        else
            command = RemoteScriptSubmitter.SUB + remoteName;

        SshResult result = null;
        try
        {
            result = ssh.executeCommand(command);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        output = result.getStdOut();
        error = result.getStdErr();

        System.out.println("output: " + output);
        System.out.println("error: " + error);

        if (error.toCharArray().length > 0 || output.toCharArray().length == 0)
            return true;
        return false;
    }
}
