package org.molgenis.generator;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.molgenis.compute.ComputeJob;
import org.molgenis.compute.ComputeParameter;
import org.molgenis.compute.ComputeProtocol;
import org.molgenis.compute.commandline.Worksheet;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.protocol.Workflow;
import org.molgenis.protocol.WorkflowElement;
import org.molgenis.util.Pair;
import org.molgenis.util.Tuple;

import java.io.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 05/04/2012
 * Time: 09:46
 * To change this template use File | Settings | File Templates.
 */


//absolite class with fold/reduce trials
public class GenericJobGenerator implements JobGenerator
{

    private static Logger logger = Logger.getLogger(GenericJobGenerator.class);

    private FoldingMaker foldingMaker = new FoldingMaker();
    private FoldingParser foldingParser = new FoldingParser();

    //template sources
    private String templateGridDownload;
    private String templateGridDownloadExe;
    private String templateGridUpload;
    private String templateGridJDL;
    private String templateGridAfterExecution;
    private String templateGridUploadLog;

    private String templateClusterSubmission;
    private String templateClusterHeader;
    private String templateClusterFooter;

    //template filenames
    private String fileTemplateGridDownload = "templ-download-grid.ftl";
    private String fileTemplateGridDownloadExe = "templ-exe-grid.ftl";
    private String fileTemplateGridUpload = "templ-upload-grid.ftl";
    private String fileTemplateGridUploadLog = "templ-upload-grid-log.ftl";
    private String fileTemplateGridJDL = "templ-jdl-grid.ftl";
    private String fileTemplateGridAfterExecution = "templ-after-exe.ftl";

    private String fileTemplateClusterHeader = "templ-pbs-header.ftl";
    private String fileTemplateClusterFooter = "templ-pbs-footer.ftl";
    private String fileTemplateClusterSubmission = "templ-submit.ftl";

    //used for grid generation
    private Hashtable<String, GridTransferContainer> pairJobTransfers = null;

    //used for cluster generation - submit script
    private Hashtable<WorkflowElement, ComputeJob> pairWEtoCJ = null;
    private Hashtable<ComputeJob, WorkflowElement> pairCJtoWE = null;

    private String submitScript = null;

    private Hashtable<String, String> config;

    public Vector<ComputeJob> generateComputeJobsWorksheet(Workflow workflow, List<Tuple> worksheet, String backend)
    {
        Vector<ComputeJob> computeJobs = new Vector<ComputeJob>();

        if (backend.equalsIgnoreCase(JobGenerator.GRID))
            pairJobTransfers = new Hashtable<String, GridTransferContainer>();

        if (backend.equalsIgnoreCase(JobGenerator.CLUSTER))
        {
            pairWEtoCJ = new Hashtable<WorkflowElement, ComputeJob>();
            pairCJtoWE = new Hashtable<ComputeJob, WorkflowElement>();
            submitScript = "";
        }
        //because Hashtable does not allow null keys or values
        Hashtable<String, String> values = new Hashtable<String, String>();

        //parameters with templates
        Vector<ComputeParameter> complexParameters = new Vector<ComputeParameter>();

        //fill hashtable with workflow global parameters only once
        Collection<ComputeParameter> parameters = workflow.getWorkflowComputeParameterCollection();
        Iterator<ComputeParameter> itParameter = parameters.iterator();
        while (itParameter.hasNext())
        {
            ComputeParameter parameter = itParameter.next();
            if (parameter.getDefaultValue() != null)
            {
                if (parameter.getDefaultValue().contains("${"))
                {
                    complexParameters.addElement(parameter);
                }
                else
                {
                    values.put(parameter.getName(), parameter.getDefaultValue());
                }
            }
            else
                values.put(parameter.getName(), "");
        }

        //produce jobs for every worksheet record
        for (int i = 0; i < worksheet.size(); i++)
        {
            int ngs_id = 1;

            //add parameters from worksheet to values
            Tuple tuple = worksheet.get(i);
            List<String> names = tuple.getFields();

            String id = "id";
            for (String name : names)
            {
                String value = tuple.getString(name);

                //to avoid empty worksheet fields
                if (value == null)
                {
                    break;
                }
                values.put(name, value);

                id += "_" + value;
            }

            //temporary until folding is implemented
            if (workflow.getName().equalsIgnoreCase("ngs_demo"))
            {
                //id = "id_" + System.currentTimeMillis();
                id = "id" + ngs_id;
                ngs_id++;
            }

            //weave complex parameters
            int count = 0;
            while ((complexParameters.size() > 0) && (count < 10))
            {
                Vector<ComputeParameter> toRemove = new Vector<ComputeParameter>();
                for (ComputeParameter computeParameter : complexParameters)
                {
                    String complexValue = weaveFreemarker(computeParameter.getDefaultValue(), values);
                    //                   values.put(computeParameter.getName(), complexValue);
                    //                   complexParameters.remove(computeParameter);

                    if (complexValue.contains("${"))
                    {
                        System.out.println(computeParameter.getName() + " -> " + complexValue);
                    }
                    else
                    {
                        values.put(computeParameter.getName(), complexValue);
                        toRemove.add(computeParameter);
                    }
                }
                complexParameters.removeAll(toRemove);
                System.out.println("loop " + count + " removed " + toRemove.size());
                count++;
            }

            //check correctness
            int number = 0;
            Enumeration keys = values.keys();
            while (keys.hasMoreElements())
            {
                String key = (String) keys.nextElement();
                String value = values.get(key);
                System.out.println(number + "\t" + key + " -> " + value);
                number++;
            }

            //read all workflow elements
            Collection<WorkflowElement> workflowElements = workflow.getWorkflowWorkflowElementCollection();
            Iterator<WorkflowElement> itr = workflowElements.iterator();
            while (itr.hasNext())
            {
                WorkflowElement el = itr.next();
                ComputeProtocol protocol = (ComputeProtocol) el.getProtocol();
                String template = protocol.getScriptTemplate();

                String jobListing = weaveFreemarker(template, values);

                ComputeJob job = new ComputeJob();

                String jobName = config.get(JobGenerator.GENERATION_ID) + "_" +
                        workflow.getName() + "_" +
                        el.getName() + "_" + id;

                job.setName(jobName);
                job.setProtocol(protocol);
                job.setComputeScript(jobListing);
                computeJobs.add(job);

                //fill containers for grid jobs to ensure correct data transfer
                // and for cluster to generate submit script
                if (backend.equalsIgnoreCase(JobGenerator.GRID))
                {
                    GridTransferContainer container = fillContainerStr(protocol, values);
                    pairJobTransfers.put(job.getName(), container);
                }
                else if (backend.equalsIgnoreCase(JobGenerator.CLUSTER))
                {
                    pairWEtoCJ.put(el, job);
                    pairCJtoWE.put(job, el);
                }
                logger.log(Level.DEBUG, "----------------------------------------------------------------------");
                logger.log(Level.DEBUG, el.getName());
                logger.log(Level.DEBUG, jobListing);
                logger.log(Level.DEBUG, "----------------------------------------------------------------------");

            }
        }
        return computeJobs;
    }

    private GridTransferContainer fillContainerStr(ComputeProtocol protocol, Hashtable<String, String> values)
    {
        GridTransferContainer container = new GridTransferContainer();

        List<ComputeParameter> inputs = protocol.getInputs();
        for (ComputeParameter input : inputs)
        {
            String name = input.getName();
            String value = values.get(name);
            container.addInput(name, value);
        }

        List<ComputeParameter> outputs = protocol.getOutputs();
        for (ComputeParameter output : outputs)
        {
            String name = output.getName();
            String value = values.get(name);
            container.addOutput(name, value);
        }

        List<ComputeParameter> exes = protocol.getExes();
        for (ComputeParameter exe : exes)
        {
            String name = exe.getName();
            String value = values.get(name);
            container.addExe(name, value);
        }

        List<ComputeParameter> logs = protocol.getLogs();
        for (ComputeParameter log : logs)
        {
            String name = log.getName();
            String value = values.get(name);
            container.addLog(name, value);
        }

        return container;
    }

    public Vector<ComputeJob> generateComputeJobsWorksheetWithFolding(Workflow workflow, List<Tuple> worksheet, String backend)
    {
        Vector<ComputeJob> computeJobs = new Vector<ComputeJob>();

        if (backend.equalsIgnoreCase(JobGenerator.GRID))
            pairJobTransfers = new Hashtable<String, GridTransferContainer>();

        if (backend.equalsIgnoreCase(JobGenerator.CLUSTER))
        {
            pairWEtoCJ = new Hashtable<WorkflowElement, ComputeJob>();
            pairCJtoWE = new Hashtable<ComputeJob, WorkflowElement>();
            submitScript = "";
        }
        //because Hashtable does not allow null keys or values
        Hashtable<String, String> values = new Hashtable<String, String>();

        //parameters with templates
        Vector<ComputeParameter> complexParameters = new Vector<ComputeParameter>();

        //fill hashtable with workflow global parameters only once
        Collection<ComputeParameter> parameters = workflow.getWorkflowComputeParameterCollection();

        Collection<WorkflowElement> workflowElements = workflow.getWorkflowWorkflowElementCollection();
        Iterator<WorkflowElement> itr = workflowElements.iterator();
        while (itr.hasNext())
        {
            WorkflowElement el = itr.next();
            ComputeProtocol protocol = (ComputeProtocol) el.getProtocol();
            String template = protocol.getScriptTemplate();

            List<String> targets = protocol.getIterateOver_Name();
            if (targets.size() == 0)
            {
                targets.add("line_number");
            }

            List<Tuple> folded = Worksheet.foldWorksheet(worksheet, (List<ComputeParameter>) parameters, targets);

            Iterator<ComputeParameter> itParameter = parameters.iterator();
            while (itParameter.hasNext())
            {
                ComputeParameter parameter = itParameter.next();
                if (parameter.getDefaultValue() != null)
                {
                    if (parameter.getDefaultValue().contains("${"))
                    {
                        complexParameters.addElement(parameter);
                    }
                    else
                    {
                        values.put(parameter.getName(), parameter.getDefaultValue());
                    }
                }
                else
                    values.put(parameter.getName(), "");
            }

            int ngs_count = 1;
            //produce jobs for every worksheet record
            for (int i = 0; i < folded.size(); i++)
            {
                //add parameters from worksheet to values
                Tuple tuple = folded.get(i);
                List<String> names = tuple.getFields();

                String id = "id";

                for (String name : names)
                {
                    String value = tuple.getString(name);

                    //to avoid empty worksheet fields
                    if (value == null)
                    {
                        values.put(name, "");
                        //break;
                    }
                    else
                        values.put(name, value);

                    id += "_" + value;
                }

                //temporary until folding is implemented
                if (workflow.getName().equalsIgnoreCase("ngs_demo"))
                {
                    //id = "id_" + System.currentTimeMillis();
                    id = "id_" + ngs_count;
                    ngs_count++;
                }

                //weave complex parameters
                int count = 0;
                while ((complexParameters.size() > 0) && (count < 10))
                {
                    Vector<ComputeParameter> toRemove = new Vector<ComputeParameter>();
                    for (ComputeParameter computeParameter : complexParameters)
                    {
                        String complexValue = weaveFreemarker(computeParameter.getDefaultValue(), values);
                        //                   values.put(computeParameter.getName(), complexValue);
                        //                   complexParameters.remove(computeParameter);

                        if (complexValue.contains("${"))
                        {
                            System.out.println(computeParameter.getName() + " -> " + complexValue);
                        }
                        else
                        {
                            values.put(computeParameter.getName(), complexValue);
                            toRemove.add(computeParameter);
                        }
                    }
                    complexParameters.removeAll(toRemove);
                    System.out.println("loop " + count + " removed " + toRemove.size());
                    count++;
                }

                //check correctness
                int number = 0;
                Enumeration keys = values.keys();
                while (keys.hasMoreElements())
                {
                    String key = (String) keys.nextElement();
                    String value = values.get(key);
                    System.out.println(number + "\t" + key + " -> " + value);
                    number++;
                }


                String jobListing = weaveFreemarker(template, values);

                ComputeJob job = new ComputeJob();

                String jobName = config.get(JobGenerator.GENERATION_ID) + "_" +
                        workflow.getName() + "_" +
                        el.getName() + "_" + id;

                job.setName(jobName);
                job.setProtocol(protocol);
                job.setComputeScript(jobListing);
                computeJobs.add(job);

                //fill containers for grid jobs to ensure correct data transfer
                // and for cluster to generate submit script
                if (backend.equalsIgnoreCase(JobGenerator.GRID))
                {
                    GridTransferContainer container = fillContainerStr(protocol, values);
                    pairJobTransfers.put(job.getName(), container);
                }
                else if (backend.equalsIgnoreCase(JobGenerator.CLUSTER))
                {
                    pairWEtoCJ.put(el, job);
                    pairCJtoWE.put(job, el);
                }
                logger.log(Level.DEBUG, "----------------------------------------------------------------------");
                logger.log(Level.DEBUG, el.getName());
                logger.log(Level.DEBUG, jobListing);
                logger.log(Level.DEBUG, "----------------------------------------------------------------------");

            }
        }
        return computeJobs;
    }

    public Vector<ComputeJob> generateComputeJobsWorksheetWithFoldingNew(Workflow workflow, List<Tuple> f, String backend)
    {
        //create the table with targets, which is equal to worksheet if there are no targets
        List<Hashtable> table = null;

        //remove unused parameters from the worksheet
        List<Hashtable> worksheet = foldingMaker.transformToTable(f);
        foldingMaker.setWorkflow(workflow);
        worksheet = foldingMaker.removeUnused(worksheet, (List<ComputeParameter>) workflow.getWorkflowComputeParameterCollection());

        //some supplementary hashtables
        Vector<ComputeJob> computeJobs = new Vector<ComputeJob>();

        if (backend.equalsIgnoreCase(JobGenerator.GRID))
            pairJobTransfers = new Hashtable<String, GridTransferContainer>();

        if (backend.equalsIgnoreCase(JobGenerator.CLUSTER))
        {
            pairWEtoCJ = new Hashtable<WorkflowElement, ComputeJob>();
            pairCJtoWE = new Hashtable<ComputeJob, WorkflowElement>();
            submitScript = "";
        }

        Collection<ComputeParameter> parameters = workflow.getWorkflowComputeParameterCollection();


        Collection<WorkflowElement> workflowElements = workflow.getWorkflowWorkflowElementCollection();
        Iterator<WorkflowElement> itr = workflowElements.iterator();
        while (itr.hasNext())
        {
            //because Hashtable does not allow null keys or values used for weaving
            Hashtable<String, Object> values = new Hashtable<String, Object>();

            //parameters which are templates
            Vector<ComputeParameter> complexParameters = new Vector<ComputeParameter>();

            WorkflowElement el = itr.next();
            ComputeProtocol protocol = (ComputeProtocol) el.getProtocol();
            String template = protocol.getScriptTemplate();

            //chack if we have any targets
            List<ComputeParameter> targets = foldingMaker.findTargets(protocol.getScriptTemplate());
            if (targets != null)
            {
                table = foldingMaker.fold(targets, worksheet);
            }
            else
                table = worksheet;

            Iterator<ComputeParameter> itParameter = parameters.iterator();
            while (itParameter.hasNext())
            {
                ComputeParameter parameter = itParameter.next();
                if (parameter.getDefaultValue() != null)
                {
                    if (parameter.getDefaultValue().contains("${"))
                    {
                        complexParameters.addElement(parameter);
                    }
                    else
                    {
                        values.put(parameter.getName(), parameter.getDefaultValue());
                    }
                }
                else
                    values.put(parameter.getName(), "");
            }

            //weave complex parameters without folding
            //because folding related to protocols
            for (int i = 0; i < worksheet.size(); i++)
            {
                Hashtable<String, Object> line = worksheet.get(i);

                Enumeration ekeys = line.keys();
                while (ekeys.hasMoreElements())
                {
                    String ekey = (String) ekeys.nextElement();
                    Object eValues = line.get(ekey);
                    values.put(ekey, eValues);
                }

                int count = 0;
                while ((complexParameters.size() > 0) && (count < 10))
                {
                    Vector<ComputeParameter> toRemove = new Vector<ComputeParameter>();
                    for (ComputeParameter computeParameter : complexParameters)
                    {
                        String complexValue = foldingMaker.weaveFreemarker(computeParameter.getDefaultValue(), values);

                        if (complexValue.contains("${"))
                        {
                            System.out.println(computeParameter.getName() + " -> " + complexValue);
                        }
                        else
                        {
                            values.put(computeParameter.getName(), complexValue);
                            toRemove.add(computeParameter);
                        }
                    }
                    complexParameters.removeAll(toRemove);
                    System.out.println("loop " + count + " removed " + toRemove.size());
                    count++;
                }
            }

            //now we start to use foldered worksheet
            for (int i = 0; i < table.size(); i++)
            {
                String id = "id";
                Hashtable<String, Object> line = table.get(i);

                Enumeration ekeys = line.keys();
                while (ekeys.hasMoreElements())
                {
                    String ekey = (String) ekeys.nextElement();
                    Object eValues = line.get(ekey);
                    values.put(ekey, eValues);

                    id += "_" + eValues.toString();
                }

                //temporary until folding is implemented
                if (workflow.getName().equalsIgnoreCase("ngs_demo"))
                {
                    //id = "id_" + System.currentTimeMillis();
                    id = "id_" + i;
                }

                String jobListing = foldingMaker.weaveFreemarker(template, values);

                ComputeJob job = new ComputeJob();

                String jobName = config.get(JobGenerator.GENERATION_ID) + "_" +
                        workflow.getName() + "_" +
                        el.getName() + "_" + id;

                job.setName(jobName);
                job.setProtocol(protocol);
                job.setComputeScript(jobListing);
                computeJobs.add(job);

                //fill containers for grid jobs to ensure correct data transfer
                // and for cluster to generate submit script
                if (backend.equalsIgnoreCase(JobGenerator.GRID))
                {
                    GridTransferContainer container = fillContainer(protocol, values);
                    pairJobTransfers.put(job.getName(), container);
                }
                else if (backend.equalsIgnoreCase(JobGenerator.CLUSTER))
                {
                    pairWEtoCJ.put(el, job);
                    pairCJtoWE.put(job, el);
                }
                logger.log(Level.DEBUG, "----------------------------------------------------------------------");
                logger.log(Level.DEBUG, el.getName());
                logger.log(Level.DEBUG, jobListing);
                logger.log(Level.DEBUG, "----------------------------------------------------------------------");

            }
        }
        return computeJobs;
    }

    public Vector<ComputeJob> generateComputeJobsFoldedWorksheet(Workflow workflow, List<Tuple> f, String backend)
    {
        //create the table with targets, which is equal to worksheet if there are no targets
        List<Hashtable> table = null;

        //remove unused parameters from the worksheet
        List<Hashtable> worksheet = foldingMaker.transformToTable(f);
        foldingMaker.setWorkflow(workflow);
        worksheet = foldingMaker.removeUnused(worksheet, (List<ComputeParameter>) workflow.getWorkflowComputeParameterCollection());

        //some supplementary hashtables
        Vector<ComputeJob> computeJobs = new Vector<ComputeJob>();

        if (backend.equalsIgnoreCase(JobGenerator.GRID))
            pairJobTransfers = new Hashtable<String, GridTransferContainer>();

        if (backend.equalsIgnoreCase(JobGenerator.CLUSTER))
        {
            pairWEtoCJ = new Hashtable<WorkflowElement, ComputeJob>();
            pairCJtoWE = new Hashtable<ComputeJob, WorkflowElement>();
            submitScript = "";
        }

        Collection<ComputeParameter> parameters = workflow.getWorkflowComputeParameterCollection();
        Collection<WorkflowElement> workflowElements = workflow.getWorkflowWorkflowElementCollection();
        for (WorkflowElement el : workflowElements)
        {
            //because Hashtable does not allow null keys or values used for weaving
            Hashtable<String, Object> values = new Hashtable<String, Object>();

            //parameters which are templates and do directly depend on worksheet values
            Vector<ComputeParameter> complexParametersDepend = new Vector<ComputeParameter>();

            //parameters which are templates but do not directly depend on worksheet values
            Vector<ComputeParameter> complexParametersIndepend = new Vector<ComputeParameter>();

            //hashtable with simple values, we need it for initial weaving
            Hashtable<String, String> simpleValues = new Hashtable<String, String>();

            ComputeProtocol protocol = (ComputeProtocol) el.getProtocol();
            String template = protocol.getScriptTemplate();

            //chack if we have any targets
            List<ComputeParameter> targets = foldingMaker.findTargets(protocol.getScriptTemplate());
            if (targets != null)
            {
                table = foldingMaker.fold(targets, worksheet);
            }
            else
                table = worksheet;

            //now we start to use foldered worksheet
            //all our parameters, that depend on foldered worksheet, will become lists as well, that upset me a lot :(
            for (int i = 0; i < table.size(); i++)
            {
                String id = "id";
                Hashtable<String, Object> line = table.get(i);

                Enumeration ekeys = line.keys();
                while (ekeys.hasMoreElements())
                {
                    String ekey = (String) ekeys.nextElement();
                    Object eValues = line.get(ekey);
                    values.put(ekey, eValues);

                    id += "_" + eValues.toString();
                }

                for (ComputeParameter parameter : parameters)
                {
                    if (parameter.getDefaultValue() != null)
                    {
                        if (parameter.getDefaultValue().contains("${"))
                        {
                            if (foldingParser.isDirectlyDependOnWorksheet(parameter, table))
                            {
                                complexParametersDepend.addElement(parameter);
                            }
                            else
                            {
                                complexParametersIndepend.addElement(parameter);
                            }
                        }
                        else
                        {
                            values.put(parameter.getName(), parameter.getDefaultValue());
                            simpleValues.put(parameter.getName(), parameter.getDefaultValue());
                        }
                    }
                }

                Hashtable<String, Object> unweavedValues = new Hashtable<String, Object>();


                System.out.println("values before " + values.size());

                Vector<ComputeParameter> toRemove = new Vector<ComputeParameter>();
                //lets process dependent parameters
                for (ComputeParameter par : complexParametersDepend)
                {
                    Pair<String, Object> value = processDependentParameter(par, line, simpleValues);
                    if (foldingParser.isValueSimple(value))
                    {
                        values.put(par.getName(), value.getB());
                        toRemove.add(par);
                    }
                    else
                    {
                        unweavedValues.put(par.getName(), value.getB());
                    }
                }

                complexParametersDepend.removeAll(toRemove);
                System.out.println("values after " + values.size() + "  complex dependencies: " + complexParametersDepend.size());

                //lets process independent parameters
                for (ComputeParameter par : complexParametersIndepend)
                {
                    Pair<String, Object> value = processDependentParameter(par, line, simpleValues);
                    if (foldingParser.isValueSimple(value))
                    {
                        values.put(par.getName(), value.getB());
                        toRemove.add(par);
                    }
                    else
                    {
                        unweavedValues.put(par.getName(), value.getB());
                    }
                }

                complexParametersIndepend.removeAll(toRemove);
                System.out.println("values after " + values.size() + "  complex dependencies: " + complexParametersIndepend.size());


                //lets see that we can do now!!!
                Vector<String> vecToRemove = new Vector<String>();

                int weavingCount = 0;
                System.out.println("loop " + weavingCount + " -> " + unweavedValues.size());
                while (unweavedValues.size() > 0 && weavingCount < 10)
                {
                    Enumeration unkeys = unweavedValues.keys();
                    while (unkeys.hasMoreElements())
                    {
                        String unkey = (String) unkeys.nextElement();
                        Object eValue = unweavedValues.get(unkey);

                        Pair<String, Object> value = null;

                        if (eValue instanceof Collection<?>)
                        {
                            //System.out.println("++++++++++++++++++++++");
                            List<String> unweavedLines = (List<String>) eValue;
                            value = processUnweavedCollection(unkey, unweavedLines, values);
                        }
                        else
                        {
                            //System.out.println("------------");
                            String unweavedLine = (String) eValue;
                            // it should not happen, but still to test
                            //if parameter is still not weaved, it should contain a list
                            //still, we specify 0 as a line number
                            value = processUnweavedLine(unkey, unweavedLine, values, 0);
                        }

                        if (foldingParser.isValueSimple(value))
                        {
                            values.put(value.getA(), value.getB());
                            vecToRemove.add(unkey);
                        }
                        else
                            unweavedValues.put(value.getA(), value.getB());

                    }

                    for (String str : vecToRemove)
                        unweavedValues.remove(str);
                    weavingCount++;

                    System.out.println("loop " + weavingCount + " -> " + unweavedValues.size());

                }

                String jobListing = foldingMaker.weaveFreemarker(template, values);

                ComputeJob job = new ComputeJob();

                String jobName = config.get(JobGenerator.GENERATION_ID) + "_" +
                        workflow.getName() + "_" +
                        el.getName() + "_" + id;

                job.setName(jobName);
                job.setProtocol(protocol);
                job.setComputeScript(jobListing);
                computeJobs.add(job);

                //fill containers for grid jobs to ensure correct data transfer
                // and for cluster to generate submit script
                if (backend.equalsIgnoreCase(JobGenerator.GRID))
                {
                    GridTransferContainer container = fillContainer(protocol, values);
                    pairJobTransfers.put(job.getName(), container);
                }
                else if (backend.equalsIgnoreCase(JobGenerator.CLUSTER))
                {
                    pairWEtoCJ.put(el, job);
                    pairCJtoWE.put(job, el);
                }
                logger.log(Level.DEBUG, "----------------------------------------------------------------------");
                logger.log(Level.DEBUG, el.getName());
                logger.log(Level.DEBUG, jobListing);
                logger.log(Level.DEBUG, "----------------------------------------------------------------------");

            }


        }
        return computeJobs;
    }

    public Vector<ComputeJob> generateComputeJobsFoldedWorksheetReduce(Workflow workflow, List<Tuple> f, String backend)
    {
        //create the table with targets, which is equal to worksheet if there are no targets
        List<Hashtable> table = null;

        //remove unused parameters from the worksheet
        List<Hashtable> worksheet = foldingMaker.transformToTable(f);
        foldingMaker.setWorkflow(workflow);
        worksheet = foldingMaker.removeUnused(worksheet, (List<ComputeParameter>) workflow.getWorkflowComputeParameterCollection());

        //some supplementary hashtables
        Vector<ComputeJob> computeJobs = new Vector<ComputeJob>();

        if (backend.equalsIgnoreCase(JobGenerator.GRID))
            pairJobTransfers = new Hashtable<String, GridTransferContainer>();

        if (backend.equalsIgnoreCase(JobGenerator.CLUSTER))
        {
            pairWEtoCJ = new Hashtable<WorkflowElement, ComputeJob>();
            pairCJtoWE = new Hashtable<ComputeJob, WorkflowElement>();
            submitScript = "";
        }

        Collection<ComputeParameter> parameters = workflow.getWorkflowComputeParameterCollection();
        Collection<WorkflowElement> workflowElements = workflow.getWorkflowWorkflowElementCollection();
        for (WorkflowElement el : workflowElements)
        {

            ComputeProtocol protocol = (ComputeProtocol) el.getProtocol();
            String template = protocol.getScriptTemplate();

            //chack if we have any targets
            List<ComputeParameter> targets = foldingMaker.findTargets(protocol.getScriptTemplate());
            if (targets != null)
            {
                table = foldingMaker.fold(targets, worksheet);
            }
            else
                table = worksheet;

            //here, we prapare some information about foldered table
            //in particular, we find what parameters are Martijn's foldered constants
            foldingParser.evaluateTable(table);

            //we set all parameters to foldered parser, which are used for reducing foldered constants
            //this comment does give any clue to what is going on :)
            foldingParser.setParametersList(parameters);

            //now we start to use foldered worksheet
            //all our parameters, that depend on foldered worksheet, will become lists as well, that upset me a lot :(
            for (int i = 0; i < table.size(); i++)
            {
                //because Hashtable does not allow null keys or values used for weaving
                Hashtable<String, Object> values = new Hashtable<String, Object>();

                //parameters which are templates and do directly depend on worksheet values
                Vector<ComputeParameter> complexParametersDepend = new Vector<ComputeParameter>();

                //parameters which are templates but do not directly depend on worksheet values
                Vector<ComputeParameter> complexParametersIndepend = new Vector<ComputeParameter>();

                //hashtable with simple values, we need it for initial weaving
                Hashtable<String, String> simpleValues = new Hashtable<String, String>();

                //job naming
                String id = "id";
                Hashtable<String, Object> line = table.get(i);

                if(targets != null)
                {
                    //use targets to create name
                    Enumeration ekeys = line.keys();
                    while (ekeys.hasMoreElements())
                    {
                        String ekey = (String) ekeys.nextElement();
                        if(isTarget(ekey, targets))
                        {
                            Object eValues = line.get(ekey);
                            values.put(ekey, eValues);
                            String vvv = eValues.toString();
                            vvv = vvv.replaceAll(" ", "_");
                            id = "_" + vvv;
                        }
                    }

                }
                else
                {
                    //use the whole line to create name
                    Enumeration ekeys = line.keys();
                    while (ekeys.hasMoreElements())
                    {
                        String ekey = (String) ekeys.nextElement();
                        Object eValues = line.get(ekey);
                        values.put(ekey, eValues);
                        String vvv = eValues.toString();
                        vvv = vvv.replaceAll(" ", "_");
                        id = "_" + vvv;
                    }
                }

                for (ComputeParameter parameter : parameters)
                {
                    if (parameter.getDefaultValue() != null)
                    {
                        if (parameter.getDefaultValue().contains("${"))
                        {
                            if (foldingParser.isDirectlyDependOnWorksheet(parameter, table))
                            {
                                complexParametersDepend.addElement(parameter);
                            }
                            else
                            {
                                complexParametersIndepend.addElement(parameter);
                            }
                        }
                        else
                        {
                            values.put(parameter.getName(), parameter.getDefaultValue());
                            simpleValues.put(parameter.getName(), parameter.getDefaultValue());
                        }
                    }
                }

                Hashtable<String, Object> unweavedValues = new Hashtable<String, Object>();
                Hashtable<String, String> unweavedValuesSimple = new Hashtable<String, String>();


                //System.out.println("values before " + values.size());

                Vector<ComputeParameter> toRemove = new Vector<ComputeParameter>();
                //lets process dependent parameters
                for (ComputeParameter par : complexParametersDepend)
                {
                    Pair<String, Object> value = processDependentParameter(par, line, simpleValues);
                    if (foldingParser.isValueSimple(value))
                    {
                        values.put(par.getName(), value.getB());
                        toRemove.add(par);
                    }
                    else
                    {
                        unweavedValues.put(par.getName(), value.getB());
                    }
                }

                complexParametersDepend.removeAll(toRemove);
                //System.out.println("values after " + values.size() + "  complex dependencies: " + complexParametersDepend.size());

                //lets process independent parameters
                for (ComputeParameter par : complexParametersIndepend)
                {
                    Pair<String, Object> value = processDependentParameter(par, line, simpleValues);
                    if (foldingParser.isValueSimple(value))
                    {
                        values.put(par.getName(), value.getB());
                        toRemove.add(par);
                    }
                    else
                    {
                        unweavedValues.put(par.getName(), value.getB());
                    }
                }

                complexParametersIndepend.removeAll(toRemove);
                //System.out.println("values after " + values.size() + "  complex dependencies: " + complexParametersIndepend.size());


                //lets see that we can do now!!!
                Vector<String> vecToRemove = new Vector<String>();

                int weavingCount = 0;
                System.out.println("loop " + weavingCount + " -> " + unweavedValues.size());
                while (unweavedValues.size() > 0 && weavingCount < 10)
                {
                    Enumeration unkeys = unweavedValues.keys();
                    while (unkeys.hasMoreElements())
                    {
                        String unkey = (String) unkeys.nextElement();
                        Object eValue = unweavedValues.get(unkey);

                        Pair<String, Object> value = null;

                        if (eValue instanceof Collection<?>)
                        {
                            //System.out.println("++++++++++++++++++++++");
                            List<String> unweavedLines = (List<String>) eValue;
                            value = processUnweavedCollection(unkey, unweavedLines, values);
                        }
                        else
                        {
                            //System.out.println("------------");
                            String unweavedLine = (String) eValue;
                            // it should not happen, but still to test
                            //if parameter is still not weaved, it should contain a list
                            //still, we specify 0 as a line number
                            value = processUnweavedLine(unkey, unweavedLine, values, 0);
                        }

                        if (foldingParser.isValueSimple(value))
                        {
                            values.put(value.getA(), value.getB());
                            vecToRemove.add(unkey);
                        }
                        else
                            unweavedValues.put(value.getA(), value.getB());

                    }

                    for (String str : vecToRemove)
                        unweavedValues.remove(str);
                    weavingCount++;

                    System.out.println("loop " + weavingCount + " -> " + unweavedValues.size());

                }

                //try reduce here
                //values = reduce(values);



                String jobListing = foldingMaker.weaveFreemarker(template, values);

                ComputeJob job = new ComputeJob();

                String jobName = config.get(JobGenerator.GENERATION_ID) + "_" +
                        workflow.getName() + "_" +
                        el.getName() + "_" + id;

                job.setName(jobName);
                job.setProtocol(protocol);
                job.setComputeScript(jobListing);
                computeJobs.add(job);

                //fill containers for grid jobs to ensure correct data transfer
                // and for cluster to generate submit script
                if (backend.equalsIgnoreCase(JobGenerator.GRID))
                {
                    GridTransferContainer container = fillContainer(protocol, values);
                    pairJobTransfers.put(job.getName(), container);
                }
                else if (backend.equalsIgnoreCase(JobGenerator.CLUSTER))
                {
                    pairWEtoCJ.put(el, job);
                    pairCJtoWE.put(job, el);
                }
                logger.log(Level.DEBUG, "----------------------------------------------------------------------");
                logger.log(Level.DEBUG, el.getName());
                logger.log(Level.DEBUG, jobListing);
                logger.log(Level.DEBUG, "----------------------------------------------------------------------");

            }


        }
        return computeJobs;
    }

    private boolean isTarget(String ekey, List<ComputeParameter> targets)
    {
        for(ComputeParameter par: targets)
        {
            String name = par.getName();
            if(name.equalsIgnoreCase(ekey))
                return true;
        }
        return false;
    }


    private Pair<String, Object> processUnweavedLine(String unkey, String unweavedLine, Hashtable<String, Object> values, int i)
    {
        Pair<String, Object> pair = new Pair<String, Object>();
        Hashtable<String, String> hashtable = prepareSimpleValues(values, i);

        String value = foldingParser.doByHand(unweavedLine, hashtable);
        pair.setA(unkey);
        pair.setB(value);
        return pair;
    }

    private Hashtable<String, String> prepareSimpleValues(Hashtable<String, Object> values, int i)
    {
        Hashtable<String, String> result = new Hashtable<String, String>();
        Enumeration unkeys = values.keys();
        while (unkeys.hasMoreElements())
        {
            String unkey = (String) unkeys.nextElement();
            Object eValue = values.get(unkey);
            String vvv;
            if (eValue instanceof Collection<?>)
            {
                List<String> list = (List<String>) eValue;
                vvv = list.get(i);
            }
            else
            {
                vvv = (String) eValue;
            }

            result.put(unkey, vvv);
        }
        return result;
    }

    private Pair<String, Object> processUnweavedCollection(String unkey, List<String> unweavedLines, Hashtable<String, Object> values)
    {
        Pair<String, Object> pair = new Pair<String, Object>();
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < unweavedLines.size(); i++)
        {
            String input = unweavedLines.get(i);
            Pair<String, Object> aPair = processUnweavedLine(unkey, input, values, i);
            list.add((String) aPair.getB());
        }
        pair.setA(unkey);
        pair.setB(list);
        return pair;
    }

    //here, we also identify what parameters should be foldered
    private Pair<String, Object> processDependentParameter
            (ComputeParameter par, Hashtable<String, Object> line, Hashtable<String, String> simpleValues)
    {
        Pair<String, Object> pair = new Pair<String, Object>();
        pair.setA(par.getName());


        int lineFolderedSize = foldingParser.getFolderedLineSize(line);
        String parTemplate = par.getDefaultValue();



        foldingParser.setNotList();
        foldingParser.checkIsList(parTemplate);
        boolean isList = foldingParser.getIsList();

        if (lineFolderedSize > 1 && isList)
        {
            List<String> values = new ArrayList<String>();
            for (int i = 0; i < lineFolderedSize; i++)
            {
                String value = foldingParser.parseTemplateLineByHand(parTemplate, line, i, simpleValues);
                values.add(value);
            }
            pair.setB(values);
        }
        else
        {
            String value = foldingParser.parseTemplateOneLineByHand(parTemplate, line, simpleValues);
            pair.setB(value);

        }
        return pair;
    }

    private GridTransferContainer fillContainer(ComputeProtocol protocol, Hashtable<String, Object> values)
    {
        GridTransferContainer container = new GridTransferContainer();

        List<ComputeParameter> inputs = protocol.getInputs();
        for (ComputeParameter input : inputs)
        {
            String name = input.getName();
            String value = (String) values.get(name);
            container.addInput(name, value);
        }

        List<ComputeParameter> outputs = protocol.getOutputs();
        for (ComputeParameter output : outputs)
        {
            String name = output.getName();
            String value = (String) values.get(name);
            container.addOutput(name, value);
        }

        List<ComputeParameter> exes = protocol.getExes();
        for (ComputeParameter exe : exes)
        {
            String name = exe.getName();
            String value = (String) values.get(name);
            container.addExe(name, value);
        }

        List<ComputeParameter> logs = protocol.getLogs();
        for (ComputeParameter log : logs)
        {
            String name = log.getName();
            String value = (String) values.get(name);
            container.addLog(name, value);
        }

        return container;
    }

    public Vector<ComputeJob> generateComputeJobsDB(Workflow workflow, List<ObservationTarget> worksheet, String backend)
    {
        return null;
    }

    public boolean generateActualJobs(Vector<ComputeJob> computeJobs, String backend, Hashtable<String, String> config)
    {
        //read templates
        String templatesDir = config.get(JobGenerator.TEMPLATE_DIR);

        if (backend.equalsIgnoreCase(JobGenerator.GRID))
            readTemplatesGrid(templatesDir);
        else if (backend.equalsIgnoreCase(JobGenerator.CLUSTER))
            readTemplatesCluster(templatesDir);

        for (ComputeJob computeJob : computeJobs)
        {
            //generate files for selected back-end
            if (backend.equalsIgnoreCase(JobGenerator.GRID))
                generateActualJobGrid(computeJob, config);
            else if (backend.equalsIgnoreCase(JobGenerator.CLUSTER))
                generateActualJobCluster(computeJob, config);
        }

        //write cluster submit script
        if (backend.equalsIgnoreCase(JobGenerator.CLUSTER))
            writeToFile(config.get(JobGenerator.OUTPUT_DIR) + System.getProperty("file.separator") + "submit_" + config.get(JobGenerator.GENERATION_ID) + ".sh",
                    submitScript);


        return true;
    }

    private void generateActualJobCluster(ComputeJob computeJob, Hashtable<String, String> config)
    {
        System.out.println("name: " + computeJob.getName());

        //create values hashtable to fill templates
        Hashtable<String, String> values = new Hashtable<String, String>();

        ComputeProtocol protocol = (ComputeProtocol) computeJob.getProtocol();

        values.put(JobGenerator.JOB_ID, computeJob.getName());
        values.put(ModelLoader.FLAG_CLUSTER_QUEUE, protocol.getClusterQueue());
        values.put(ModelLoader.FLAG_CORES, protocol.getCores().toString());
        values.put(ModelLoader.FLAG_NODES, protocol.getNodes().toString());
        values.put(ModelLoader.FLAG_MEMORY, protocol.getMem());
        values.put(ModelLoader.FLAG_WALLTIME, protocol.getWalltime());

        //create actual cluster job
        String header = weaveFreemarker(templateClusterHeader, values);
        String main = computeJob.getComputeScript();
        String footer = weaveFreemarker(templateClusterFooter, values);
        String actualJob = header + main + footer;

        //write script
        (new File(config.get(JobGenerator.OUTPUT_DIR))).mkdirs();
        writeToFile(config.get(JobGenerator.OUTPUT_DIR) + System.getProperty("file.separator") + computeJob.getName() + ".sh",
                actualJob);

        //create job submission part
        WorkflowElement el = pairCJtoWE.get(computeJob);

        if (el.getPreviousSteps().size() > 0)
        {
            String dependency = JobGenerator.DEPENDENCY_HEAD;
            for (WorkflowElement wEl : el.getPreviousSteps())
            {
                ComputeJob cJ = pairWEtoCJ.get(wEl);
                dependency += ":" + cJ.getName();
                values.put(JobGenerator.JOB_DEPENDENCIES, dependency);
            }
        }
        else
            values.put(JobGenerator.JOB_DEPENDENCIES, "");

        String strSubmit = weaveFreemarker(templateClusterSubmission, values);

        submitScript += strSubmit;

    }

    private void generateActualJobGrid(ComputeJob computeJob, Hashtable<String, String> config)
    {
        //create values hashtable to fill templates
        Hashtable<String, String> values = new Hashtable<String, String>();

        values.put("script_name", computeJob.getName());
        values.put("error_log", "err_" + computeJob.getName() + ".log");
        values.put("output_log", "out_" + computeJob.getName() + ".log");
        values.put("script_location", config.get(JobGenerator.BACK_END_DIR));

        //create jdl
        String jdlListing = weaveFreemarker(templateGridJDL, values);

        //write jdl
        (new File(config.get(JobGenerator.OUTPUT_DIR))).mkdirs();
        writeToFile(config.get(JobGenerator.OUTPUT_DIR) + System.getProperty("file.separator") + computeJob.getName() + ".jdl",
                jdlListing);

        //create shell
        String shellListing = "";
        String initialScript = computeJob.getComputeScript();
        GridTransferContainer container = pairJobTransfers.get(computeJob.getName());

        //get log filename
        Hashtable<String, String> logs = container.getLogs();
        Enumeration logValues = logs.elements();
        String logName = (String) logValues.nextElement();
        String justLogName = giveJustName(logName);

        //generate downloading section (transfer inputs and executable)
        //and change job listing to execute in the grid
        Hashtable<String, String> inputs = container.getInputs();
        Enumeration actuals = inputs.elements();
        while (actuals.hasMoreElements())
        {
            Hashtable<String, String> local = new Hashtable<String, String>();

            String actualName = (String) actuals.nextElement();
            String justName = giveJustName(actualName);

            local.put(JobGenerator.LFN_NAME, actualName);
            local.put(JobGenerator.INPUT, justName);
            local.put(JobGenerator.LOG, justLogName);

            String inputListing = weaveFreemarker(templateGridDownload, local);
            initialScript = initialScript.replaceAll(actualName, justName);

            shellListing += inputListing;
        }

        Hashtable<String, String> exes = container.getExes();
        actuals = exes.elements();
        while (actuals.hasMoreElements())
        {
            Hashtable<String, String> local = new Hashtable<String, String>();

            String actualName = (String) actuals.nextElement();
            String justName = giveJustName(actualName);

            local.put(JobGenerator.LFN_NAME, actualName);
            local.put(JobGenerator.INPUT, justName);
            local.put(JobGenerator.LOG, justLogName);

            String inputListing = weaveFreemarker(templateGridDownloadExe, local);

            System.out.println("-----------");
            System.out.println(initialScript);
            System.out.println("act " + actualName);
            System.out.println("just " + justName);
            initialScript = initialScript.replaceAll(actualName, justName);
            shellListing += inputListing;
        }

        shellListing += initialScript;

        //generate uploading section
        //and change job listing to execute in the grid
        Hashtable<String, String> outputs = container.getOutputs();
        actuals = outputs.elements();
        while (actuals.hasMoreElements())
        {
            Hashtable<String, String> local = new Hashtable<String, String>();

            String actualName = (String) actuals.nextElement();
            String justName = giveJustName(actualName);

            local.put(JobGenerator.LFN_NAME, actualName);
            local.put(JobGenerator.OUTPUT, justName);
            local.put(JobGenerator.LOG, justLogName);

            String outputListing = weaveFreemarker(templateGridUpload, local);
            shellListing = shellListing.replaceAll(actualName, justName);

            shellListing += outputListing;
        }

        //add upload log
        Hashtable<String, String> local = new Hashtable<String, String>();

        local.put(JobGenerator.LFN_NAME, logName);
        local.put(JobGenerator.OUTPUT, justLogName);
        local.put(JobGenerator.LOG, justLogName);

        String outputListing = weaveFreemarker(templateGridUploadLog, local);
        shellListing += outputListing;

        //write shell
        writeToFile(config.get(JobGenerator.OUTPUT_DIR) + System.getProperty("file.separator") + computeJob.getName() + ".sh",
                shellListing);
    }

    private String giveJustName(String actualName)
    {
        int posSlash = actualName.lastIndexOf("/");
        String justName = actualName.substring(posSlash + 1);
        return justName;
    }

    private void readTemplatesCluster(String templatesDir)
    {
        try
        {
            templateClusterHeader = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateClusterHeader);
            templateClusterFooter = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateClusterFooter);
            templateClusterSubmission = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateClusterSubmission);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void readTemplatesGrid(String templatesDir)
    {
        try
        {
            templateGridDownload = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateGridDownload);
            templateGridDownloadExe = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateGridDownloadExe);
            templateGridUpload = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateGridUpload);
            templateGridUploadLog = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateGridUploadLog);
            templateGridJDL = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateGridJDL);
            templateGridAfterExecution = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateGridAfterExecution);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void setConfig(Hashtable<String, String> config)
    {
        this.config = config;
    }

    public String weaveFreemarker(String strTemplate, Hashtable<String, String> values)
    {
        Configuration cfg = new Configuration();
        //cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        Template t = null;
        StringWriter out = new StringWriter();
        try
        {
            t = new Template("name", new StringReader(strTemplate), cfg);
            t.process(values, out);
        }
        catch (TemplateException e)
        {
            //e.printStackTrace();
        }
        catch (IOException e)
        {
            //e.printStackTrace();
        }

        return out.toString();
    }

    private final String getFileAsString(String filename) throws IOException
    {
        File file = new File(filename);

        if (!file.exists())
        {
            logger.log(Level.ERROR, "template file " + filename + " does not exist");
            System.exit(1);
        }
        final BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream(file));
        final byte[] bytes = new byte[(int) file.length()];
        bis.read(bytes);
        bis.close();
        return new String(bytes);
    }

    public void writeToFile(String outfilename, String script)
    {
        try
        {
            BufferedWriter out = new BufferedWriter(new FileWriter(outfilename));
            out.write(script);
            out.close();
        }
        catch (IOException e)
        {
        }
    }

    private Hashtable<String, Object> reduce(Hashtable<String, Object> values)
    {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }


}
