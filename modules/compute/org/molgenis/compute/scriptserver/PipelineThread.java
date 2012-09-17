package org.molgenis.compute.scriptserver;

import org.molgenis.compute.monitor.LoggingReader;
import org.molgenis.compute.pipelinemodel.Pipeline;
import org.molgenis.compute.pipelinemodel.Script;
import org.molgenis.compute.pipelinemodel.Step;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 22/09/2011
 * Time: 11:11
 * To change this template use File | Settings | File Templates.
 */
public abstract class PipelineThread implements Runnable
{
    protected ExecutorService exec = null;
    protected Pipeline pipeline = null;
    protected LoggingReader monitor = null;

    public void run()
    {

        int numberOfSteps = pipeline.getNumberOfSteps();

        System.out.println(">>> start pipeline " + pipeline.getId());

        for (int i = 0; i < numberOfSteps; i++)
        {
            Step step = pipeline.getStep(i);
            step.setActive(true);

            System.out.println(">>> start step " + pipeline.getId() + " - " + step.getId());

            for (int j = 0; j < step.getNumberOfScripts(); j++)
            {
               Script script = step.getScript(j);
               boolean error = submitScript(script);


                int numberOfAttempts = 0;
                while (error && numberOfAttempts < 100)
                {
                    System.out.println("error occurs");
                    try
                    {
                        Thread.sleep(3000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    error = submitScript(script);
                    numberOfAttempts++;
                }

                if(numberOfAttempts == 100)
                {
                    throw new RuntimeException("Too many attempts to submit the script");
                }
            }

            //here monitor step execution
            monitor.setStep(step);

            while (!step.isFinished())
            {
                boolean isFinished = false;
                try
                {
                    isFinished = monitor.isStepFinished();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                if (isFinished)
                {
                    step.setFinished(true);
                    step.setActive(false);
                    //TODO here is the correct place to update the database
                }
                else
                {
                    //delay before next checking
                    try
                    {
                        // set 5000 or more for cluster
                        Thread.sleep(getSleepingInterval());
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

        }
        if(exec != null)
            exec.shutdown();
        pipeline.setFinished(true);
        System.out.println("... pipeline " + pipeline.getId() + "finished");

    }

    protected abstract boolean submitScript(Script script);
    protected abstract int getSleepingInterval();

}
