package org.molgenis.compute.test.reader;

import app.DatabaseFactory;
import org.molgenis.compute.design.ComputeProtocol;
import org.molgenis.compute.design.Workflow;
import org.molgenis.compute.design.WorkflowElement;
import org.molgenis.compute.design.WorkflowElement_PreviousSteps;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: georgebyelas
 * Date: 24/08/2012
 * Time: 11:17
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowReaderDBJDBC implements WorkflowReader
{
    public Workflow getWorkflow(String name)
    {
        Database db = null;
        try
        {
            db = DatabaseFactory.create();
            db.beginTx();

            Workflow w = db.find(Workflow.class, new QueryRule(Workflow.NAME, QueryRule.Operator.EQUALS, name)).get(0);

            List<WorkflowElement> workflowElements = db.find(WorkflowElement.class,
                    new QueryRule(WorkflowElement.WORKFLOW_NAME, QueryRule.Operator.EQUALS, w.getName()));

            for(WorkflowElement we : workflowElements)
            {
                String protocol_name = we.getProtocol_Name();
                ComputeProtocol protocol = db.find(ComputeProtocol.class, new QueryRule(ComputeProtocol.NAME, QueryRule.Operator.EQUALS, protocol_name)).get(0);
                we.setProtocol(protocol);

                List<WorkflowElement_PreviousSteps> prev = db.find(WorkflowElement_PreviousSteps.class,
                        new QueryRule(WorkflowElement_PreviousSteps.WORKFLOWELEMENT, QueryRule.Operator.EQUALS, we.getName()));
            }

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
