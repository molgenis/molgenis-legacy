package org.molgenis.compute.monitor;


import org.molgenis.compute.scriptserver.MCF;
import org.molgenis.compute.ssh.SshData;
import org.molgenis.util.Ssh;

import java.io.IOException;


/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 21/06/2011
 * Time: 13:10
 * To change this template use File | Settings | File Templates.
 */

public class DatabaseUpdaterSsh extends DatabaseUpdaterCluster
{
    private Ssh ssh = null;

    public DatabaseUpdaterSsh(MCF mcf)
    {
        this.mcf = mcf;
        startSsh();
    }

    private void startSsh()
    {
        try
        {
            ssh = new Ssh(SshData.SERVER_MILLIPEDE, SshData.USER_MILLIPEDE, SshData.PASS_MILLIPEDE);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    protected String readRemoteFile(String path)
    {
        String result = null;
        try
        {
            result = ssh.downloadFileIntoString(path);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }
}
