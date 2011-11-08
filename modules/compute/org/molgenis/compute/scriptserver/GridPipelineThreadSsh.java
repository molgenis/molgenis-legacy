package org.molgenis.compute.scriptserver;

import org.molgenis.compute.monitor.GridMonitor;
import org.molgenis.compute.pipelinemodel.Pipeline;
import org.molgenis.compute.ssh.SshData;
import org.molgenis.util.Ssh;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 08/11/2011
 * Time: 12:49
 * To change this template use File | Settings | File Templates.
 */
public class GridPipelineThreadSsh extends PipelineThreadSsh
{
    public GridPipelineThreadSsh(Pipeline pipeline)
    {
        super(pipeline);

    }

    @Override
    protected void startMonitor()
    {
        monitor = new GridMonitor();
    }

    @Override
    protected void startClusterSsh()
    {
        try
        {
            ssh = new Ssh(SshData.SERVER_GRID, SshData.USER_GRID, SshData.PASS_GRID);
            System.out.println("... ssh to grid created");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("... ssh to grid failed");
        }
    }

}
