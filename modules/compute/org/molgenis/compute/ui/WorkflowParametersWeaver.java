package org.molgenis.compute.ui;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: Mar 14, 2011
 * Time: 1:31:21 PM
 * To change this template use File | Settings | File Templates.
 */

public class WorkflowParametersWeaver
{
    private static final String JOB_ID = "jobID";//also is a dataset name
    private static final String DATASET_LOCATION = "location";
    private static final String ACTUAL_COMMAND = "actualcommand";
    private static final String SCRIPT_ID = "scriptID";
    private static final String WALLTIME = "walltime";
    private static final String VERIFICATION_COMMAND = "verificationcommand";

    private Hashtable<String, String> scriptParameters = new Hashtable<String, String>();

    private String scriptTemplate = "#!/bin/bash \n" +
            "#PBS -q short\n" +
            "#PBS -l nodes=1:ppn=1\n" +
            "#PBS -l walltime=${walltime}\n" +
            "#PBS -l mem=7gb\n" +
            "#PBS -e ${location}/err/err_${scriptID}.err\n" +
            "#PBS -o ${location}/out/out_${scriptID}.out\n" +
            "printf \"${scriptID}_started \" >>${location}/log_${jobID}.txt\n" +
            "date \"+DATE: %m/%d/%y%tTIME: %H:%M:%S\" >>${location}/log_${jobID}.txt\n" +
            "date \"+start time: %m/%d/%y%t %H:%M:%S\" >>${location}/extra/${scriptID}.txt\n" +
            "echo running on node: `hostname` >>${location}/extra/${scriptID}.txt\n" +
            "${actualcommand}\n" +
            "${verificationcommand}" +
            "printf \"${scriptID}_finished \" >>${location}/log_${jobID}.txt\n" +
            "date \"+finish time: %m/%d/%y%t %H:%M:%S\" >>${location}/extra/${scriptID}.txt\n" +
            "date \"+DATE: %m/%d/%y%tTIME: %H:%M:%S\" >>${location}/log_${jobID}.txt\n";

    private String logfilename = "${location}/log_${jobID}.txt";
    private String errfilename = "${location}/err/err_${scriptID}.err";
    private String outfilename = "${location}/out/out_${scriptID}.out";
    private String extrafilename = "${location}/extra/${scriptID}.txt";

    private String verificationTemplate = "java -jar /data/gcc/tools/GATK-1.0.5069/Sting/dist/GenomeAnalysisTK.jar " +
            "-R /data/gcc/resources/hg19/indices/human_g1k_v37.fa  " +
            "-I /data/gcc/test_george/819/110214_SN163_391A80MTLABXX_4_AGAGAT.sorted.bam " +
            "-T CountReads " +
            ">>${location}/extra/${scriptID}.txt\n";


    public String weaveFreemarker(String strTemplate, Hashtable<String, String> parameters)
    {
        Template t = null;
        StringWriter out = new StringWriter();
        try
        {
            t = new Template("name", new StringReader(strTemplate), new Configuration());
            t.process(parameters, out);
        } catch (TemplateException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        //System.out.println("result: " + out.toString());

        return out.toString();
    }

    //extra to test read
    public String makeVerificationScript()
    {
        return weaveFreemarker(verificationTemplate, scriptParameters);
    }

    public String makeScript()
    {
        return weaveFreemarker(scriptTemplate, scriptParameters);
    }

    public String getLogfilename()
    {
        return weaveFreemarker(logfilename, scriptParameters);
    }

    public String getErrfilename()
    {
        return weaveFreemarker(errfilename, scriptParameters);
    }

    public String getOutfilename()
    {
        return weaveFreemarker(outfilename, scriptParameters);
    }


    public void setJobID(String str)
    {
        scriptParameters.put(JOB_ID, str);
    }

    public void setScriptID(String str)
    {
        scriptParameters.put(SCRIPT_ID, str);
    }

    public void setActualCommand(String str)
    {
        scriptParameters.put(ACTUAL_COMMAND, str);
    }

    public void setDatasetLocation(String str)
    {
        scriptParameters.put(DATASET_LOCATION, str);
    }

    public void setWalltime(String str)
    {
        scriptParameters.put(WALLTIME, str);
    }

    public void setVerificationCommand(String str)
    {
        scriptParameters.put(VERIFICATION_COMMAND, str);
    }

    public void writeToFile(String outfilename, String script)
    {
        try
        {
            BufferedWriter out = new BufferedWriter(new FileWriter(outfilename));
            out.write(script);
            out.close();
        }
        catch (IOException e)
        {
        }
    }

    public String getExtralogfilename()
    {

        return weaveFreemarker(extrafilename, scriptParameters);
    }

}
