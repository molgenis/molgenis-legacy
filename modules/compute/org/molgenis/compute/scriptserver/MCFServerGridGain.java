package org.molgenis.compute.scriptserver;

import org.gridgain.grid.Grid;
import org.gridgain.grid.GridNode;
import org.molgenis.compute.grid.GridStarter;
import org.molgenis.compute.pipelinemodel.Pipeline;
import org.molgenis.compute.resourcemanager.NodeManager;
import org.molgenis.compute.resourcemanager.ResourceManager;


import java.util.Iterator;
import java.util.Vector;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: Nov 18, 2010
 * Time: 9:21:22 AM
 * To change this template use File | Settings | File Templates.
 */

public class MCFServerGridGain implements MCF
{
    private Grid grid = null;
    private ResourceManager resourceManager = null;

    private Vector<Pipeline> pipelines = new Vector<Pipeline>();
    private Vector<Pipeline> archivePipelines = new Vector<Pipeline>();

    public MCFServerGridGain()
    {
        start();
    }

    public MCFServerGridGain(Grid grid)
    {
        this.grid = grid;
    }

    public void setPipeline(Pipeline pipeline)
    {
        //maybe temporary
        boolean remoteNodeExists = false;

        while (!remoteNodeExists)
        {
            Collection<GridNode> remoteNodes = grid.getRemoteNodes();

            if (remoteNodes.size() > 0)
            {
                remoteNodeExists = true;
            }
            else
            {
                System.out.println(">>> waiting for remote node");
                try
                {
                    Thread.sleep(10000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(">>> start pipeline execution");


        pipelines.add(pipeline);

        //todo logging properly!!!
        PipelineThreadGridGain pipelineThreadGridGain = new PipelineThreadGridGain(pipeline, grid);
        new PipelineExecutor().execute(pipelineThreadGridGain);
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

    private void start()
    {
        //start gridgain
        GridStarter gridStarter = new GridStarter();
        grid = gridStarter.startGrid();

        //start resource manager
        resourceManager = new ResourceManager();
        resourceManager.setGrid(grid);
        resourceManager.setSettings(8, 10);

        NodeManager nodeManager = new NodeManager();
        nodeManager.setGrid(grid);
        //first parameter is delay, second period in minutes
        nodeManager.setSettings(15, 1);
        nodeManager.start();

        gridStarter.startRemoteNode();
    }

    public void removePipeline(String id)
    {

    }

    public ExecutorService getExecutor()
    {
        return grid.newGridExecutorService();
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
        return MCF.GRID;
    }


}
