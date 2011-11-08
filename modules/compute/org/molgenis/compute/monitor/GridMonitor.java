package org.molgenis.compute.monitor;

import org.molgenis.compute.ssh.SshData;
import org.molgenis.util.Ssh;

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
        System.out.println(">>> have no idea!");
        return false;
    }

}
