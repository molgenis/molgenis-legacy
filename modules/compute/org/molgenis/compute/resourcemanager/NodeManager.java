package org.molgenis.compute.resourcemanager;


import org.gridgain.grid.Grid;
import org.gridgain.grid.GridNode;
import org.molgenis.compute.grid.GridStarter;

import java.util.Timer;
import java.util.TimerTask;


public class NodeManager
{
    private int delay = -1;
    private int period = -1;  // repeat every sec.

    private Timer timer = new Timer();

    public static final String WORKER = "worker";
    private GridStarter starter = new GridStarter();
    private Grid grid = null;

    public void start()
    {
        timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                boolean workerExist = false;
                for (GridNode node : grid.getRemoteNodes())
                {
                    String name = node.getAttribute("org.gridgain.grid.name");
                    //System.out.println("node " + name);
                    if (name != null)
                        if (name.equals(WORKER))
                        {
                            workerExist = true;
                            break;
                        }
                }
                if (!workerExist)
                {
                    System.out.println("! node manager - starting worker");
                    starter.startRemoteNode();
                }

            }
        }, delay * 1000 * 60, period * 1000 * 60);
    }

    //set settings in days
    public void setSettings(int delay, int period)
    {
        this.delay = delay;
        this.period = period;
    }

    public void setGrid(Grid grid)
    {
        this.grid = grid;
    }

}
