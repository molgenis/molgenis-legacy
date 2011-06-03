package compute.scriptserver;

import org.gridgain.grid.*;
import org.gridgain.grid.spi.discovery.jgroups.GridJgroupsDiscoverySpi;
import org.gridgain.grid.spi.communication.jgroups.GridJgroupsCommunicationSpi;
import org.gridgain.grid.spi.topology.basic.GridBasicTopologySpi;
import compute.pipelinemodel.PipelineDemoGenerator;
import compute.pipelinemodel.Step;
import compute.pipelinemodel.Script;
import compute.pipelinemodel.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.Collection;

import compute.remoteexecutor.RemoteResult;
import compute.remoteexecutor.RemoteScriptSubmitter;
import compute.monitor.LoggingReader;

//temporary class for proteomics demo 
public class Start
{
    private Grid grid = null;

    private String jobID;
    private String pipelineName;
    private String inputfileslist;

    private Pipeline pipeline = null;

    private final static String DEMO_LOG = "/data/byelas/demolog/demolog.txt";
    private final static String DEMO_LOCAL = "demolog.txt";


    public Start(String jobID, String pipelineName, String inputfileslist)
    {
        this.jobID = jobID;
        this.pipelineName = pipelineName;
        this.inputfileslist = inputfileslist;
    }

    public static void main(String[] args)
    {
        String jobID = "", pipelineName = "", inputfileslist = "";


        if (args.length > 1)
        {
            jobID = args[0];
            pipelineName = args[1];
            inputfileslist = args[2];
        } else
        {
            System.exit(1);
        }

        Start start = new Start(jobID, pipelineName, inputfileslist);
        start.demo();

        System.out.println("... finished");
    }

    //pipeline execution
    private void demo()
    {
        //generate demo pipeline
        PipelineDemoGenerator generator = new PipelineDemoGenerator();

        //last parameter is a flag for local or remote demo
        //false = cluster
        boolean isLocal = false;
        //pipeline = generator.generateTestCase(jobID, pipelineName, inputfileslist, isLocal);
        pipeline = generator.getTest();

//        System.out.println("exit demo");
//        exit(0);

        //start gridgain
        startGridGain();

        //monitor, which reads logging files
        LoggingReader monitor = new LoggingReader();
        if(isLocal)
            monitor.setLogFile(DEMO_LOCAL);
        else
        monitor.setLogFile(DEMO_LOG);
        monitor.setGrid(grid);


        //just for demo purpose
        waitForRemoteNode();

        ExecutorService exec = grid.newGridExecutorService();

        int numberOfSteps = pipeline.getNumberOfSteps();

        System.out.println(">>> start pipeline " + pipeline.getId());

        for (int i = 0; i < numberOfSteps; i++)
        {
            Step step = pipeline.getStep(i);

            System.out.println(">>> start step " + step.getId());

            for (int j = 0; j < step.getNumberOfScripts(); j++)
            {
                Script script = step.getScript(j);

                Future<RemoteResult> future = exec.submit(new RemoteScriptSubmitter(script));

                RemoteResult back = null;
                try
                {
                    back = future.get();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }

                System.out.println("output: " + back.getOutString());
                System.out.println("error: " + back.getErrString());

            }

            //here monitor step execution
            monitor.setStep(step);

            System.out.println(">>> monitoring execution");

            while(!monitor.isNotFinishedStep())
            {
                monitor.isStepFinished();

                try
                {
                    Thread.sleep(3000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            
            System.out.println("... step finished");

        }
        exec.shutdown();

        System.out.println("... pipeline finished");


    }

    private void waitForRemoteNode()
    {
        boolean remoteNodeExists = false;

        while (!remoteNodeExists)
        {
            Collection<GridNode> remoteNodes = grid.getRemoteNodes();

            if (remoteNodes.size() > 0)
            {
                remoteNodeExists = true;
            } else
            {
                System.out.println("... wait for remote node");
                try
                {
                    Thread.sleep(3000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(">>> continue execution");

    }

    private void startGridGain()
    {
        GridConfigurationAdapter cfg = new GridConfigurationAdapter();

        GridBasicTopologySpi topSpi = new GridBasicTopologySpi();

        // Exclude local node from topology.
        topSpi.setLocalNode(false);


        // Configure your own topology SPI.
        cfg.setTopologySpi(topSpi);

        //discovery spi
        GridJgroupsDiscoverySpi spi = new GridJgroupsDiscoverySpi();

        // Override default JGroups configuration file.
        spi.setConfigurationFile("/config/jgroups/tcp/jgroups.xml");

        // Override default discovery SPI.
        cfg.setDiscoverySpi(spi);


        //transport spi
        GridJgroupsCommunicationSpi commSpi = new GridJgroupsCommunicationSpi();

        // Override default JGroups configuration file.
        commSpi.setConfigurationFile("/config/jgroups/tcp/jgroups.xml");
        cfg.setCommunicationSpi(commSpi);

        try
        {
            GridFactory.start(cfg);
            grid = GridFactory.getGrid();
        }
        catch (GridException e)
        {
            e.printStackTrace();
        }

        System.out.println("... compute manager started");

    }
}
