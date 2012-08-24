package org.molgenis.compute.test.executor;

import app.DatabaseFactory;
import org.molgenis.compute.runtime.ComputeHost;
import org.molgenis.compute.runtime.ComputeTask;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: georgebyelas
 * Date: 22/08/2012
 * Time: 14:26
 * To change this template use File | Settings | File Templates.
 */
public class ComputeExecutorPilotDB implements ComputeExecutor
{
    private ExecutionHost host = null;

    //actual start pilots here
    public void executeTasks()
    {
        //evaluate if we have tasks ready to run
        Database db = null;
        List<ComputeTask> generatedTasks = null;
        int readyToSubmitSize = 0;

        try
        {
            db = DatabaseFactory.create();
            db.beginTx();

            generatedTasks = db.find(ComputeTask.class, new QueryRule(ComputeTask.STATUSCODE, QueryRule.Operator.EQUALS, "generated"));


            readyToSubmitSize = evaluateTasks(db, generatedTasks);

            db.commitTx();

        }
        catch (DatabaseException e)
        {
            e.printStackTrace();
        }

        //start as many pilots as we have tasks ready to run
        for(int i = 0; i < readyToSubmitSize; i++)
        {
            try
            {
                host.submitPilot();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private int evaluateTasks(Database db, List<ComputeTask> generatedTasks)
    {
        int count = 0;
        for(ComputeTask task : generatedTasks)
        {
            boolean isReady = true;
            List<ComputeTask> prevSteps = task.getPrevSteps();
            for(ComputeTask prev : prevSteps)
            {
                if(!prev.getStatusCode().equalsIgnoreCase("done"))
                    isReady = false;
            }

            if(isReady)
            {
                count++;
                task.setStatusCode("ready");
            }
        }
        return count;
    }

    public void startHost(String name)
    {
        //get host name from database
        Database db = null;
        ComputeHost dbHost = null;
        try
        {
            db = DatabaseFactory.create();
            db.beginTx();

            dbHost = db.find(ComputeHost.class, new QueryRule(ComputeHost.NAME, QueryRule.Operator.EQUALS, name)).get(0);
            db.close();
        }
        catch (DatabaseException e)
        {
            e.printStackTrace();
        }

        try
        {
            host = new ExecutionHost(dbHost.getHostName(), dbHost.getHostUsername(), dbHost.getHostPassword(), 22);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

}
