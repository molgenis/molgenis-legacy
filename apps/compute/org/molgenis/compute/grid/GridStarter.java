package org.molgenis.compute.grid;

import org.gridgain.grid.*;
import org.gridgain.grid.spi.topology.basic.GridBasicTopologySpi;
import org.gridgain.grid.spi.discovery.jgroups.GridJgroupsDiscoverySpi;
import org.gridgain.grid.spi.communication.jgroups.GridJgroupsCommunicationSpi;

import java.util.Collection;

//standard grid started
public class GridStarter
{

    public Grid startGrid()
    {
        Grid grid = null;

        GridConfigurationAdapter cfg = new GridConfigurationAdapter();

        GridBasicTopologySpi topSpi = new GridBasicTopologySpi();

        // Exclude local node from topology.
        topSpi.setLocalNode(false);


        // Configure your own topology SPI.
        cfg.setTopologySpi(topSpi);

        //discovery spi
        GridJgroupsDiscoverySpi spi = new GridJgroupsDiscoverySpi();

        // Override default JGroups configuration file.
        spi.setConfigurationFile("/config/jgroups/tcp/jgroups.xml");
        //spi.setConfigurationFile("/home/gbyelas/gridgain/config/jgroups/tcp/jgroups.xml");

        // Override default discovery SPI.
        cfg.setDiscoverySpi(spi);

        //transport spi
        GridJgroupsCommunicationSpi commSpi = new GridJgroupsCommunicationSpi();

        // Override default JGroups configuration file.
        commSpi.setConfigurationFile("/config/jgroups/tcp/jgroups.xml");
        //commSpi.setConfigurationFile("/home/gbyelas/gridgain/config/jgroups/tcp/jgroups.xml");
        cfg.setCommunicationSpi(commSpi);

        try
        {
            GridFactory.start(cfg);
            grid = GridFactory.getGrid();
        }
        catch (GridException e)
        {
            e.printStackTrace();
        }

        System.out.println("... grid started");

        return grid;
    }

    public void waitForRemoteNode(Grid grid)
    {
        boolean remoteNodeExists = false;

        while (!remoteNodeExists)
        {
            Collection<GridNode> remoteNodes = grid.getRemoteNodes();

            if (remoteNodes.size() > 0)
            {
                remoteNodeExists = true;
            } else
            {
                System.out.println("... wait for remote node");
                try
                {
                    Thread.sleep(5000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(">>> remote node is available");
    }

}
