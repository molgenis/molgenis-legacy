package compute.remoteexecutor;

import java.util.concurrent.Callable;
import java.io.*;

import compute.sysexecutor.SysCommandExecutor;
import compute.pipelinemodel.Script;
import compute.pipelinemodel.FileToSaveRemotely;

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