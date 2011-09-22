package org.molgenis.compute.ui;


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

public class DatabaseUpdaterSsh extends DatabaseUpdater
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
            ssh = new Ssh(SshData.SERVER, SshData.USER, SshData.PASS);
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
            ssh = new Ssh(SshData.SERVER, SshData.USER, SshData.PASS);
            result = ssh.downloadFileIntoString(path);
            ssh.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

}
