package org.molgenis.compute.monitor;


import org.molgenis.framework.db.Database;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 21/06/2011
 * Time: 13:10
 * To change this template use File | Settings | File Templates.
 */

public interface DatabaseUpdater
{
    public void start();

    boolean isStarted();

    void setSettings(int delay, int period);
    void setDatabase(Database db);

    void addComputeAppPath(ComputeAppPaths computeAppPath);

}
