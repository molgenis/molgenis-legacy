package org.molgenis.compute.pipelinemodel;

import org.molgenis.compute.remoteexecutor.RemoteScriptSubmitter;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 07/11/2011
 * Time: 14:31
 * To change this template use File | Settings | File Templates.
 */
public class ClusterScript extends Script
{
    public ClusterScript(String scriptID, String outputRemoteLocation, byte[] bytes)
    {
        super(scriptID, outputRemoteLocation, bytes);
    }

    @Override
    public String getSubmitCommand()
    {
        String command;
        if (this.isShort())
            command = RemoteScriptSubmitter.SUB_SHORT + this.getRemoteDir() + this.getRemotename();
        else
            command = RemoteScriptSubmitter.SUB + this.getRemoteDir() + this.getRemotename();
        System.out.println(">>>" + command);
        return command;
    }

    @Override
    public String getMonitoringCommand()
    {
        return null;
    }
}
