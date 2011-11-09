package org.molgenis.compute.monitor;


import org.molgenis.compute.scriptserver.MCF;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 21/06/2011
 * Time: 13:10
 * To change this template use File | Settings | File Templates.
 */

public class DatabaseUpdaterGridGain extends DatabaseUpdaterCluster
{
    private ExecutorService executor = null;
    private RemoteFileToStringReader fileReader = new RemoteFileToStringReader();

    public DatabaseUpdaterGridGain(MCF mcf)
    {
        this.mcf = mcf;
        executor = mcf.getExecutor();

        if (executor == null)
        {
            System.out.println("executor does not exist");
        }
    }

    protected String readRemoteFile(String path)
    {
        fileReader.setFilename(path);
        Future<String> future = executor.submit(fileReader);

        String result = null;
        try
        {
            result = future.get();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        return result;
    }

}
