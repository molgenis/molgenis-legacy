package org.molgenis.compute.test;

import java.io.File;
import java.util.Hashtable;
import java.util.List;

import org.molgenis.compute.commandline.WorksheetHelper;
import org.molgenis.compute.design.Workflow;
import org.molgenis.compute.test.executor.ComputeExecutor;
import org.molgenis.compute.test.executor.ComputeExecutorPilotDB;
import org.molgenis.compute.test.generator.ComputeGenerator;
import org.molgenis.compute.test.generator.ComputeGeneratorDB;
import org.molgenis.compute.test.generator.ComputeGeneratorDBWorksheet;
import org.molgenis.compute.test.reader.WorkflowReader;
import org.molgenis.compute.test.reader.WorkflowReaderDBJPA;
import org.molgenis.util.Tuple;

public class NGSDemo {
	public static void main(String[] args) {
		// Loading data

		WorkflowReader reader = new WorkflowReaderDBJPA();

		// read a workflow
		Workflow workflow = reader
				.getWorkflow("in-house_workflow_realignmentAndSnpCalling.csv");

		// load targets from a worksheet
		List<Tuple> targetList = null;
		try {
			targetList = (new WorksheetHelper())
					.readTuplesFromFile(new File(
							"/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/workflows/in-house_worksheet_test.csv"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		// create user parameters (Mc values)
		Hashtable<String, String> userParameters = new Hashtable<String, String>();
		userParameters.put(ComputeGeneratorDB.BACKEND,
				ComputeGeneratorDB.BACKEND_GRID);
		userParameters.put(ComputeGeneratorDB.RUN_ID, "test1");
		// todo add mc parameters!

		// generate ComputeTasks
		ComputeGenerator generator = new ComputeGeneratorDBWorksheet();
		generator.generateWithTuple(workflow, targetList, userParameters);

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
}
