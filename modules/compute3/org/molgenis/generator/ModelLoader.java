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
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 05/04/2012
 * Time: 10:18
 * To change this template use File | Settings | File Templates.
 */
public class ModelLoader
{
    public static final String FLAG_MOLGENIS = "#MOLGENIS";

    public static final String FLAG_INPUTS = "#INPUTS";
    public static final String FLAG_OUTPUTS = "#OUTPUTS";
    public static final String FLAG_EXES = "#EXES";
    public static final String FLAG_LOG = "#LOGS";
    public static final String FLAG_TARGETS = "#TARGETS";

    public static final String FLAG_CLUSTER_QUEUE = "clusterQueue";
    public static final String FLAG_CORES = "cores";
    public static final String FLAG_NODES = "nodes";
    public static final String FLAG_WALLTIME = "walltime";
    public static final String FLAG_MEMORY = "mem";

    public static final String FLAG_INTERPRETER = "interpreter";
    private static final String INTERPRETER_BASH = "bash";
	private static final String INTERPRETER_R = "R";


    private static Logger logger = Logger.getLogger(ModelLoader.class);

    private Workflow workflow = null;

    public Workflow loadWorkflowFromFiles(File fileWorkflow, File dirProtocol, File fileParameters) throws Exception
    {
        workflow = new Workflow();
        String name = fileWorkflow.getName();
        //removing file extension
        name = name.substring(0, name.lastIndexOf("."));
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

            String protocol = readFileAsString( fileProtocol);
            //create ComputeProtocol parsing file
            ComputeProtocol computeProtocol = parseComputeProtocolFromString(strComputeProtocol, protocol);
            workflowElement.setProtocol(computeProtocol);

            //set predecessors for workflow elements
            workflowElement.setPreviousSteps(createListPreviousSteps(workflowElement, workflowElements));
        }

        return workflow;
    }

    private List<WorkflowElement> createListPreviousSteps(WorkflowElement workflowElement, List<WorkflowElement> workflowElements)
    {
        List<WorkflowElement> previous = new ArrayList<WorkflowElement>();
        List<String> names = workflowElement.getPreviousSteps_Name();

        for(int i = 0; i < names.size(); i++)
        {
            WorkflowElement el = findWorkflowElement(names.get(i), workflowElements);
            previous.add(el);
        }

        return previous;
    }

    private WorkflowElement findWorkflowElement(String s, List<WorkflowElement> workflowElements)
    {
        Iterator<WorkflowElement> itr = workflowElements.iterator();
        while(itr.hasNext())
        {
            WorkflowElement el = itr.next();
            String name = el.getName();

            if(s.equalsIgnoreCase(name))
                return el;
        }

        logger.log(Level.ERROR, "workflow element " + s + " does not exist");
        System.exit(1);
        return null;
    }

    private ComputeProtocol parseComputeProtocolFromString(String protocolName, String protocolListing)
    {

        ComputeProtocol protocol = new ComputeProtocol();

        protocol.setName(protocolName);
        protocol.setScriptTemplate(protocolListing);

        String strMolgenisHeader = protocolListing.substring(protocolListing.indexOf(FLAG_MOLGENIS),
                protocolListing.indexOf("\n", protocolListing.indexOf(FLAG_MOLGENIS)));

        String str = null;

        //set walltime
        if(strMolgenisHeader.indexOf(FLAG_WALLTIME) > -1)
        {
            str = getValueFromMolgenisHeader(strMolgenisHeader, FLAG_WALLTIME);
            protocol.setWalltime(str);
        }

        //set # nodes
        if(strMolgenisHeader.indexOf(FLAG_NODES) > -1)
        {
            str = getValueFromMolgenisHeader(strMolgenisHeader, FLAG_NODES);
            protocol.setNodes(Integer.parseInt(str));
        }

        //set # cores
        if(strMolgenisHeader.indexOf(FLAG_CORES) > -1)
        {
            str = getValueFromMolgenisHeader(strMolgenisHeader, FLAG_CORES);
            protocol.setCores(Integer.parseInt(str));
        }

        //set interpreter
        if(strMolgenisHeader.indexOf(FLAG_INTERPRETER) > -1)
        {
            str = getValueFromMolgenisHeader(strMolgenisHeader, FLAG_INTERPRETER);
            protocol.setInterpreter(str);
        }

        //set cluster queue
        if(strMolgenisHeader.indexOf(FLAG_CLUSTER_QUEUE) > -1)
        {
            str = getValueFromMolgenisHeader(strMolgenisHeader, FLAG_CLUSTER_QUEUE);
            protocol.setClusterQueue(str);
        }

        //set memory
        if(strMolgenisHeader.indexOf(FLAG_MEMORY) > -1)
        {
            str = getValueFromMolgenisHeader(strMolgenisHeader, FLAG_MEMORY);
            protocol.setMem(str);
        }

        List<ComputeParameter> list = null;
        //set targets
        if(protocolListing.indexOf(FLAG_TARGETS) > -1)
        {
            str = protocolListing.substring(protocolListing.indexOf(FLAG_TARGETS),
                    protocolListing.indexOf("\n", protocolListing.indexOf(FLAG_TARGETS)));
            list = getParametersFromHeader(str);
            protocol.setIterateOver(list);
        }

        //set inputs
        if(protocolListing.indexOf(FLAG_INPUTS) > -1)
        {
            str = protocolListing.substring(protocolListing.indexOf(FLAG_INPUTS),
                            protocolListing.indexOf("\n", protocolListing.indexOf(FLAG_INPUTS)));
            list = getParametersFromHeader(str);
            protocol.setInputs(list);
        }

        //set outputs
        if(protocolListing.indexOf(FLAG_OUTPUTS) > -1)
        {
            str = protocolListing.substring(protocolListing.indexOf(FLAG_OUTPUTS),
                            protocolListing.indexOf("\n", protocolListing.indexOf(FLAG_OUTPUTS)));
            list = getParametersFromHeader(str);
            protocol.setOutputs(list);
        }

        //set exes
        if(protocolListing.indexOf(FLAG_EXES) > -1)
        {
            str = protocolListing.substring(protocolListing.indexOf(FLAG_EXES),
                                protocolListing.indexOf("\n", protocolListing.indexOf(FLAG_EXES)));
            list = getParametersFromHeader(str);
            protocol.setExes(list);
        }

        //set logs
        if(protocolListing.indexOf(FLAG_LOG) > -1)
        {
            str = protocolListing.substring(protocolListing.indexOf(FLAG_LOG),
                                protocolListing.indexOf("\n", protocolListing.indexOf(FLAG_LOG)));
            list = getParametersFromHeader(str);
            protocol.setLog(list);
        }

        System.out.println("  ... parsed");
        return protocol;
    }

    private List<ComputeParameter> getParametersFromHeader(String str)
    {
        List<ComputeParameter> list = new ArrayList<ComputeParameter>();
        Vector<String> names = findNames(str);

        for(int i = 0; i < names.size(); i++)
        {
            ComputeParameter par = findParameter(names.elementAt(i));
            list.add(par);
        }

        return list;
    }

    private ComputeParameter findParameter(String s)
    {
        Collection<ComputeParameter> parameters = workflow.getWorkflowComputeParameterCollection();

        Iterator<ComputeParameter> itr = parameters.iterator();
        while(itr.hasNext())
        {
            ComputeParameter par = itr.next();
            String name = par.getName();

            if(s.equalsIgnoreCase(name))
                return par;
        }

        logger.log(Level.ERROR, "parameter " + s + " does not exist");
        System.exit(1);
        return null;
    }

    //here, we trim first to remove end string white spaces
    private Vector<String> findNames(String list)
    {
        list = list.trim();

        Vector<String> names = new Vector<String>();
        int posEmpty = list.indexOf(" ") + 1;

        if(posEmpty == 0)
            return names;

        list = list.substring(posEmpty);

        while(list.indexOf(",") > -1)
        {
            int posComa = list.indexOf(",");
            String name = list.substring(0, posComa).trim();
            if(name != "")
                names.addElement(name);
            list = list.substring(posComa + 1);
        }
            names.add(list);
        return names;
    }


    private String getValueFromMolgenisHeader(String str, String flag)
    {
        String value;
        int index = str.indexOf("=", str.indexOf(flag)) + 1;

        int indexSpace = str.indexOf(" ", index);

        if(indexSpace > -1)
            value = str.substring(index, indexSpace);
        else
            value = str.substring(index);

        return value;
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
