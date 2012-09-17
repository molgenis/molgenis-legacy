package org.molgenis.compute.remoteexecutor;

import java.util.concurrent.Callable;
import java.io.*;

import org.molgenis.compute.pipelinemodel.FileToSaveRemotely;
import org.molgenis.compute.pipelinemodel.Script;
import org.molgenis.compute.sysexecutor.SysCommandExecutor;


public class RemoteGridGainStarter implements Callable<String>, Serializable
{
    private static final String SUB = "qsub -q nodeslong /data/byelas/scripts/gridgain/resident_worker.sh";

    public String call() throws Exception
    {
        System.out.println(">>> start another gridgain");

        SysCommandExecutor cmdExecutor = new SysCommandExecutor();
        cmdExecutor.runCommand(SUB);
        String cmdOutput = cmdExecutor.getCommandOutput();

        return cmdOutput;
    }
}