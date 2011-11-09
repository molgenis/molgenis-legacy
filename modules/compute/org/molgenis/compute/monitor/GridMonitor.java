package org.molgenis.compute.monitor;

import org.molgenis.compute.pipelinemodel.GridScript;
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

    public boolean isStepFinished() throws IOException
    {
        String output, error;
        boolean isStepFinished = true;
        for (int i = 0; i < currentStep.getNumberOfScripts(); i++)
        {
            Script script = currentStep.getScript(i);
            if(!script.isFinished())
            {
                System.out.println("script " + script.getID());

                    System.out.println("command: " + script.getMonitoringCommand());
                    SshResult result = ssh.executeCommand(script.getMonitoringCommand());

                output = result.getStdOut();
                error = result.getStdErr();

                String status = checkRunStatus(output, error, script);

                System.out.println(">>> " + status);
                if(!script.isFinished())
                {
                    isStepFinished = false;
                    if(script.getStatus() == Script.Status.DONE_FAILED /*||
                            script.isExpired()*/)
                    {
                        resubmit(script);
                    }
                }
                else
                {
                    String getLogsCommand = ((GridScript) script).getLogsCommand();

                    result = ssh.executeCommand(getLogsCommand);

                    System.out.println("output:\n" + result.getStdOut());
                    System.out.println("error: ");
                }
            }
            else
                continue;//skip checking - script is finished already

        }
        return isStepFinished;
    }

    private void resubmit(Script script) throws IOException
    {
        //
        System.out.println(">>> get logs if exists");
        String getLogsCommand = ((GridScript) script).getLogsCommand();
        SshResult result = ssh.executeCommand(getLogsCommand);
        System.out.println("get logs out:\n" + result.getStdOut());
        System.out.println("get logs error: " + result.getStdErr());

        //
        System.out.println(">>> cancel execution");
        String cancelCommand = ((GridScript)script).getCancelCommand();
        result = ssh.executeCommand(cancelCommand);
        System.out.println("cancel out:\n" + result.getStdOut());
        System.out.println("cancel error: " + result.getStdErr());

        //
        System.out.println(">>> submit again");
        result = ssh.executeCommand(script.getSubmitCommand());
        System.out.println("resubmit out:\n" + result.getStdOut());
        System.out.println("resubmit error: " + result.getStdErr());
    }

    //compare with all produced statuses
    private String checkRunStatus(String output, String error, Script script)
    {
        //System.out.println("Output:\n" + output);

        if (error == null || "".equalsIgnoreCase(error))
        {
            //System.out.println("Error: none!");
            if(output.contains(GridScript.STATUS_RUN))
            {
                if(output.contains(GridScript.STATUS_RUN_WAITING))
                {
                    script.setStatus(Script.Status.WAITING);
                }
                else if(output.contains(GridScript.STATUS_RUN_SCHEDULED))
                {
                    script.setStatus(Script.Status.SCHEDULED);
                }
                else if(output.contains(GridScript.STATUS_RUN_RUNNING))
                {
                    script.setStatus(Script.Status.RUNNING);
                    script.setStarted(true);
                }
                else if(output.contains(GridScript.STATUS_RUN_DONE_SUCCESS))
                {
                    script.setStatus(Script.Status.DONE_SUCCESS);
                    script.setFinished(true);
                }
                else if(output.contains(GridScript.STATUS_RUN_DONE_FAILED))
                {
                    script.setStatus(Script.Status.DONE_FAILED);
                }
                return script.getStatus().toString();
            }
            return "error in returned status";
        }
        else
        {
            //System.out.println("Error: " + error);
            return "error while checking status";
        }

    }

}
