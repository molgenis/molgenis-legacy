package org.molgenis.compute.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.velocity.util.StringUtils;
import org.molgenis.compute.commandline.WorksheetHelper;
import org.molgenis.compute.commandline.options.Options;
import org.molgenis.compute.design.ComputeParameter;
import org.molgenis.compute.design.Workflow;
import org.molgenis.compute.test.executor.ComputeExecutor;
import org.molgenis.compute.test.executor.ComputeExecutorPilotDB;
import org.molgenis.compute.test.generator.ComputeGenerator;
import org.molgenis.compute.test.generator.ComputeGeneratorDBWorksheet;
import org.molgenis.compute.test.reader.WorkflowReader;
import org.molgenis.compute.test.reader.WorkflowReaderDBJPA;
import org.molgenis.util.Tuple;

public class NGSDemo {

	public static void main(String[] args) {
		System.out.println(">> Start..");
		// Loading workflow with JPA
		WorkflowReader reader = new WorkflowReaderDBJPA();

		// read a workflow
		Workflow workflow = reader.getWorkflow("in-house_workflow_realignmentAndSnpCalling.csv");

		// Get command line parameters and add them to workflow
		addCommandLineParameters(args, workflow);

		// get worksheet from commmand line parameters
		String worksheetFile = null;
		for (ComputeParameter cp : workflow.getWorkflowComputeParameterCollection()) {
			if (cp.getName().equalsIgnoreCase("McWorksheet"))
				worksheetFile = cp.getDefaultValue();
		}

		// load targets from a worksheet
		List<Tuple> worksheet = null;
		try {
			worksheet = (new WorksheetHelper()).readTuplesFromFile(new File(worksheetFile));
			// "/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/workflows/in-house_worksheet.csv"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		// generate ComputeTasks
		ComputeGenerator generator = new ComputeGeneratorDBWorksheet();
		generator.generateWithTuple(workflow, worksheet, null);

		System.out.println("... generated");

		System.out.println("execute with pilots on the grid");
		// execute generated tasks with pilots
		ComputeExecutor executor = new ComputeExecutorPilotDB();
		executor.startHost("lsgrid");

		while (true) {
			executor.executeTasks();
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static void addCommandLineParameters(String[] args, Workflow workflow) {
		Options opt = new Options(args, Options.Prefix.DASH, Options.Multiplicity.ONCE, 0);

		List<String> params = new ArrayList<String>(Arrays.asList("parameters", "workflow", "worksheet", "protocols", "templates", "scripts", "id", "dir", "backend"));
		Iterator<String> it = params.iterator();
		while (it.hasNext()) {
			opt.getSet().addOption(it.next(), false, Options.Separator.EQUALS);
		}

		boolean isCorrect = opt.check(opt.getSet().getSetName(), false, false);
		if (!isCorrect) {
			System.out.println(opt.getCheckErrors());

			System.out.println("command line format:\n" + "-worksheet=<InputWorksheet.csv>\n" + "-parameters=<InputParameters.csv>\n" + "-workflow=<InputWorkflow.csv>\n"
					+ "-protocols=<InputProtocolsDir>\n" + "-templates=<InputTemplatesDir>\n" + "-scripts=<OutputScriptsDir>\n" + "-id=<ScriptGenerationID>\n" + "-dir=<McDir>\n"
					+ "-backend=<cluster|grid>");
			System.exit(1);
		}

		// now add each command line parameter as ComputeParameter to workflow

		it = params.iterator();
		while (it.hasNext()) {
			String name = it.next();
			workflow.getWorkflowComputeParameterCollection().add(createComputeParameter("Mc" + StringUtils.firstLetterCaps(name), opt.getSet().getOption(name).getResultValue(0)));
		}
	}

	private static ComputeParameter createComputeParameter(String name, String value) {
		ComputeParameter cp = new ComputeParameter();
		cp.setName(name);
		cp.setDefaultValue(value);
		return cp;
	}
}
