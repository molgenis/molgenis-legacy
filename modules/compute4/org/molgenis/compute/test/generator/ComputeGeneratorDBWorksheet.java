package org.molgenis.compute.test.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.molgenis.compute.commandline.Worksheet;
import org.molgenis.compute.design.ComputeParameter;
import org.molgenis.compute.design.Workflow;
import org.molgenis.compute.design.WorkflowElement;
import org.molgenis.compute.runtime.ComputeTask;
import org.molgenis.compute.test.temp.Target;
import org.molgenis.compute.test.util.TemplateWeaver;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.util.Tuple;

import app.DatabaseFactory;

/**
 * Created with IntelliJ IDEA. User: georgebyelas Date: 22/08/2012 Time: 12:05
 * To change this template use File | Settings | File Templates.
 */

public class ComputeGeneratorDBWorksheet implements ComputeGenerator {
	public static final String RUN_ID = "run_id";

	// supplementary (just because it's handy to use)
	Hashtable<WorkflowElement, ComputeTask> workflowElementComputeTaskHashtable = new Hashtable<WorkflowElement, ComputeTask>();

	private TemplateWeaver weaver = new TemplateWeaver();
	private FoldingMaster foldingMaster = new RealFoldingMaster();

	private Hashtable<String, String> userParameters = null;
	private String backend = "grid";

	Database db = null;

	public void generate(Workflow workflow, List<Target> targets, Hashtable<String, String> config) {
	}

	/**
	 * Generate tasks and put them into the database
	 */
	public void generateWithTuple(Workflow workflow, List<Tuple> worksheet, Hashtable<String, String> userParametersInput) {
		this.userParameters = userParametersInput;

		List<ComputeParameter> parameterList = (List<ComputeParameter>) workflow.getWorkflowComputeParameterCollection();
		Collection<WorkflowElement> workflowElementsList = workflow.getWorkflowWorkflowElementCollection();

		try {
			db = DatabaseFactory.create();
			db.beginTx();

		} catch (DatabaseException e) {
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
		for (WorkflowElement workflowElement : workflowElementsList) {

			System.out.println(">> Workflow element name: " + workflowElement.getName());
			System.out.println(">> Protocol name: " + workflowElement.getProtocol_Name());
			System.out.println(">> Protocol template: " + workflowElement.getProtocol().getScriptTemplate());

			List<String> iterationTargetNameList = new ArrayList<String>();
			Iterator<ComputeParameter> it = workflowElement.getProtocol().getIterateOver().iterator();
			while (it.hasNext()) {
				iterationTargetNameList.add(it.next().getName());
			}

			// if no targets specified, then actually we mean "all targets".
			// Therefore, we add line_number as a target.
			if (0 == iterationTargetNameList.size()) {
				iterationTargetNameList.add("line_number");
			}

			List<Tuple> foldedWorksheet = Worksheet.foldWorksheet(worksheetEntity.worksheet, parameterList, iterationTargetNameList);

			// foldingMaster.createTuples(
			// computeParameterList, worksheet, userParameters);

			String template = workflowElement.getProtocol().getScriptTemplate();

			String result = null;
			if (userParameters.get(ComputeGeneratorDB.BACKEND).equals(ComputeGeneratorDB.BACKEND_GRID)) {
				result = null;// weaver.weaveFreemarker(template,
								// values);
			} else if (userParameters.get(ComputeGeneratorDB.BACKEND).equals(ComputeGeneratorDB.BACKEND_PBS)) {
				// String result = weaver.weaveFreemarker(template,
				// foldedWorksheet);
			} else {
				System.err.println("Backend should be: backend_grid or backend_pbs");
				System.exit(1);
			}

			ComputeTask task = new ComputeTask();
			String taskName = workflowElement.getName() + "_" + userParameters.get(RUN_ID);
			task.setName(taskName);
			task.setComputeScript(result);
			task.setInterpreter(workflowElement.getProtocol().getScriptInterpreter());
			task.setRequirements(workflowElement.getProtocol().getRequirements());
			task.setWorkflowElement(workflowElement);

			List<WorkflowElement> prev = workflowElement.getPreviousSteps();
			List<ComputeTask> prevTasks = new ArrayList<ComputeTask>();

			for (WorkflowElement w : prev) {
				ComputeTask prevTask = workflowElementComputeTaskHashtable.get(w);
				prevTasks.add(prevTask);
			}
			task.setPrevSteps(prevTasks);

			tasks.add(task);

			// because it's handy:
			workflowElementComputeTaskHashtable.put(workflowElement, task);
		}

		try {
			db.add(tasks);
			db.commitTx();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}

	}

}
