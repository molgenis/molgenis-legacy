package org.molgenis.compute.test.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

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

public class ComputeGeneratorDB implements ComputeGenerator {
	public static final String RUN_ID = "run_id";
	public static final String BACKEND = "backend";
	public static final String BACKEND_GRID = "backend_grid";
	public static final String BACKEND_PBS = "backend_pbs";

	// supplementary
	Hashtable<WorkflowElement, ComputeTask> workflowElementComputeTaskHashtable = new Hashtable<WorkflowElement, ComputeTask>();

	private TemplateWeaver weaver = new TemplateWeaver();
	private FoldingMaster foldingMaster = new FakeFoldingMaster();

	private Hashtable<String, String> userValues = null;
	private String backend = "grid";

	Database db = null;

	public void generate(Workflow workflow, List<Target> targets,
			Hashtable<String, String> config) {
		this.userValues = config;

		Collection<ComputeParameter> listParameters = workflow
				.getWorkflowComputeParameterCollection();
		Collection<WorkflowElement> listWorkflowElements = workflow
				.getWorkflowWorkflowElementCollection();

		try {
			db = DatabaseFactory.create();
			db.beginTx();

		} catch (DatabaseException e) {
			e.printStackTrace();
		}

		List<ComputeTask> tasks = new ArrayList<ComputeTask>();
		for (WorkflowElement workflowElement : listWorkflowElements) {
			// most probably values generation
			Hashtable<String, String> values = foldingMaster.createValues(
					listParameters, targets, userValues);

			String template = workflowElement.getProtocol().getScriptTemplate();
			String result = weaver.weaveFreemarker(template, values);

			ComputeTask task = new ComputeTask();
			String taskName = workflowElement.getName() + "_"
					+ userValues.get(RUN_ID);
			task.setName(taskName);
			task.setComputeScript(result);
			task.setInterpreter(workflowElement.getProtocol()
					.getScriptInterpreter());
			task.setRequirements(workflowElement.getProtocol()
					.getRequirements());
			task.setWorkflowElement(workflowElement);

			List<WorkflowElement> prev = workflowElement.getPreviousSteps();
			List<ComputeTask> prevTasks = new ArrayList<ComputeTask>();

			for (WorkflowElement w : prev) {
				ComputeTask prevTask = workflowElementComputeTaskHashtable
						.get(w);
				prevTasks.add(prevTask);
			}
			task.setPrevSteps(prevTasks);

			tasks.add(task);
			workflowElementComputeTaskHashtable.put(workflowElement, task);
		}

		try {
			db.add(tasks);
			db.commitTx();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	public void generateWithTuple(Workflow workflow, List<Tuple> targets,
			Hashtable<String, String> config) {

	}

}
