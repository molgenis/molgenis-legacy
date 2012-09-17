package org.molgenis.compute.monitor;

import org.molgenis.compute.ssh.SshData;
import org.molgenis.util.Ssh;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 08/11/2011
 * Time: 13:05
 * To change this template use File | Settings | File Templates.
 */
public class ClusterLoggingReaderSsh extends LoggingReaderSsh
{

    @Override
    protected void startSsh()
    {
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


    }
}
