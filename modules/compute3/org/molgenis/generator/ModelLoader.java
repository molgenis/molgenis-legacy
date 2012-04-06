package org.molgenis.generator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.molgenis.compute.ComputeParameter;
import org.molgenis.compute.ComputeProtocol;
import org.molgenis.compute.commandline.WorksheetHelper;
import org.molgenis.protocol.Workflow;
import org.molgenis.protocol.WorkflowElement;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
            System.out.print("protocol " + strComputeProtocol);

            //loading protocol
            String strProtocol = dirProtocol.getAbsolutePath() + System.getProperty("file.separator") + strComputeProtocol + ".ftl";
            File fileProtocol = new File(strProtocol);

            isExist(fileProtocol);

            String protocol = readFileAsString(fileProtocol);
            //create ComputeProtocol parsing file
            ComputeProtocol computeProtocol = parseComputeProtocolFromString(protocol);
            workflowElement.setProtocol(computeProtocol);
        }

        return workflow;
    }

    private ComputeProtocol parseComputeProtocolFromString(String protocol)
    {
        System.out.println("  ... parsed");
        return null;
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

    private String readFileAsString(File file) throws java.io.IOException
	{
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1)
		{
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

    private void isExist(File file)
    {
        if (!file.exists())
        {
            logger.log(Level.ERROR, "protocol " + file.getName() + " does not exist");
            System.exit(1);
        }
    }
}
