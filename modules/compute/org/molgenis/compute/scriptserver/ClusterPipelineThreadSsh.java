package org.molgenis.compute.scriptserver;

import org.molgenis.compute.monitor.ClusterLoggingReaderSsh;
import org.molgenis.compute.pipelinemodel.Pipeline;
import org.molgenis.compute.ssh.SshData;
import org.molgenis.util.Ssh;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 08/11/2011
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */
public class ClusterPipelineThreadSsh extends PipelineThreadSsh
{

    public ClusterPipelineThreadSsh(Pipeline pipeline)
    {


        super(pipeline);
    }

    @Override
    protected void startClusterSsh()
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

    protected void startMonitor()
    {
        monitor = new ClusterLoggingReaderSsh();
    }
}
