package org.molgenis.generator;

import org.apache.log4j.Logger;
import org.molgenis.compute.ComputeParameter;
import org.molgenis.compute.commandline.WorksheetHelper;
import org.molgenis.protocol.Workflow;
import org.molgenis.protocol.WorkflowElement;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 05/04/2012
 * Time: 10:18
 * To change this template use File | Settings | File Templates.
 */
public class ModelLoader
{
    private static Logger logger = Logger.getLogger(ModelLoader.class);

    public Workflow loadWorkflowFromFiles(File fileWorkflow, File dirProtocol, File fileParameters) throws Exception
    {
        Workflow workflow = new Workflow();
        String name = fileWorkflow.getName();
        workflow.setName(name);

        //read workflow elements
        List<WorkflowElement> workflowElements = readEntitiesFromFile(fileWorkflow, WorkflowElement.class);
        workflow.setWorkflowWorkflowElementCollection(workflowElements);

        //read workflow parameters
        List<ComputeParameter> workflowParameters = readEntitiesFromFile(fileParameters, ComputeParameter.class);
        workflow.setWorkflowComputeParameterCollection(workflowParameters);

        //set protocols and workflow_name to elements
        Iterator<WorkflowElement> iterator = workflow.getWorkflowWorkflowElementCollection().iterator();
        while(iterator.hasNext())
        {
            WorkflowElement workflowElement = iterator.next();
            workflowElement.setWorkflow_Name(workflow.getName());

            String strComputeProtocol = workflowElement.getProtocol_Name();
            System.out.println("protocol " + strComputeProtocol);

        }

        return workflow;
    }

    public List<Tuple> loadWorksheetFromFile(File fileWorksheet)
    {
        List<Tuple> worksheet = null;
        try
        {
            worksheet = new WorksheetHelper().readTuplesFromFile(fileWorksheet);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return worksheet;
    }


    private <E extends Entity> List<E> readEntitiesFromFile(File file,
                                                            final Class<E> klazz) throws Exception
    {
        final List<E> result = new ArrayList<E>();

        // check if file exists
        if (!file.exists())
        {
            logger.warn("file '" + file.getName() + "' is missing");
            return result;
        }

        // read the file
        CsvReader reader = new CsvFileReader(file);
        for (Tuple tuple : reader)
        {
            E entity = klazz.newInstance();
            entity.set(tuple);
            result.add(entity);

        }

        return result;
    }
}
