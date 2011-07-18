package org.molgenis.compute.pipelinemodel;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;

import java.io.*;
import java.util.Random;


//temporary demo class for warp2D pipeline
public class PipelineDemoGenerator
{
    //weaving variables
    static final String JOB_ID = "jobID";
    static final String FILE_ID = "fileID";
    static final String STEP_ID = "stepID";
    static final String SCRIPT_ID = "scriptID";

    static final String SECONDS = "seconds";




    static final String FILE1_ID = "file1";
    static final String FILE2_ID = "file2";

    private Pipeline pipeline = new Pipeline();

    //temp-demo variables

    private String jobID = null;
    private String inputfileslist = null;
    private String remoteLocation = null;

    private String scriptText = "#!/bin/bash \n" +
            "#PBS -q nodes\n" +
            "#PBS -l nodes=1:ppn=1\n" +
            "#PBS -l walltime=00:20:00\n" +
            "#PBS -l mem=7gb\n" +
            "#PBS -e /data/byelas/demo/$jobID$/err_$jobID$.err\n" +
            "#PBS -o /data/byelas/demo/$jobID$/out_$jobID$.out\n" +
            "\n" +
            "mkdir /data/byelas/demo/$jobID$\n" +
            "\n" +
            "printf \"$jobID$_$stepID$_$scriptID$_started \" >>/data/byelas/demo/$jobID$/log.txt\n" +
            "date \"+DATE: %m/%d/%y%tTIME: %H:%M:%S\" >>/data/byelas/demo/$jobID$/log.txt\n" +
            "sleep $seconds$\n" +
            "printf \"$jobID$_$stepID$_$scriptID$_finished \" >>/data/byelas/demo/$jobID$/log.txt\n" +
            "date \"+DATE: %m/%d/%y%tTIME: %H:%M:%S\" >>/data/byelas/demo/$jobID$/log.txt";


    private String scriptLocal =
             "mkdir /demo/$jobID$\n" +
             "\n" +
             "printf \"$jobID$_$stepID$_$scriptID$_started \" >>/demo/$jobID$/log.txt\n" +
             "date \"+DATE: %m/%d/%y%tTIME: %H:%M:%S\" >>/demo/$jobID$/log.txt\n" +
             "sleep $seconds$\n" +
             "printf \"$jobID$_$stepID$_$scriptID$_finished \" >>/demo/$jobID$/log.txt\n" +
             "date \"+DATE: %m/%d/%y%tTIME: %H:%M:%S\" >>/demo/$jobID$/log.txt";


    public Pipeline getTest()
    {
        Pipeline p = generateTestCase("test1", "", "/Users/georgebyelas/Development/ScriptbasedComputePlatform/input.txt", false);
        //p.setLogfile("/data/byelas/demo/test1log.txt");

        return p;

    }


    //here generation of demo pipeline
    public Pipeline generateTestCase(String jobID, String pipelineName, String inputfileslist, boolean isLocal)
    {
        String templateLocation;

        this.jobID = jobID;
        pipeline.setId(jobID);

        this.inputfileslist = inputfileslist;

//        if (!isLocal)
        {
//            templateLocation = "templates/grid";
            remoteLocation = "/data/byelas/scripts/test/";
        }
//        else
//        {
//            templateLocation = "templates/test.sh";
//            remoteLocation = "/upload/test/";
//        }


        System.out.println("job ID: " + jobID);
        //System.out.println("pipeline: " + pipelineName);
        System.out.println("input files: " + inputfileslist);


        //first step - grid
        templateLocation = "/Users/georgebyelas/Development/ScriptbasedComputePlatform/templates/proteomics/grid";
        Step step = getStep(templateLocation, "grid");
        step.setId("grid");
        pipeline.addStep(step);

        //second step - centroid
        templateLocation = "/Users/georgebyelas/Development/ScriptbasedComputePlatform/templates/proteomics/centroid";
        step = getStep(templateLocation, "centroid");
        step.setId("centroid");
        pipeline.addStep(step);


        //third step - warp2D
        templateLocation = "/Users/georgebyelas/Development/ScriptbasedComputePlatform/templates/proteomics/warp2d";
        step = getWarp2DStep(templateLocation, "warp2d");
        step.setId("warp2d");
        pipeline.addStep(step);

        //forth step - metamatch
//        templateLocation = "templates/metamatch";
//        Step step = getMetamatchStep(templateLocation, "metamatch");
//        pipeline.addStep(step);


        return pipeline;
    }

    private Step getMetamatchStep(String templateLocation, String stepID)
    {
        Step step = new Step();

        String templateScript = "";
        try
        {
            templateScript = readFileAsString(templateLocation);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        String strScript = weaveTest(templateScript, jobID, stepID, "NO_FILE_NAME");
                String remoteName = jobID + "_" + stepID + ".sh";
                Script script = new Script(remoteName, strScript.getBytes());

                script.setID(jobID + "_" + stepID);
                script.setRemoteDir(remoteLocation);
                script.setShort(true);


        String templateFilelist = "";
        try
        {
            templateFilelist = readFileAsString("templates/file_list.txt");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        FileToSaveRemotely fileToSave = new FileToSaveRemotely("/data/byelas/proteomics/3.warp2d_out/file_list.txt", templateFilelist.getBytes());
        script.addFileToTransfer(fileToSave);

        step.addScript(script);

        return step; 
    }


    private Step getWarp2DStep(String templateLocation, String stepID)
    {
        Step step = new Step();
        step.setId(stepID);

        String template = "";
        try
        {
            template = readFileAsString(templateLocation);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        try
        {
            BufferedReader in = new BufferedReader(new FileReader(inputfileslist));
            String fileName1 = in.readLine();
            String fileName2;
            while ((fileName2 = in.readLine()) != null)
            {
                String strScript = weaveWarp2d(template, fileName1, fileName2, jobID, stepID);
                String remoteName = jobID + "_" + stepID + "_" + fileName2 + ".sh";
                Script script = new Script(remoteName, strScript.getBytes());

                script.setID(jobID + "_" + stepID + "_" + fileName2);
                script.setRemoteDir(remoteLocation);
                script.setShort(true);

                step.addScript(script);

                //System.out.println(strScript);
            }
            in.close();
        }
        catch (IOException e)
        {
        }


        return step;
    }

    private Step getStep(String templateLocation, String stepID)
    {
        Step step = new Step();
        step.setId(stepID);

        String template = "";
        try
        {
            template = readFileAsString(templateLocation);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        //read file with all input files and iterate on names
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(inputfileslist));
            String fileName;
            while ((fileName = in.readLine()) != null)
            {
                String strScript = weaveTest(template, jobID, stepID, fileName);
                String remoteName = jobID + "_" + stepID + "_" + fileName + ".sh";
                Script script = new Script(remoteName, strScript.getBytes());

                script.setID(jobID + "_" + stepID + "_" + fileName);
                script.setRemoteDir(remoteLocation);
                script.setShort(true);

                step.addScript(script);
            }
            in.close();
        }
        catch (IOException e)
        {
        }

        return step;  //To change body of created methods use File | Settings | File Templates.
    }


    private static String readFileAsString(String filePath) throws java.io.IOException
    {
        byte[] buffer = new byte[(int) new File(filePath).length()];
        BufferedInputStream f = null;
        try
        {
            f = new BufferedInputStream(new FileInputStream(filePath));
            f.read(buffer);
        } finally
        {
            if (f != null) try
            {
                f.close();
            } catch (IOException ignored)
            {
            }
        }
        return new String(buffer);
    }

    private String weaveTest(String strTemplate, String strJobID, String strStepID, String strFile)
    {
        String result;

        StringTemplate template = new StringTemplate(strTemplate, DefaultTemplateLexer.class);

        template.setAttribute(JOB_ID, strJobID);
        template.setAttribute(STEP_ID, strStepID);
        template.setAttribute(FILE_ID, strFile);


        result = template.toString();

        return result;
    }

    private String weaveWarp2d(String strTemplate, String strFile1, String strFile2, String strJobID, String strStepID)
    {
        String result;

        StringTemplate template = new StringTemplate(strTemplate, DefaultTemplateLexer.class);

        template.setAttribute(FILE1_ID, strFile1);
        template.setAttribute(FILE2_ID, strFile2);

        template.setAttribute(JOB_ID, strJobID);
        template.setAttribute(STEP_ID, strStepID);

        result = template.toString();

        return result;
    }

    public Pipeline getDemoPipeline(int id_number)
    {
        boolean isLocal = false;

        Pipeline pipeline = new Pipeline();
        String pipelineID = "pipeline" + id_number;
        pipeline.setId(pipelineID);

        //pipeline.setLogfile("/data/byelas/demo/"+ pipelineID +"/log.txt");

        if(isLocal)
            ;//pipeline.setLogfile("/demo/"+ pipelineID +"/log.txt");


        Random randomGenerator = new Random();
        int stepsNumber = randomGenerator.nextInt(10) + 1;


        for(int i = 0; i < stepsNumber; i++)
        {
            String stepID = "step" + i;
            Step step = new Step(stepID);

            int scriptsNumber = randomGenerator.nextInt(10) + 1;

            for(int j = 0; j < scriptsNumber; j++)
            {
                String scriptID = "script" + j;
                int seconds = randomGenerator.nextInt(30) + 20;
                //if(seconds)
                byte[] text = WeaveDemo(pipelineID, stepID, scriptID, ""+seconds).getBytes();
                Script script = new Script(pipelineID + "_" + stepID + "_" + scriptID, "/data/byelas/demo/scripts/", text);

                if(isLocal)
                    script = new Script(pipelineID + "_" + stepID + "_" + scriptID, "/demo/scripts/", text);

                script.setShort(true);
                step.addScript(script);
            }

            pipeline.addStep(step);
        }


        return pipeline;
    }

    private String WeaveDemo(String pipelineID, String stepID, String scriptID, String seconds)
    {
        String result;

        //StringTemplate template = new StringTemplate(scriptText, DefaultTemplateLexer.class);
        StringTemplate template = new StringTemplate(scriptText, DefaultTemplateLexer.class);


        template.setAttribute(JOB_ID, pipelineID);
        template.setAttribute(STEP_ID, stepID);
        template.setAttribute(SCRIPT_ID, scriptID);
        template.setAttribute(SECONDS, seconds);

        result = template.toString();

        return result;
    }
}
