package org.molgenis.compute.monitor;

import org.molgenis.compute.scriptserver.MCF;
import org.molgenis.framework.db.Database;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 09/11/2011
 * Time: 11:02
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseUpdaterGrid implements DatabaseUpdater
{
    public DatabaseUpdaterGrid(MCF mcf)
    {

    }

    public void start()
    {
        System.out.println("from DB updater");
    }

    public boolean isStarted()
    {
        return false;
    }

    public void setSettings(int delay, int period)
    {
    }

    public void setDatabase(Database db)
    {
    }

    public void addComputeAppPath(ComputeAppPaths computeAppPath)
    {

    }
}
