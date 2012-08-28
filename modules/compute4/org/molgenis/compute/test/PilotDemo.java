package org.molgenis.compute.test;

import org.molgenis.compute.design.Workflow;
import org.molgenis.compute.test.executor.ComputeExecutor;
import org.molgenis.compute.test.executor.ComputeExecutorPilotDB;
import org.molgenis.compute.test.generator.ComputeGenerator;
import org.molgenis.compute.test.generator.ComputeGeneratorDB;
import org.molgenis.compute.test.reader.WorkflowReader;
import org.molgenis.compute.test.reader.WorkflowReaderDBJPA;
import org.molgenis.compute.test.temp.Target;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class PilotDemo
{
    public static void main(String[] args)
    {
        WorkflowReader reader = new WorkflowReaderDBJPA();
        //WorkflowReader reader = new WorkflowReaderDBJDBC();

        //read test workflow
        Workflow workflow = reader.getWorkflow("TestWorkflow");

        //read test targets
        Target target = new Target();
        List<Target> targetList = new ArrayList<Target>();
        targetList.add(target);

        //create user values
        Hashtable<String, String> userValues = new Hashtable<String, String>();
        userValues.put(ComputeGeneratorDB.RUN_ID, "test1");
        //todo add mc parameters!

        //generate ComputeTasks
        ComputeGenerator generator = new ComputeGeneratorDB();
        generator.generate(workflow, targetList, userValues);

        System.out.println("... generated");

        System.out.println("execute with pilots on the grid");
        //execute generated tasks with pilots
        ComputeExecutor executor = new ComputeExecutorPilotDB();
        executor.startHost("lsgrid");

        while(true)
        {
            executor.executeTasks();
            try
            {
                Thread.sleep(20000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
