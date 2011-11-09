package org.molgenis.compute.monitor;

import org.molgenis.compute.pipelinemodel.Script;
import org.molgenis.compute.ssh.SshData;
import org.molgenis.util.Ssh;
import org.molgenis.util.SshResult;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 08/11/2011
 * Time: 12:40
 * To change this template use File | Settings | File Templates.
 */
public class GridMonitor extends LoggingReaderSsh
{
    @Override
    protected void startSsh()
    {
        try
        {
            ssh = new Ssh(SshData.SERVER_GRID, SshData.USER_GRID, SshData.PASS_GRID);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public boolean isStepFinished()
    {
        String output, error;

        for (int i = 0; i < currentStep.getNumberOfScripts(); i++)
        {
            Script script = currentStep.getScript(i);
            System.out.println("script " + script.getID());

            SshResult result = null;
            try
            {
                System.out.println("command: " + script.getMonitoringCommand());
                result = ssh.executeCommand(script.getMonitoringCommand());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            output = result.getStdOut();
            error = result.getStdErr();

            System.out.println("Output:\n" + output);
            if (error == null || "".equalsIgnoreCase(error))
            {
                System.out.println("Error: none!");
            }
            else
            {
                System.out.println("Error: " + error);
            }

        }
        return false;
    }

}
