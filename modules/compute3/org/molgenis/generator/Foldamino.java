package org.molgenis.generator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.molgenis.compute.ComputeParameter;
import org.molgenis.compute.ComputeProtocol;
import org.molgenis.protocol.Workflow;
import org.molgenis.protocol.WorkflowElement;
import org.molgenis.util.Tuple;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 19/04/2012
 * Time: 12:49
 * To change this template use File | Settings | File Templates.
 */
public class Foldamino
{
    private String flag = "#FOREACH";
    private List<ComputeParameter> parameters = null;
    private ModelLoader loader = new ModelLoader();
    private Workflow workflow;

    private static Logger logger = Logger.getLogger(Foldamino.class);


    public List<Hashtable> removeUnused(List<Hashtable> worksheet, List<ComputeParameter> parameters)
    {
        this.parameters = parameters;

        //in principle all hashtables should be identical
        Hashtable<String, String> hashtable0 = worksheet.get(0);

        Enumeration keys = hashtable0.keys();
        while (keys.hasMoreElements())
        {
            String key = (String) keys.nextElement();

            boolean isUsed = getIsUsed(key);

            if (!isUsed)
            {
                for (Hashtable hashtable : worksheet)
                {
                    hashtable.remove(key);
                }
            }
        }

        return worksheet;
    }

    private boolean getIsUsed(String field)
    {
        for (ComputeParameter parameter : parameters)
        {
            if (field.equalsIgnoreCase(parameter.getName()))
            {
                return true;
            }
        }
        return false;
    }

    public List<Hashtable> transformToTable(List<Tuple> worksheet)
    {
        List<Hashtable> list = new ArrayList<Hashtable>();

        for (Tuple tuple : worksheet)
        {
            Hashtable hashtable = new Hashtable();
            List<String> fields = tuple.getFields();
            for (String field : fields)
            {
                if (field == null)
                    continue;
                else if (field.equalsIgnoreCase(""))
                    continue;

                String value = tuple.getString(field);
                System.out.println(field + " -> " + value);

                if (value == null)
                    continue;
                hashtable.put(field, value);
            }
            list.add(hashtable);
        }
        return list;
    }

    public void testFolding(Workflow workflow, List<Hashtable> worksheet)
    {
        this.workflow = workflow;
        Collection<WorkflowElement> elements = workflow.getWorkflowWorkflowElementCollection();

        for (WorkflowElement element : elements)
        {
            ComputeProtocol protocol = (ComputeProtocol) element.getProtocol();

            List<ComputeParameter> targets = findTargets(protocol.getScriptTemplate());
            if (targets == null)
            {
                System.out.println(element.getName() + " has no targets ");
            }
            else
            {
                System.out.println(element.getName() + " has targets!");
                List<Hashtable> table = fold(targets, worksheet);
            }
        }
    }

    private List<Hashtable> fold(List<ComputeParameter> targets, List<Hashtable> worksheet)
    {
        //we find all used parameters
        Vector<ComputeParameter> parameters = new Vector<ComputeParameter>();

        //we assume that all hashtables are the same, which should be true
        Hashtable<String, String> hashtable0 = worksheet.get(0);

        Enumeration keys = hashtable0.keys();
        while (keys.hasMoreElements())
        {
            String key = (String) keys.nextElement();
            ComputeParameter par = findParameter(key);
            parameters.add(par);
        }

        //vector of all targets
        Vector<String> vecTargetNames = new Vector<String>();

        //find all hasOne
        //first find initial hasOnes
        Vector<String> vecHasOneNames = new Vector<String>();
        for(ComputeParameter target : targets)
        {
            //this does not work - target.getHasOne()
            vecTargetNames.add(target.getName());
            List<ComputeParameter> hasOnes = target.getHasOne();
            List<String> hasOneNames = target.getHasOne_Name();
            vecHasOneNames.addAll(hasOneNames);
            continue;
        }

        int prevSize = 0;

        //now find the rest
        while(vecHasOneNames.size() > prevSize)
        {
            Vector<String> temp = new Vector<String>();
            for(String hasOneName : vecHasOneNames)
            {
                ComputeParameter par = findParameter(hasOneName);
                List<String> hasOneNames = par.getHasOne_Name();
                temp.addAll(hasOneNames);

            }
            vecHasOneNames.addAll(temp);
            prevSize = vecHasOneNames.size();
        }

        //now we create new folded list
        List<Hashtable> folded = new ArrayList<Hashtable>();

        //foldered line, where Object can be String of List<String>
        Hashtable<String, Object> line = null;

        Vector<FoldaminoUniqueContainer> uniqueContainers = new Vector<FoldaminoUniqueContainer>();

        for(Hashtable initialLine: worksheet)
        {
            System.out.println("#" + initialLine.toString());

            //vector of all target values is used to build unique key-combination
            //unique string
            String keyCombination = "";
            for(String field : vecTargetNames)
            {
                String value = (String) initialLine.get(field);
                keyCombination += value;
            }

            //vector of all hasOne is used to build hasOne unique combination
            String hasOneCombination = "";
            for(String field : vecHasOneNames)
            {
                String value = (String) initialLine.get(field);
                hasOneCombination += value;
            }

            //check if combination is new unique and create new container
            // or exist and add to exist container
            uniqueContainers = evaluateUniqueness(uniqueContainers, keyCombination, hasOneCombination, initialLine);
        }

        for(FoldaminoUniqueContainer container : uniqueContainers)
        {
            Vector<Hashtable> initialLines = container.getAllHashtablles();
            if(initialLines.size() > 1)
            {
                line = new Hashtable<String, Object>();
//                for(Hashtable initialLine initialLines)
//                {
//
//                }
            }
            else
            {
                line = initialLines.elementAt(0);
            }
            folded.add(line);
        }

        return folded;
    }

    private Vector<FoldaminoUniqueContainer> evaluateUniqueness(Vector<FoldaminoUniqueContainer> uniqueContainers,
                                                                String keyCombination, String hasOneCombination, Hashtable initialLine)
    {
        boolean unique = true;
        for(FoldaminoUniqueContainer container : uniqueContainers)
        {
            String key = container.getKey();
            if(key.equalsIgnoreCase(keyCombination))
            {
                //it is not unique
                //let check hasOneCombination
                String hasOne = container.getHasOne();
                if(hasOne.equalsIgnoreCase(hasOneCombination))
                {
                    //it should be foldered
                    container.addElement(initialLine);
                    unique = false;
                    break;
                }
                else
                {
                    logger.log(Level.ERROR, "hasOne properties " + hasOne + " & " + hasOneCombination + " are not correct ");
                    System.exit(1);
                }
            }
        }

        if(unique)
        {
                FoldaminoUniqueContainer newContainer = new FoldaminoUniqueContainer(keyCombination, hasOneCombination, initialLine);
                uniqueContainers.add(newContainer);
        }
        return uniqueContainers;  //To change body of created methods use File | Settings | File Templates.
    }

    private List<ComputeParameter> findTargets(String protocol)
    {
        List<ComputeParameter> list = null;
        if (protocol.indexOf(flag) > -1)
        {
            String str = protocol.substring(protocol.indexOf(flag),
                    protocol.indexOf("\n", protocol.indexOf(flag)));
            list = getParametersFromHeader(str);
            return list;
        }
        return list;
    }

    public List<ComputeParameter> getParametersFromHeader(String str)
    {
        List<ComputeParameter> list = new ArrayList<ComputeParameter>();
        Vector<String> names = findNames(str);

        for (int i = 0; i < names.size(); i++)
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
        while (itr.hasNext())
        {
            ComputeParameter par = itr.next();
            String name = par.getName();

            if (s.equalsIgnoreCase(name))
                return par;
        }

        System.exit(1);
        return null;
    }

    private Vector<String> findNames(String list)
    {
        list = list.trim();

        Vector<String> names = new Vector<String>();
        int posEmpty = list.indexOf(" ") + 1;

        if (posEmpty == 0)
            return names;

        list = list.substring(posEmpty);

        while (list.indexOf(",") > -1)
        {
            int posComa = list.indexOf(",");
            String name = list.substring(0, posComa).trim();
            if (name != "")
                names.addElement(name);
            list = list.substring(posComa + 1);
        }
        names.add(list);
        return names;
    }
}
