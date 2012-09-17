package org.molgenis.compute.test.generator;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.molgenis.compute.commandline.FreemarkerHelper;
import org.molgenis.compute.commandline.Worksheet;
import org.molgenis.compute.design.ComputeParameter;
import org.molgenis.compute.design.Workflow;
import org.molgenis.compute.design.WorkflowElement;
import org.molgenis.compute.runtime.ComputeTask;
import org.molgenis.compute.test.temp.Target;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.util.Tuple;

import app.DatabaseFactory;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Created with IntelliJ IDEA. User: georgebyelas Date: 22/08/2012 Time: 12:05
 * To change this template use File | Settings | File Templates.
 */

public class ComputeGeneratorDBWorksheet implements ComputeGenerator
{
	// supplementary (just because it's handy to use)
	Hashtable<WorkflowElement, ComputeTask> workflowElementComputeTaskHashtable = new Hashtable<WorkflowElement, ComputeTask>();

	Database db = null;

	public void generate(Workflow workflow, List<Target> targets, Hashtable<String, String> config)
	{
	}

	/**
	 * Generate tasks and put them into the database
	 */
	public void generateWithTuple(Workflow workflow, List<Tuple> worksheet,
			Hashtable<String, String> commandLineParameters)
	{

		List<ComputeParameter> parameterList = (List<ComputeParameter>) workflow
				.getWorkflowComputeParameterCollection();
		Collection<WorkflowElement> workflowElementsList = workflow.getWorkflowWorkflowElementCollection();

		try
		{
			db = DatabaseFactory.create();
			db.beginTx();

		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}

		// I guess, we should also add line_number as a 'ComputeParameter'...
		// Actually, I think this should happen at a different place...
		ComputeParameter line_number = new ComputeParameter();
		line_number.setName("line_number");
		line_number.setDefaultValue(null);
		parameterList.add(line_number);

		// Create a Worksheet entity, just as in Compute 2
		Worksheet worksheetEntity = new Worksheet(parameterList, worksheet);

		List<ComputeTask> tasks = new ArrayList<ComputeTask>();
		for (WorkflowElement workflowElement : workflowElementsList)
		{

			// System.out.println(">> Workflow element name: " +
			// workflowElement.getName());
			// System.out.println(">> Protocol name: " +
			// workflowElement.getProtocol_Name());
			// System.out.println(">> Protocol template: " +
			// workflowElement.getProtocol().getScriptTemplate());

			List<String> iterationTargetNameList = new ArrayList<String>();
			Iterator<ComputeParameter> it = workflowElement.getProtocol().getIterateOver().iterator();
			while (it.hasNext())
			{
				iterationTargetNameList.add(it.next().getName());
			}

			// if no targets specified, then actually we mean "all targets".
			// Therefore, we add line_number as a target.
			// MD: is this best place to do this?!
			if (0 == iterationTargetNameList.size())
			{
				iterationTargetNameList.add("line_number");
			}

			List<Tuple> foldedWorksheet = Worksheet.foldWorksheet(worksheetEntity.worksheet, parameterList,
					iterationTargetNameList);

			String template = workflowElement.getProtocol().getScriptTemplate();

			for (Tuple work : foldedWorksheet)
			{
				// put ComputeParams in map
				Map<String, Object> parameters = new HashMap<String, Object>();
				for (String field : work.getFields())
				{
					parameters.put(field, work.getObject(field));
				}

				// construct taskName
				String taskName = workflowElement.getName() + "_" + parameters.get("McId") + "_"
						+ parameters.get("line_number");

				String script = createScript(template, work, taskName, workflowElementsList, parameterList);

				ComputeTask task = new ComputeTask();
				task.setName(taskName);
				task.setComputeScript(script);
				task.setInterpreter(workflowElement.getProtocol().getScriptInterpreter());
				task.setRequirements(workflowElement.getProtocol().getRequirements());
				task.setWorkflowElement(workflowElement);
				task.setStatusCode("generated");

				List<WorkflowElement> prev = workflowElement.getPreviousSteps();
				List<ComputeTask> prevTasks = new ArrayList<ComputeTask>();

				for (WorkflowElement w : prev)
				{
					ComputeTask prevTask = workflowElementComputeTaskHashtable.get(w);
					prevTasks.add(prevTask);
				}
				task.setPrevSteps(prevTasks);

				tasks.add(task);

				// because it's handy:
				workflowElementComputeTaskHashtable.put(workflowElement, task);
			}
		}

		try
		{
			// dirty hack to ensure we don't add tasks twice
			if (db.find(ComputeTask.class).size() < 0) db.add(tasks);
			db.commitTx();
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * Create a script, given a tuple from folded worksheet, taskName,
	 * workflowElementsList and ComputeParameter list
	 * 
	 * @param template
	 * @param work
	 * @param taskName
	 * @param workflowElementsList
	 * @return filledtemplate.toString();
	 */
	private String createScript(String templateScript, Tuple work, String taskName,
			Collection<WorkflowElement> workflowElementsList, List<ComputeParameter> paramList)
	{

		// put all parameters from tuple in hashmap for weaving
		Map<String, Object> parameters = new HashMap<String, Object>();
		for (String field : work.getFields())
		{
			parameters.put(field, work.getObject(field));
		}

		// add the helper
		parameters.put("freemarkerHelper", new FreemarkerHelper(paramList));
		parameters.put("parameters", work);
		parameters.put("workflowElements", workflowElementsList);

		try
		{
			Configuration cfg = new Configuration();
			// Set path so that protocols can include other protocols using the
			// "include" statement
			cfg.setDirectoryForTemplateLoading(new File(parameters.get("McProtocols").toString()));

			Template template;
			template = new Template(taskName, new StringReader(templateScript), cfg);
			StringWriter script = new StringWriter();
			template.process(parameters, script);
			return script.toString();
		}
		catch (IOException e)
		{
			System.err.println(">> ERROR >> IOException");
			e.printStackTrace();
		}
		catch (TemplateException e)
		{
			System.err.println(">> ERROR >> TemplateException");
			e.printStackTrace();
		}
		return null;
	}
}
