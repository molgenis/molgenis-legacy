package org.molgenis.compute.test.reader;

import app.DatabaseFactory;
import org.molgenis.compute.design.Workflow;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;

/**
 * Created with IntelliJ IDEA.
 * User: georgebyelas
 * Date: 22/08/2012
 * Time: 10:18
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowReaderDBJPA implements WorkflowReader
{
    public Workflow getWorkflow(String name)
    {
        Database db = null;
        try
        {
            db = DatabaseFactory.create();
            db.beginTx();

            //Workflow w = db.query(Workflow.class).find().get(0);
            Workflow w = db.find(Workflow.class, new QueryRule(Workflow.NAME, QueryRule.Operator.EQUALS, name)).get(0);

            db.close();
            return w;
        }
        catch (DatabaseException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
