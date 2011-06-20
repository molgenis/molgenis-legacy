package org.molgenis.compute;

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

    private Hashtable<String, String> scriptParameters = new Hashtable<String, String>();

    private String scriptTemplate = "#!/bin/bash \n" +
            "#PBS -q nodes\n" +
            "#PBS -l nodes=1:ppn=1\n" +
            "#PBS -l walltime=${walltime}\n" +
            "#PBS -l mem=7gb\n" +
            "#PBS -e ${location}/err/err_${jobID}_${scriptID}.err\n" +
            "#PBS -o ${location}/out/out_${jobID}_${scriptID}.out\n" +
            "printf \"${scriptID}_started \" >>${location}/log_${jobID}.txt\n" +
            "date \"+DATE: %m/%d/%y%tTIME: %H:%M:%S\" >>${location}/log_${jobID}.txt\n" +
            "${actualcommand}\n" +
            "printf \"${scriptID}_finished \" >>${location}/log_${jobID}.txt\n" +
            "date \"+DATE: %m/%d/%y%tTIME: %H:%M:%S\" >>${location}/log_${jobID}.txt";

    private String logfilename = "${location}/log_${jobID}.txt";


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

        System.out.println("result: " + out.toString());

        return out.toString();
    }

    public String makeScript()
    {
        return weaveFreemarker(scriptTemplate, scriptParameters);
    }

    public String getLogfilename()
    {
        return weaveFreemarker(logfilename, scriptParameters);
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
}
