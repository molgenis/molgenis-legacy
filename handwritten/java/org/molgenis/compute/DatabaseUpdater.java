package org.molgenis.compute;

import compute.monitor.LoggingReader;
import compute.monitor.RemoteFileReader;
import compute.remoteexecutor.RemoteResult;
import compute.scriptserver.MCF;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.protocol.ComputeApplication;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
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

public class DatabaseUpdater
{
    private int delay = -1;
    private int period = -1;  // repeat every sec.

    private Timer timer = new Timer();

    private Vector<ComputeAppPaths> activeComputeApps = new Vector<ComputeAppPaths>();
    private Database db = null;

    private boolean isStarted = false;
    private MCF mcf = null;

    private RemoteFileReader fileReader = new RemoteFileReader();
    private ExecutorService executor = null;

    public enum AppStatus
    {
        idle, started, finished;
    }

    public void start()
    {

        isStarted = true;
        timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                System.out.println(">>> updater report");

                //here it si more efficient to monitor only stArted applications
                //now we monitor all of them

                Vector<ComputeAppPaths> appToRemove = new Vector<ComputeAppPaths>();

                for (int i = 0; i < activeComputeApps.size(); i++)
                {
                    ComputeAppPaths appPaths = activeComputeApps.elementAt(i);
                    ComputeApplication application = appPaths.getApplication();

                    String name = application.getName();

                    AppStatus status = findStatus(name);

                    System.out.println("application: " + name + " --->  " + status.toString());


                    if (status.equals(AppStatus.started))
                    {
                        application.setStatusCode("started");
                        try
                        {
                            db.beginTx();
                            db.update(application);
                            db.commitTx();
                        }
                        catch (DatabaseException e)
                        {
                            e.printStackTrace();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else if (status.equals(AppStatus.finished))
                    {
                        application.setStatusCode("finished");

                        String error = readRemoteFile(appPaths.getErrpath());
                        application.setErrorFile(error);
                        String output = readRemoteFile(appPaths.getOutpath());
                        application.setOutputFile(output);
                        if (appPaths.getLogpath() != null)
                        {
                            String log = readRemoteFile(appPaths.getLogpath());
                            application.setLogFile(log);
                        }
                        try
                        {
                            db.beginTx();
                            db.update(application);
                            db.commitTx();
                        }
                        catch (DatabaseException e)
                        {
                            e.printStackTrace();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        appToRemove.addElement(appPaths);
                    }
                }

                activeComputeApps.removeAll(appToRemove);
                if (activeComputeApps.size() == 0)
                    cancel();
            }
        }, delay * 1000, period * 1000);
    }

    private AppStatus findStatus(String name)
    {
        String logfile = RemoteFileReader.FILE_IS_NOT_EXISTS;

        for (int i = 0; i < mcf.getNumberActivePipelines(); i++)
        {
            String path = mcf.getPipeline(i).getPipelinelogpath();
            logfile = readRemoteFile(path);
            if (!logfile.equalsIgnoreCase(RemoteFileReader.FILE_IS_NOT_EXISTS))
            {
                int index_started = logfile.indexOf(name + LoggingReader._STARTED);
                int index_finished = logfile.indexOf(name + LoggingReader._FINISHED);

                if (index_finished > -1)
                    return AppStatus.finished;
                else if (index_started > -1)
                    return AppStatus.started;
            }

        }
        return AppStatus.idle;
    }

    private String readRemoteFile(String path)
    {

        Future<RemoteResult> future = executor.submit(new RemoteFileReader(path));

        RemoteResult back = null;
        try
        {
            back = future.get();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }


        return new String(back.getData());
    }

    public void stop()
    {
        timer.cancel();
        isStarted = false;
    }

    //set settings in seconds
    public void setSettings(int delay, int period)
    {
        this.delay = delay;
        this.period = period;
    }

    public void addComputeAppPath(ComputeAppPaths computeAppPath)
    {
        activeComputeApps.addElement(computeAppPath);
    }

    public boolean isStarted()
    {
        return isStarted;
    }

    public void setDatabase(Database db)
    {
        this.db = db;
    }

    public void setMCF(MCF mcf)
    {
        this.mcf = mcf;
        executor = mcf.getExecutor();

        if (executor == null)
        {
            System.out.println("pizdec");
        }
    }
}
