package org.molgenis.compute.scriptserver;

import org.molgenis.compute.pipelinemodel.Pipeline;

import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: Nov 18, 2010
 * Time: 9:21:22 AM
 * To change this template use File | Settings | File Templates.
 */

public class MCFServerSsh implements MCF
{

    private Vector<Pipeline> pipelines = new Vector<Pipeline>();
    private Vector<Pipeline> archivePipelines = new Vector<Pipeline>();

    public MCFServerSsh()
    {
        start();
    }


    public void setPipeline(Pipeline pipeline)
    {
        System.out.println(">>> start pipeline execution");

        pipelines.add(pipeline);

        //todo logging properly!!!
        PipelineThreadSsh pipelineThreadSsh = new PipelineThreadSsh(pipeline);
        new PipelineExecutor().execute(pipelineThreadSsh);
    }

    public Pipeline getPipeline(String id)
    {
        for (int i = 0; i < pipelines.size(); i++)
        {
            String cID = pipelines.elementAt(i).getId();
            if (cID.equalsIgnoreCase(id))
                return pipelines.elementAt(i);
        }

        return null;
    }

    public Pipeline getArchivePipeline(int i)
    {
        return archivePipelines.elementAt(i);
    }

    public int getNumberActivePipelines()
    {
        return pipelines.size();
    }

    public Pipeline getActivePipeline(int i)
    {
        return pipelines.elementAt(i);
    }

    public int getNumberFinishedPipelines()
    {
        return archivePipelines.size();
    }

    public void start()
    {

    }

    public void removePipeline(String id)
    {

    }

    public ExecutorService getExecutor()
    {
        return null;
    }

    public void removeFinishedPipelines()
    {
        Vector<Pipeline> toRemove = new Vector<Pipeline>();
        Iterator it = pipelines.iterator();
        while (it.hasNext())
        {
            Pipeline p = (Pipeline) it.next();
            if (p.isFinished())
            {
                archivePipelines.addElement(p);
                //pipelines.remove(p);
                toRemove.addElement(p);
            }
        }
        pipelines.removeAll(toRemove);
    }

    public String getBasis()
    {
        return MCF.SSH;
    }


}
