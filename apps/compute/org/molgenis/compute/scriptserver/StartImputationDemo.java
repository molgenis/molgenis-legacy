package org.molgenis.compute.scriptserver;


import java.util.Hashtable;

import org.gridgain.grid.Grid;
import org.molgenis.compute.grid.GridStarter;
import org.molgenis.compute.pipelinemodel.Pipeline;
import org.molgenis.compute.resourcemanager.ResourceManager;
import org.molgenis.compute.scriptgenerator.ImpPipelineGenerator;



//main class to start imputation on the cluster - demo with one imputation task
public class StartImputationDemo
{

    public static void main(String[] args)
    {
        String jobID = null, //dataset name
               datasetLocation = null, //dataset location on the cluster
               outputLocation = null, //for scripts on the cluster
               hapmapLocation = null, //trityper format
               hapmapBeagleLocation = null, //beagle format
               numberSamples = null; //in the dataset

        if (args.length > 1)
        {
            jobID = args[0];
            datasetLocation = args[1];

            outputLocation = args[2];
            //numberSamples = Integer.parseInt(args[3]);
            numberSamples = args[3];

            hapmapLocation = args[4];
            hapmapBeagleLocation = args[5];

        } else
        {
            System.exit(1);
        }

        StartImputationDemo imputationDemo = new StartImputationDemo();
        imputationDemo.test(jobID, datasetLocation, hapmapLocation, hapmapBeagleLocation, numberSamples, outputLocation);
    }

    private void test(String jobID, String datasetLocation, String hapmapLocation, String hapmapBeagleLocation, String numberSamples, String outputRemoteLocation)
    {
        Pipeline impPipeline = null;

        Hashtable parameters = new Hashtable();

        parameters.put(Constants.JOB_ID, jobID);
        parameters.put(Constants.DATASET_LOCATION, datasetLocation);
        parameters.put(Constants.HAPMAP_LOCATION, hapmapLocation);
        parameters.put(Constants.HAPMAP_BEAGLE_LOCATION, hapmapBeagleLocation);
        parameters.put(Constants.SIZE, numberSamples);

        //generate imputation pipeline
        ImpPipelineGenerator generator = new ImpPipelineGenerator();
        try
        {
            impPipeline = generator.getImputationPipeline(parameters, outputRemoteLocation);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //start grid
        GridStarter gridStarter = new GridStarter();
        Grid grid = gridStarter.startGrid();

        //start resource manager
        ResourceManager resourceManager = new ResourceManager();
        resourceManager.setGrid(grid);
        resourceManager.setSettings(8, 10);

        //wait for remote node on the cluster
        gridStarter.waitForRemoteNode(grid);

        //here remote node is available

        //specify log file for pipeline
        String logfile = datasetLocation + System.getProperty("file.separator") + Constants.IMPUTATION_LOG + jobID;

        //create thread to run pipeline
        PipelineThread pipelineThread = new PipelineThread(impPipeline, grid, logfile);

        //run pipeline
        new PipelineExecutor().execute(pipelineThread);

    }
}
