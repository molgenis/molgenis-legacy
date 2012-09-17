package org.molgenis.compute.test.util;

import app.DatabaseFactory;
import org.molgenis.compute.runtime.ComputeTask;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: georgebyelas
 * Date: 23/08/2012
 * Time: 12:04
 * To change this template use File | Settings | File Templates.
 */
public class PrepareTest
{
    public static void main(String[] args)
    {
        Database db = null;
        List<ComputeTask> tasks = null;

        try
        {
            db = DatabaseFactory.create();
            db.beginTx();

            tasks = db.query(ComputeTask.class).find();

            for(ComputeTask task : tasks)
            {
                task.setStatusCode("generated");
            }

            db.commitTx();

        }
        catch (DatabaseException e)
        {
            e.printStackTrace();
        }

    }
}
