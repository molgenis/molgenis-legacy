package compute.scriptserver;

import compute.pipelinemodel.Pipeline;
import compute.grid.GridStarter;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridNode;
import compute.resourcemanager.ResourceManager;

import java.util.Vector;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: Nov 18, 2010
 * Time: 9:21:22 AM
 * To change this template use File | Settings | File Templates.
 */

public class MCFServer implements MCF
{
    private Grid grid = null;
    private ResourceManager resourceManager = null;

    private Vector<Pipeline> pipelines = new Vector();

    public MCFServer()
    {
        start();
    }

    public MCFServer(Grid grid)
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
            } else
            {
                System.out.println(">>> wait for remote node");
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
        PipelineThread pipelineThread = new PipelineThread(pipeline, grid);
        new PipelineExecutor().execute(pipelineThread);
    }

    public Pipeline getPipeline(String id)
    {
        for(int i = 0; i < pipelines.size(); i++)
        {
            String cID = pipelines.elementAt(i).getId();
            if(cID.equalsIgnoreCase(id))
                return pipelines.elementAt(i);
        }

        return null;
    }

    public Pipeline getPipeline(int i)
    {
        return pipelines.elementAt(i);
    }

    public int getNumberActivePipelines()
    {
        return pipelines.size();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void start()
    {
        //start gridgain
        GridStarter gridStarter = new GridStarter();
        grid = gridStarter.startGrid();

        //start resource manager
        resourceManager = new ResourceManager();
        resourceManager.setGrid(grid);
        resourceManager.setSettings(8, 10);

    }

    public static void main(String[] args)
    {
        MCFServer mcf = new MCFServer();

        String result = mcf.test();
        System.out.println("ID: " + result);

    }

    public String test()
    {
        String ID = "LALA_ID";

        Pipeline p = new Pipeline();
        p.setId(ID);
        this.setPipeline(p);
        Pipeline back = this.getPipeline(ID);

        return back.getId();
    }

    public String getSimpleTestInfo()
    {
        String result = pipelines.elementAt(0).getMonitor().getTestSummary();

        return result;
    }

    public void removePipeline(String id)
    {
        
    }


}
