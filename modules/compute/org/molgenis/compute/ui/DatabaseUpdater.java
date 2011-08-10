package org.molgenis.compute.ui;


import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.molgenis.compute.ComputeApplication;
import org.molgenis.compute.monitor.LoggingReader;
import org.molgenis.compute.monitor.RemoteFileToStringReader;
import org.molgenis.compute.scriptserver.MCF;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;


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

    private ExecutorService executor = null;

    private RemoteFileToStringReader fileReader = new RemoteFileToStringReader();


    public void start()
    {

        isStarted = true;
        timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                //System.out.println(">>> updater report");

                //first "cache" all log files of active pipelines
                //this is more efficient than read it every log file to find application status later
                Vector<String> allLogs = readAllLogs();

                Vector<ComputeAppPaths> appToRemove = new Vector<ComputeAppPaths>();
                for (int i = 0; i < activeComputeApps.size(); i++)
                {
                    ComputeAppPaths appPaths = activeComputeApps.elementAt(i);
                    ComputeApplication application = appPaths.getApplication();

                    String name = application.getName();

                    //to avoid transaction if the status is not changed
                    ComputeAppPaths.AppStatus prevStatus = appPaths.getPrevStatus();
                    ComputeAppPaths.AppStatus status = findStatus(name, allLogs);

                    if(prevStatus.equals(status))
                        continue;

                    appPaths.setPrevStatus(status);

                    if (status.equals(ComputeAppPaths.AppStatus.started))
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
                    }
                    else if (status.equals(ComputeAppPaths.AppStatus.finished))
                    {
                        application.setStatusCode("finished");

                        String error = readRemoteFile(appPaths.getErrpath());
                        //application.setErrorFile(error);
                        String extralog = readRemoteFile(appPaths.getExtralog());
                        String output = readRemoteFile(appPaths.getOutpath());
                        application.setOutputFile(error);


                        if (appPaths.getLogpathSize() > 0)
                        {
                            for(int iii = 0; iii < appPaths.getLogpathSize(); iii++)
                            {
                                String log = readRemoteFile(appPaths.getLogpath(iii));
                            //application.setLogFile(log);
                            extralog += "\n-------------------------------------\n";
                            extralog += log;
                            }

                        }

                        application.setLogFile(extralog);

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
                        appToRemove.addElement(appPaths);
                    }
                }

                activeComputeApps.removeAll(appToRemove);
                if (activeComputeApps.size() == 0)
                    cancel();
            }
        }, delay * 1000, period * 1000);
    }

    private ComputeAppPaths.AppStatus findStatus(String name, Vector<String> allLogs)
    {

        for (int i = 0; i < allLogs.size(); i++)
        {
            String logfile = allLogs.elementAt(i);
            if (!logfile.equalsIgnoreCase(RemoteFileToStringReader.FILE_IS_NOT_EXISTS))
            {
                int index_started = logfile.indexOf(name + LoggingReader._STARTED);
                int index_finished = logfile.indexOf(name + LoggingReader._FINISHED);

                if (index_finished > -1)
                    return ComputeAppPaths.AppStatus.finished;
                else if (index_started > -1)
                    return ComputeAppPaths.AppStatus.started;
            }

        }
        return ComputeAppPaths.AppStatus.idle;
    }

    private Vector<String> readAllLogs()
    {
        Vector<String> allLogs = new Vector<String>();

        for (int i = 0; i < mcf.getNumberActivePipelines(); i++)
        {
            String path = mcf.getActivePipeline(i).getPipelinelogpath();
            String logfile = readRemoteFile(path);
            allLogs.addElement(logfile);
        }

        return allLogs;
    }

    private String readRemoteFile(String path)
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
        //System.out.println("result: " + result);
        return result;
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
            System.out.println("executor does not exist");
        }
    }
}
