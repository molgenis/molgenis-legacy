package org.molgenis.generator;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.molgenis.compute.ComputeJob;
import org.molgenis.compute.ComputeParameter;
import org.molgenis.compute.ComputeProtocol;
import org.molgenis.gridhandler.CommandLineImputationGridHandler;
import org.molgenis.gridhandler.GridHandler;
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

//class with proper folding
public class Compute3JobGenerator implements JobGenerator
{

    private static Logger logger = Logger.getLogger(Compute3JobGenerator.class);

    //parsing/making folding story
    private FoldingMaker foldingMaker = new FoldingMaker();
    private FoldingParser foldingParser = new FoldingParser();

    //to reuse few methods
    ModelLoader loader = new ModelLoader();

    //grid specific imputation handler
    private GridHandler gridHandler = null;
    private boolean workflowHasDependencies = false;


    //template sources
    private String templateGridHeader;
    private String templateGridDownload;
    private String templateGridDownloadExe;
    private String templateGridUpload;
    private String templateGridJDL;
    private String templateGridAfterExecution;
    private String templateGridUploadLog;
    private String templateGridDAGNode;
    private String templateMacro;


    private String templateClusterSubmission;
    private String templateClusterHeader;
    private String templateClusterFooter;

    //template filenames
    private String fileTemplateGridHeader = "templ-grid-head.ftl";
    private String fileTemplateGridDownload = "templ-download-grid.ftl";
    private String fileTemplateGridDownloadExe = "templ-exe-grid.ftl";
    private String fileTemplateGridUpload = "templ-upload-grid.ftl";
    private String fileTemplateGridUploadLog = "templ-upload-grid-log.ftl";
    private String fileTemplateGridJDL = "templ-jdl-grid.ftl";
    private String fileTemplateGridAfterExecution = "templ-after-exe.ftl";
    private String fileTemplateGridDAGNode = "templ-jdl-dag-node.ftl";

    private String fileTemplateClusterHeader = "templ-pbs-header.ftl";
    private String fileTemplateClusterFooter = "templ-pbs-footer.ftl";
    private String fileTemplateClusterSubmission = "templ-submit.ftl";
    private String fileTemplateMacro = "templ-macro.ftl";

    //used for grid generation
    private Hashtable<String, GridTransferContainer> pairJobTransfers = null;

    //used for cluster generation - submit script
    private Hashtable<WorkflowElement, ComputeJob> pairWEtoCJ = null;
    private Hashtable<ComputeJob, WorkflowElement> pairCJtoWE = null;

    //submission script for the cluster - filled when processing jobs for cluster
    private String submitScript = null;

    //dag file for submission for the grid
    private String dagScript = null;

    //dag dependencies list - filled when processing jobs for grid
    private String dagDependencies = null;

    private Hashtable<String, String> config;

    private List<Tuple> worksheet = null;

    public void setWorksheet(List<Tuple> worksheet)
    {
        this.worksheet = worksheet;
    }


    public Vector<ComputeJob> generateComputeJobsFoldedWorksheet(Workflow workflow, List<Tuple> f, String backend)
    {
        //create the table with targets, which is equal to worksheet if there are no targets
        List<Hashtable> table = null;

        //check if workflow elements have dependencies
        workflowHasDependencies = hasDependencies(workflow);

        //remove unused parameters from the worksheet
        List<Hashtable> worksheet = foldingMaker.transformToTable(f);
        foldingMaker.setWorkflow(workflow);
        worksheet = foldingMaker.removeUnused(worksheet, (List<ComputeParameter>) workflow.getWorkflowComputeParameterCollection());

        //result jobs
        Vector<ComputeJob> computeJobs = new Vector<ComputeJob>();

        //if (backend.equalsIgnoreCase(JobGenerator.GRID))
            pairJobTransfers = new Hashtable<String, GridTransferContainer>();

        pairWEtoCJ = new Hashtable<WorkflowElement, ComputeJob>();
        pairCJtoWE = new Hashtable<ComputeJob, WorkflowElement>();

        if (backend.equalsIgnoreCase(JobGenerator.CLUSTER))
        {
            submitScript = "";
        }
        else if (backend.equalsIgnoreCase(JobGenerator.GRID) && workflowHasDependencies)
        {
            dagScript = "Type=\"dag\";\n\n";
            dagDependencies = "";
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

            //here, we prapare some information about folded table
            //in particular, we find what parameters are Martijn's folded constants
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
                Vector<ComputeParameter> complexParameters = new Vector<ComputeParameter>();

                //hashtable with simple values, we need it for initial weaving
                Hashtable<String, String> simpleValues = new Hashtable<String, String>();

                //job naming
                String id = "id";
                Hashtable<String, Object> line = table.get(i);

                if (targets != null)
                {
                    //use targets to create name
                    Enumeration ekeys = line.keys();
                    while (ekeys.hasMoreElements())
                    {
                        String ekey = (String) ekeys.nextElement();
                        if (isTarget(ekey, targets))
                        {
                            Object eValues = line.get(ekey);
                            values.put(ekey, eValues);
                            String vvv = eValues.toString();
                            vvv = vvv.replaceAll(" ", "_");
                            id += "_" + vvv;
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
                        id += "_" + vvv;
                    }
                }

                for (ComputeParameter parameter : parameters)
                {
                    if (parameter.getDefaultValue() != null)
                    {
                        if (parameter.getDefaultValue().contains("${"))
                        {
                            complexParameters.addElement(parameter);
                        }
                        else
                        {
                            values.put(parameter.getName(), parameter.getDefaultValue());
                            simpleValues.put(parameter.getName(), parameter.getDefaultValue());
                        }
                    }
                }
                logger.log(Level.DEBUG, "simple parameters before: " + values.size());

                //we transform complex parameters to unweaved values, because unweaved value can folded
                Hashtable<String, Object> unweavedValues = new Hashtable<String, Object>();

                for (ComputeParameter par : complexParameters)
                {
                    Pair<String, Object> value = processDependentParameter(par, line, simpleValues);
                    if (foldingParser.isValueSimple(value))
                    {
                        values.put(par.getName(), value.getB());
                    }
                    else
                    {
                        unweavedValues.put(par.getName(), value.getB());
                    }
                }

                logger.log(Level.DEBUG, "simple parameters after " + values.size() + "  parameters to weave: " + complexParameters.size());

                Vector<String> vecToRemove = new Vector<String>();

                int weavingCount = 0;
                logger.log(Level.DEBUG, "loop " + weavingCount + " -> " + unweavedValues.size());
                //in a loop weave complex parameters with values
                while (unweavedValues.size() > 0 && weavingCount < 100)
                {
                    Enumeration unkeys = unweavedValues.keys();
                    while (unkeys.hasMoreElements())
                    {
                        String unkey = (String) unkeys.nextElement();
                        Object eValue = unweavedValues.get(unkey);

                        Pair<String, Object> value = null;

                        if (eValue instanceof Collection<?>)
                        {
                            List<String> unweavedLines = (List<String>) eValue;
                            value = processUnweavedCollection(unkey, unweavedLines, values);
                        }
                        else
                        {
                            String unweavedLine = (String) eValue;
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

                    logger.log(Level.DEBUG, "loop/parameters to weave " + weavingCount + " -> " + unweavedValues.size());

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
                //if (backend.equalsIgnoreCase(JobGenerator.GRID))
                {
                    GridTransferContainer container = fillContainer(protocol, values);
                    pairJobTransfers.put(job.getName(), container);
                }
                //else if (backend.equalsIgnoreCase(JobGenerator.CLUSTER))
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

    private boolean hasDependencies(Workflow workflow)
    {
        Collection<WorkflowElement> workflowElements = workflow.getWorkflowWorkflowElementCollection();
        for (WorkflowElement el : workflowElements)
        {
            if (el.getPreviousSteps().size() > 0)
                return true;
        }
        return false;
    }

    private boolean isTarget(String ekey, List<ComputeParameter> targets)
    {
        for (ComputeParameter par : targets)
        {
            String name = par.getName();
            if (name.equalsIgnoreCase(ekey))
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
            String value = foldingParser.parseTemplateLineByHand(parTemplate, line, 0, simpleValues);
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

    public boolean generateActualJobsWithMacros(Vector<ComputeJob> computeJobs, String backend, Hashtable<String, String> config)
    {
        //read templates
        String templatesDir = config.get(JobGenerator.TEMPLATE_DIR);
        readTemplates(templatesDir);


        for (ComputeJob computeJob : computeJobs)
        {

            GridTransferContainer container = pairJobTransfers.get(computeJob.getName());

            String strMacrosInput = makeMacros(container, computeJob.getComputeScript(), "input", backend);
            String strMacrosOutput = makeMacros(container, computeJob.getComputeScript(), "output", backend);


            System.out.println(">>> generation job: " + computeJob.getName());
            if (backend.equalsIgnoreCase(JobGenerator.CLUSTER))
            {
                generateActualJobCluster(computeJob, config, strMacrosInput, strMacrosOutput);
            }
            else if (backend.equalsIgnoreCase(JobGenerator.GRID))
            {
                generateActualJobGridMacro(computeJob, config, strMacrosInput, strMacrosOutput);
            }
        }

        //write cluster submit script
        if (backend.equalsIgnoreCase(JobGenerator.CLUSTER))
            writeToFile(config.get(JobGenerator.OUTPUT_DIR) + System.getProperty("file.separator") + "submit_" + config.get(JobGenerator.GENERATION_ID) + ".sh",
                    submitScript);

        return true;
    }

    //here we create macros listing
    private String makeMacros(GridTransferContainer container, String script, String type, String backend)
    {
        String text = "";
        if (type.equalsIgnoreCase("input"))
        {
            text += JobGenerator.SOURCE_SCRIPT + "\n";

            Vector<String> nameInputs = loader.findFlagValues(script, ModelLoader.FLAG_INPUTS);
            Hashtable<String, String> inputs = container.getInputs();

            for (String s : nameInputs)
            {
                String macroline = makeMacroLine(s, inputs, type, backend);
                text += macroline;

            }

            Vector<String> nameExes = loader.findFlagValues(script, ModelLoader.FLAG_EXES);
            Hashtable<String, String> exes = container.getExes();

            for (String s : nameExes)
            {
                String macroline = makeMacroLine(s, exes, JobGenerator.EXE, backend);
                text += macroline;
            }

        }
        else if (type.equalsIgnoreCase("output"))
        {
            text += "\n";

            Vector<String> nameOutputs = loader.findFlagValues(script, ModelLoader.FLAG_OUTPUTS);
            Hashtable<String, String> outputs = container.getOutputs();
            for (String s : nameOutputs)
            {
                String macroline = makeMacroLine(s, outputs, type, backend);
                text += macroline;
            }
        }
        return text;
    }

    private String makeMacroLine(String s, Hashtable<String, String> inputs, String type, String backend)
    {
        String macro = null;
        if (s.contains(".*"))
        {
            String prefix = s.substring(0, s.lastIndexOf("."));
            Enumeration keys = inputs.keys();
            boolean isPathSet = false;
            Hashtable<String, String> weaveValues = new Hashtable<String, String>();
            String extensions = "";
            while (keys.hasMoreElements())
            {
                String name = (String) keys.nextElement();

                if (name.contains(prefix))
                {
                    int prefixIndex = name.indexOf(prefix);
                    if (prefixIndex == 0)
                    {
                        String value = inputs.get(name);
                        //here we chack is it the path or this variable here by mistake of naming conventions
                        if (value.lastIndexOf("/") > -1)
                        {
                            //to set path and other variables only once
                            if (!isPathSet)
                            {
                                weaveValues.put(JobGenerator.MACRO_BACKEND, backend);
                                weaveValues.put(JobGenerator.MACRO_TYPE, type);

                                int lastSlash = value.lastIndexOf("/");
                                String path = value.substring(0, lastSlash + 1);
                                weaveValues.put(JobGenerator.MACRO_PATH, path);

                                String actualName = value.substring(lastSlash + 1, value.length());

                                int firstDot = actualName.indexOf(".");
                                String vvv = actualName.substring(0, firstDot);
                                weaveValues.put(JobGenerator.MACRO_NAME, vvv);

                                String extension = actualName.substring(firstDot, actualName.length());
                                extensions += " " + "\"" + extension + "\"";

                                isPathSet = true;
                            }
                            else
                            {
                                int lastSlash = value.lastIndexOf("/");
                                String actualName = value.substring(lastSlash + 1, value.length());
                                int firstDot = actualName.indexOf(".");

                                String extension = actualName.substring(firstDot, actualName.length());

                                extensions += " " + "\"" + extension + "\"";


                            }
                        }
                    }
                }
            }
            weaveValues.put(JobGenerator.MACRO_EXTENSIONS, extensions);
            macro = weaveFreemarker(templateMacro, weaveValues);
            int z = 0;
        }
        else
        {

            Enumeration keys = inputs.keys();
            while (keys.hasMoreElements())
            {
                String name = (String) keys.nextElement();
                if (s.equalsIgnoreCase(name))
                {
                    String value = inputs.get(name);

                    Hashtable<String, String> weaveValues = new Hashtable<String, String>();
                    weaveValues.put(JobGenerator.MACRO_BACKEND, backend);
                    weaveValues.put(JobGenerator.MACRO_TYPE, type);

                    int lastSlash = value.lastIndexOf("/");
                    String path = value.substring(0, lastSlash + 1);
                    weaveValues.put(JobGenerator.MACRO_PATH, path);

                    String actualName = value.substring(lastSlash + 1, value.length());

                    int firstDot = actualName.indexOf(".");
                    if (firstDot < 0)
                    {
                        String vvv = actualName.substring(0, actualName.length());
                        weaveValues.put(JobGenerator.MACRO_NAME, vvv);
                        weaveValues.put(JobGenerator.MACRO_EXTENSIONS, "");
                    }
                    else
                    {
                        String vvv = actualName.substring(0, firstDot);
                        weaveValues.put(JobGenerator.MACRO_NAME, vvv);
                        String extension = actualName.substring(firstDot, actualName.length());
                        extension = "\"" + extension + "\"";
                        weaveValues.put(JobGenerator.MACRO_EXTENSIONS, extension);
                    }
                    macro = weaveFreemarker(templateMacro, weaveValues);
                }

            }

        }
        return macro;  //To change body of created methods use File | Settings | File Templates.
    }

    private void generateActualJobGridMacro(ComputeJob computeJob, Hashtable<String, String> config, String strMacrosInput, String strMacrosOutput)
    {
        Hashtable<String, String> values = new Hashtable<String, String>();

        values.put("script_name", computeJob.getName());
        values.put("error_log", "err_" + computeJob.getName() + ".log");
        values.put("output_log", "out_" + computeJob.getName() + ".log");
        values.put("script_location", config.get(JobGenerator.BACK_END_DIR));
        values.put("node_name", computeJob.getName());
        //create jdl
        String jdlListing = weaveFreemarker(templateGridJDL, values);

        //write jdl
        (new File(config.get(JobGenerator.OUTPUT_DIR))).mkdirs();
        writeToFile(config.get(JobGenerator.OUTPUT_DIR) + System.getProperty("file.separator") + computeJob.getName() + ".jdl",
                jdlListing);

        //create shell
        String shellListing = templateGridHeader;
        String initialScript = computeJob.getComputeScript();
        GridTransferContainer container = pairJobTransfers.get(computeJob.getName());

        //generate downloading section (transfer inputs and executable)
        //and change job listing to execute in the grid
        Hashtable<String, String> inputs = container.getInputs();
        Enumeration actuals = inputs.elements();
        while (actuals.hasMoreElements())
        {
            String actualName = (String) actuals.nextElement();
            String justName = giveJustName(actualName);

            //escapes are required to avoid $ sign processing in String replaceAll method
            justName = "\\" + justName;

            //commented out - but I keep it if I need to sho somebody the problem of replacement with wild cards
//            System.out.println("actual " + actualName);
//            System.out.println("just " + justName);
//
//            System.out.println("------------- before");
//            System.out.println(initialScript);

            initialScript = initialScript.replaceAll(actualName, justName);

//            System.out.println("------------- after");
//            System.out.println(initialScript);

        }

        Hashtable<String, String> exes = container.getExes();
        actuals = exes.elements();
        while (actuals.hasMoreElements())
        {
            String actualName = (String) actuals.nextElement();
            String justName = giveJustName(actualName);

            justName = "\\" + justName;
            initialScript = initialScript.replaceAll(actualName, justName);
        }
        shellListing += strMacrosInput;
        shellListing += initialScript;
        String outputsString = "";

        Hashtable<String, String> outputs = container.getOutputs();
        actuals = outputs.elements();
        while (actuals.hasMoreElements())
        {
            String actualName = (String) actuals.nextElement();
            String justName = giveJustName(actualName);

            justName = "\\" + justName;
            shellListing = shellListing.replaceAll(actualName, justName);

        }

        shellListing += strMacrosOutput;

        //write shell
        writeToFile(config.get(JobGenerator.OUTPUT_DIR) + System.getProperty("file.separator") + computeJob.getName() + ".sh",
                shellListing);

    }


    public boolean generateActualJobs(Vector<ComputeJob> computeJobs, String backend, Hashtable<String, String> config)
    {
        int generationCount = -1;
        //read templates
        String templatesDir = config.get(JobGenerator.TEMPLATE_DIR);

        if (backend.equalsIgnoreCase(JobGenerator.GRID))
        {
            readTemplatesGrid(templatesDir);
            gridHandler = new CommandLineImputationGridHandler();
            gridHandler.setWorksheet(worksheet);
            generationCount = gridHandler.getNextJobID();

            //adding dag dependencies
            if (workflowHasDependencies)
            {
                dagScript += "nodes = [\n";
            }
        }
        else if (backend.equalsIgnoreCase(JobGenerator.CLUSTER))
            readTemplatesCluster(templatesDir);

        for (ComputeJob computeJob : computeJobs)
        {
            System.out.println(">>> generation job: " + computeJob.getName());

            //generate files for selected back-end
            if (backend.equalsIgnoreCase(JobGenerator.GRID))
                generateActualJobGrid(computeJob, config, generationCount);
            else if (backend.equalsIgnoreCase(JobGenerator.CLUSTER))
                generateActualJobCluster(computeJob, config);

            generationCount++;
        }

        //write cluster submit script
        if (backend.equalsIgnoreCase(JobGenerator.CLUSTER))
            writeToFile(config.get(JobGenerator.OUTPUT_DIR) + System.getProperty("file.separator") + "submit_" + config.get(JobGenerator.GENERATION_ID) + ".sh",
                    submitScript);
        else if (backend.equalsIgnoreCase(JobGenerator.GRID) && workflowHasDependencies)
        {
            //produce targetsListFile
            gridHandler.writeJobsLogsToFile(config);

            //finilize dag
            dagScript += "\n];";
            // cut last coma and new line
            dagDependencies = dagDependencies.substring(0, dagDependencies.length() - 2);
            dagDependencies = "\ndependencies = {\n" + dagDependencies + "\n}";
            dagScript += dagDependencies;

            //write dag file
            writeToFile(config.get(JobGenerator.OUTPUT_DIR) + System.getProperty("file.separator") +
                    "dag_" + config.get(JobGenerator.GENERATION_ID) + ".jdl",
                    dagScript);
        }

        return true;
    }

    private void generateActualJobCluster(ComputeJob computeJob, Hashtable<String, String> config)
    {

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

    private void generateActualJobCluster(ComputeJob computeJob, Hashtable<String, String> config, String macrosInput, String macrosOutput)
    {

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
        String actualJob = header + macrosInput + main + macrosOutput + footer;

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

    //todo: method contains few constants that should be moved to interface
    private void generateActualJobGrid(ComputeJob computeJob, Hashtable<String, String> config, int generationCount)
    {
        //create values hashtable to fill templates
        Hashtable<String, String> values = new Hashtable<String, String>();

        values.put("script_name", computeJob.getName());
        values.put("error_log", "err_" + computeJob.getName() + ".log");
        values.put("output_log", "out_" + computeJob.getName() + ".log");
        values.put("script_location", config.get(JobGenerator.BACK_END_DIR));
        values.put("node_name", computeJob.getName());
        //create jdl
        String jdlListing = weaveFreemarker(templateGridJDL, values);

        if (workflowHasDependencies)
        {
            String dagNodeListing = weaveFreemarker(templateGridDAGNode, values);
            dagScript += dagNodeListing + "\n";
        }

        //write jdl
        (new File(config.get(JobGenerator.OUTPUT_DIR))).mkdirs();
        writeToFile(config.get(JobGenerator.OUTPUT_DIR) + System.getProperty("file.separator") + computeJob.getName() + ".jdl",
                jdlListing);

        //create shell
        String shellListing = templateGridHeader;
        String initialScript = computeJob.getComputeScript();
        GridTransferContainer container = pairJobTransfers.get(computeJob.getName());

        //get log filename
        Hashtable<String, String> logs = container.getLogs();
        Enumeration logValues = logs.elements();
        String logName = (String) logValues.nextElement();
        String justLogName = giveJustName(logName);

        //set log name to computeJob, that will be used in grid handler
        computeJob.setLogFile("lfn://grid/" + logName);

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
            //escapes are required to avoid $ sign processing in String replaceAll method
            justName = "\\" + justName;
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

            logger.log(Level.DEBUG, "-----------");
            logger.log(Level.DEBUG, initialScript);
            logger.log(Level.DEBUG, "act " + actualName);
            logger.log(Level.DEBUG, "just " + justName);
            justName = "\\" + justName;
            initialScript = initialScript.replaceAll(actualName, justName);
            shellListing += inputListing;
        }

        shellListing += initialScript;

        //generate uploading section
        //and change job listing to execute in the grid

        String outputsString = "";

        Hashtable<String, String> outputs = container.getOutputs();
        actuals = outputs.elements();
        while (actuals.hasMoreElements())
        {
            Hashtable<String, String> local = new Hashtable<String, String>();

            String actualName = (String) actuals.nextElement();
            String justName = giveJustName(actualName);

            //set output file to compute job, that will be used in grid handler
            computeJob.setOutputFile("lfn://grid/" + actualName);

            local.put(JobGenerator.LFN_NAME, actualName);
            local.put(JobGenerator.OUTPUT, justName);
            local.put(JobGenerator.LOG, justLogName);

            String outputListing = weaveFreemarker(templateGridUpload, local);
            justName = "\\" + justName;
            shellListing = shellListing.replaceAll(actualName, justName);

            //shellListing += outputListing;
            outputsString += outputListing;
        }

        shellListing += outputsString;

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

        //and computeJob to grid handler
        gridHandler.setComputeJob(computeJob);

        //write job dependencies to DAG
        WorkflowElement el = pairCJtoWE.get(computeJob);
        if (el.getPreviousSteps().size() > 0)
        {
            String jobName = computeJob.getName();
            Vector<String> dependencyNames = new Vector<String>();
            for (WorkflowElement wEl : el.getPreviousSteps())
            {
                ComputeJob cJ = pairWEtoCJ.get(wEl);
                String strDependencyName = cJ.getName();
                dependencyNames.addElement(strDependencyName);
            }

            String strDependency = "";

            //more than one previous element
            if (dependencyNames.size() > 1)
            {
                for (String str : dependencyNames)
                {
                    strDependency += str + ", ";
                }
                //cut last coma
                strDependency = strDependency.substring(0, strDependency.length() - 2);
                strDependency = "{ " + strDependency + " }";
            }
            //only one dependency element
            else
            {
                strDependency = dependencyNames.elementAt(0);
            }

            // format string and add to DAg
            String toAdd = "{ " + jobName + ", " + strDependency + " },\n";
            dagDependencies = toAdd + dagDependencies;
        }

    }

    private String giveJustName(String actualName)
    {
        int posSlash = actualName.lastIndexOf("/");
        String justName = actualName.substring(posSlash + 1);
        return JobGenerator.GRID_TEMP_DIR + System.getProperty("file.separator") + justName;
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
            templateGridHeader = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateGridHeader);
            templateGridDownload = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateGridDownload);
            templateGridDownloadExe = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateGridDownloadExe);
            templateGridUpload = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateGridUpload);
            templateGridUploadLog = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateGridUploadLog);
            templateGridJDL = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateGridJDL);
            templateGridAfterExecution = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateGridAfterExecution);
            templateGridDAGNode = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateGridDAGNode);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void readTemplates(String templatesDir)
    {
        try
        {
            templateClusterHeader = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateClusterHeader);
            templateClusterFooter = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateClusterFooter);
            templateClusterSubmission = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateClusterSubmission);
            templateGridHeader = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateGridHeader);
            templateGridDownload = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateGridDownload);
            templateGridDownloadExe = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateGridDownloadExe);
            templateGridUpload = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateGridUpload);
            templateGridUploadLog = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateGridUploadLog);
            templateGridJDL = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateGridJDL);
            templateGridAfterExecution = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateGridAfterExecution);
            templateMacro = getFileAsString(templatesDir + System.getProperty("file.separator") + fileTemplateMacro);

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


}
