package org.molgenis.compute.remoteexecutor;

import java.util.concurrent.Callable;
import java.io.*;

import org.molgenis.compute.pipelinemodel.FileToSaveRemotely;
import org.molgenis.compute.pipelinemodel.Script;
import org.molgenis.compute.sysexecutor.SysCommandExecutor;


public class RemoteScriptSubmitter implements Callable<RemoteResult>, Serializable
{
    private static final String SUB_SHORT = "qsub -q short ";
    private static final String SUB = "qsub ";

    RemoteResult returnData = new RemoteResult();

    private Script script = null;

    public RemoteScriptSubmitter(Script script)
    {
        this.script = script;
    }

    public RemoteResult call() throws Exception
    {
        System.out.println(">>> start execution");

        //save remotely
        saveData(script);

        //save additional files
        if(script.isHasAdditionalFiles())
        {
            for(int i = 0; i < script.getNumberFileToSaveRemotely(); i++)
            {
                saveData(script.getFileToSaveRemotely(i));
            }
        }

        //sumbit script
        SysCommandExecutor cmdExecutor = new SysCommandExecutor();

        String command;
        if(script.isShort())
            command = SUB_SHORT + script.getRemoteDir() + script.getRemotename();
        else
            command = SUB + script.getRemoteDir() + script.getRemotename();

        cmdExecutor.runCommand(command);
        System.out.println(command);

        System.out.println("...done");

        String cmdError = cmdExecutor.getCommandError();
        String cmdOutput = cmdExecutor.getCommandOutput();

        returnData.setErrString(cmdError);
        returnData.setOutString(cmdOutput);

        return returnData;
    }

    private static void saveData(Script script)
    {
        File newFile = new File(script.getRemoteDir() + script.getRemotename());

        if (!newFile.exists())
        {
            try
            {
                newFile.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            FileOutputStream fos = new FileOutputStream(newFile);
            fos.write(script.getScriptData());
            fos.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static void saveData(FileToSaveRemotely file)
    {
        File newFile = new File(file.getRemoteName());

        if (!newFile.exists())
        {
            try
            {
                newFile.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            FileOutputStream fos = new FileOutputStream(newFile);
            fos.write(file.getFileData());
            fos.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}