package org.molgenis.compute.test.util;

import app.DatabaseFactory;
import org.molgenis.compute.design.*;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.util.Tuple;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: georgebyelas
 * Date: 15/08/2012
 * Time: 13:15
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowImporterJDBC
{
    private File workflowDir, parametersFile, workflowElements, protocolsDir;

    public static void main(String[] args)
    {
        if (args.length == 4)
        {
            System.out.println("*** START WORKFLOW IMPORT");
        }
        else
        {
            System.out.println("Not enough parameters");
            System.exit(1);
        }

        try
        {
            new WorkflowImporterJDBC().process(args[0], args[1], args[2], args[3]);
        }
        catch (DatabaseException e)
        {
            e.printStackTrace();
        }
    }

    private void process(String arg0, String arg1, String arg2, String arg3) throws DatabaseException
    {
        //self-explanatory code
        workflowDir = new File(arg0);
        parametersFile = new File(arg1);
        workflowElements = new File(arg2);
        protocolsDir = new File(arg3);

        isExist(workflowDir);
        isExist(parametersFile);
        isExist(workflowElements);
        isExist(protocolsDir);

        Database db = DatabaseFactory.create();
        try
        {
            db.beginTx();

            String workflowName = workflowDir.getName();

            //add workflow
            Workflow workflow = new Workflow();
            workflow.setName(workflowName);
            db.add(workflow);

            //add requirement
            ComputeRequirement requirement = new ComputeRequirement();
            requirement.setName("TestRequirement");
            requirement.setCores(1);
            requirement.setNodes(1);
            requirement.setMem("test");
            requirement.setWalltime("test");
            db.add(requirement);

            //add parameters
            CsvReader reader = new CsvFileReader(parametersFile);
            Hashtable<ComputeParameter, Vector<String>> collectionParameterHasOnes = new Hashtable<ComputeParameter, Vector<String>>();
            Vector<ComputeParameter> parameters = new Vector<ComputeParameter>();
            for (Tuple row : reader)
            {
                String name = row.getString("Name");
                String defaultValue = row.getString("defaultValue");
                String description = row.getString("description");
                String dataType = row.getString("dataType");
                String hasOne_name = row.getString("hasOne_name");

                ComputeParameter parameter = new ComputeParameter();
                parameter.setName(name);
                parameter.setDefaultValue(defaultValue);
                if(dataType == null)
                    parameter.setDataType("string");
                else
                    parameter.setDataType(dataType);
                parameter.setWorkflow(workflow);

                if (hasOne_name != null)
                {
                    Vector<String> hasOnes = processComas(hasOne_name);
                    collectionParameterHasOnes.put(parameter, hasOnes);
                }
                parameters.add(parameter);
            }

            //find parameters has ones
            Enumeration ekeys = collectionParameterHasOnes.keys();
            while (ekeys.hasMoreElements())
            {
                ComputeParameter parameter = (ComputeParameter) ekeys.nextElement();
                Vector<String> parNames = collectionParameterHasOnes.get(parameter);

                Vector<ComputeParameter> vecParameters = new Vector<ComputeParameter>();
                for (String name : parNames)
                {
                    ComputeParameter hasParameter = findParameter(parameters, name);
                    vecParameters.add(hasParameter);
                }
                parameter.setHasOne(vecParameters);
            }

            for (ComputeParameter parameter : parameters)
            {
                db.add(parameter);
            }

            //add protocols
            Vector<ComputeProtocol> protocols = new Vector<ComputeProtocol>();
            if (protocolsDir.isDirectory())
            {
                String[] files = protocolsDir.list();
                if (files.length > 0)
                {
                    for (int i = 0; i < files.length; i++)
                    {
                        String fName = files[i];
                        String protocolName;

                        int dotPos = fName.lastIndexOf(".");
                        if (dotPos > -1)
                        {
                            protocolName = fName.substring(0, dotPos);
                        }
                        else
                            protocolName = fName;

                        String path = protocolsDir.getPath() + System.getProperty("file.separator") + fName;
                        String listing = getFileAsString(path);

                        //TODO here we need to agree on flags and reuse code from compute3 or compute4
                        //also why do we have requirements here if we have xref to ComputeRequirements

                        ComputeProtocol protocol = new ComputeProtocol();
                        protocol.setName(protocolName);
                        protocol.setScriptTemplate(listing);
                        protocol.setRequirements(requirement);

                        //
                        protocol.setCores(1);
                        protocol.setNodes(1);
                        protocol.setMem("");
                        protocol.setWalltime("");

                        protocols.add(protocol);
                        db.add(protocol);
                    }
                }
            }

            //add workflow elements
            Vector<WorkflowElement> elements = new Vector<WorkflowElement>();
            reader = new CsvFileReader(workflowElements);
            for (Tuple row : reader)
            {
                String name = row.getString("name");
                String strProtocol = row.getString("protocol_name");
                String strPrevious = row.getString("PreviousSteps_name");

                WorkflowElement element = new WorkflowElement();
                element.setName(name);
                element.setWorkflow(workflow);

                ComputeProtocol p = findProtocol(protocols, strProtocol);
                element.setProtocol(p);

                if (strPrevious != null)
                {
                    Vector<String> prevNames = processComas(strPrevious);
                    Vector<WorkflowElement> vecPrevious = new Vector<WorkflowElement>();
                    List<Integer> ids = new ArrayList<Integer>();

                    for(String prev : prevNames)
                    {
                        WorkflowElement elPrev = findWorkflowElement(elements, prev);
                        vecPrevious.add(elPrev);
                        ids.add(elPrev.getId());
                    }
                    element.setPreviousSteps_Id(ids);
//                    element.setPreviousSteps(vecPrevious);
                }

                elements.add(element);
                db.add(element);
            }

            db.commitTx();
            System.out.println("... done");
        }
        catch (Exception e)
        {
            db.rollbackTx();
            e.printStackTrace();
        }


    }

    private WorkflowElement findWorkflowElement(Vector<WorkflowElement> vector, String name)
    {
        for (WorkflowElement par : vector)
        {
            if (par.getName().equalsIgnoreCase(name))
                return par;
        }
        return null;
    }

    private ComputeProtocol findProtocol(Vector<ComputeProtocol> vector, String name)
    {
        for (ComputeProtocol par : vector)
        {
            if (par.getName().equalsIgnoreCase(name))
                return par;
        }
        return null;
    }

    private ComputeParameter findParameter(Vector<ComputeParameter> vector, String name)
    {
        for (ComputeParameter par : vector)
        {
            if (par.getName().equalsIgnoreCase(name))
                return par;
        }
        return null;
    }


    private void isExist(File file)
    {
        if (!file.exists())
        {
            System.out.println("Error: " + file.getName() + " does not exist");
            System.exit(1);
        }
    }

    private Vector<String> processComas(String list)
    {
        list = list.trim();
        Vector<String> values = new Vector<String>();

        while (list.indexOf(",") > -1)
        {
            int posComa = list.indexOf(",");
            String name = list.substring(0, posComa).trim();
            if (name != "")
                values.addElement(name);
            list = list.substring(posComa + 1);
        }
        values.add(list);
        return values;
    }

    private final String getFileAsString(String filename) throws IOException
    {
        File file = new File(filename);

        final BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream(file));
        final byte[] bytes = new byte[(int) file.length()];
        bis.read(bytes);
        bis.close();
        return new String(bytes);
    }
}
