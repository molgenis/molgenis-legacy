package org.molgenis.compute.workflowgenerator;

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

public class ParameterWeaver
{
    private static final String JOB_ID = "jobID";//also is a dataset name
    private static final String DATASET_LOCATION = "location";
    private static final String ACTUAL_COMMAND = "actualcommand";
    private static final String SCRIPT_ID = "scriptID";
    private static final String WALLTIME = "walltime";
    private static final String CLUSTER_QUEUE = "clusterqueue";
    private static final String CORES = "cores";
    private static final String MEMORY_REQ = "memory";

    private static final String DEF_WALLTIME = "23:59:00";
    private static final String DEF_CLUSTER_QUEUE = "nodes";
    private static final String DEF_CORES = "1";
    private static final String DEF_MEMORY_REQ = "7";

    private static final String DEF_DEPENDANCY = "dependancy";
    private static final String DEF_SUBMIT_ID = "submitID";

    private static final String VERIFICATION_COMMAND = "verificationcommand";

    private Hashtable<String, String> scriptParameters = new Hashtable<String, String>();

    private String scriptClusterTemplate = "#!/bin/bash \n" +
            "#PBS -q ${clusterqueue}\n" +
            "#PBS -l nodes=1:ppn=${cores}\n" +
            "#PBS -l walltime=${walltime}\n" +
            "#PBS -l mem=${memory}gb\n" +
            "#PBS -e ${location}/err/err_${scriptID}.err\n" +
            "#PBS -o ${location}/out/out_${scriptID}.out\n" +
            "mkdir -p ${location}/err\n" +
            "mkdir -p ${location}/out\n" +
            "printf \"${scriptID}_started \" >>${location}/log_${jobID}.txt\n" +
            "date \"+DATE: %m/%d/%y%tTIME: %H:%M:%S\" >>${location}/log_${jobID}.txt\n" +
            "date \"+start time: %m/%d/%y%t %H:%M:%S\" >>${location}/extra/${scriptID}.txt\n" +
            "echo running on node: `hostname` >>${location}/extra/${scriptID}.txt\n" +
            "${actualcommand}\n" +
            "${verificationcommand}" +
            "printf \"${scriptID}_finished \" >>${location}/log_${jobID}.txt\n" +
            "date \"+finish time: %m/%d/%y%t %H:%M:%S\" >>${location}/extra/${scriptID}.txt\n" +
            "date \"+DATE: %m/%d/%y%tTIME: %H:%M:%S\" >>${location}/log_${jobID}.txt\n";

    private String submitTemplate = "#job_${submitID}\n" +
            "job_${submitID}=$(qsub -N ${scriptID} ${dependancy} ${scriptID}.sh)\n" +
            "echo $job_${submitID}\n" +
            "sleep 8\n\n";


    private String downloadGridTemplate = "#download input data\n" +
            "srmcp -server_mode=passive srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/data/lsgrid/${input_path}${input_name} \\\n" +
            "file:////scratch/${input_name}\n";

    private String uploadGridTemplate = "#upload result data\n" +
            "srmcp -server_mode=passive file:////scratch/output3.txt \\\n" +
            "srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/data/lsgrid/${output_path}${output_name}\n";

    private String jdlTemplate = "Type=\"Job\";\n" +
            "JobType=\"Normal\";\n" +
            "\n" +
            "Executable = \"/bin/sh\";\n" +
            "Arguments = \"${script_name}.sh\";\n" +
            "\n" +
            "StdError = \"${error_log}\";\n" +
            "StdOutput = \"${output_log}\";\n" +
            "\n" +
            "InputSandbox = {${script_location}${script_name}.sh${extra_inputs}};\n" +
            "OutputSandbox = {${error_log},${output_log}${extra_outputs}};";

    private String logfilename = "${location}/log_${jobID}.txt";
    private String errfilename = "${location}/err/err_${scriptID}.err";
    private String outfilename = "${location}/out/out_${scriptID}.out";
    private String extrafilename = "${location}/extra/${scriptID}.txt";

    private String verificationTemplate = "java -jar /data/gcc/tools/GATK-1.0.5069/Sting/dist/GenomeAnalysisTK.jar " +
            "-R /data/gcc/resources/hg19/indices/human_g1k_v37.fa  " +
            "-I /data/gcc/test_george/819/110214_SN163_391A80MTLABXX_4_AGAGAT.sorted.bam " +
            "-T CountReads " +
            ">>${location}/extra/${scriptID}.txt\n";

    private String gridHeader = "#!/bin/bash\n";


    public String weaveFreemarker(String strTemplate, Hashtable<String, String> parameters)
    {
        Template t = null;
        StringWriter out = new StringWriter();
        try
        {
            t = new Template("name", new StringReader(strTemplate), new Configuration());
            t.process(parameters, out);
        }
        catch (TemplateException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return out.toString();
    }


    public String weaveFeature(String template)
    {
        return weaveFreemarker(template, scriptParameters);
    }

    //extra to test read
    public String makeVerificationScript()
    {
        return weaveFreemarker(verificationTemplate, scriptParameters);
    }

    public String makeScript()
    {
        return weaveFreemarker(scriptClusterTemplate, scriptParameters);
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

    public void setClusterQueue(String str)
    {
        scriptParameters.put(CLUSTER_QUEUE, str);
    }

    public void setCores(String str)
    {
        scriptParameters.put(CORES, str);
    }

    public void setMemoryReq(String str)
    {
        scriptParameters.put(MEMORY_REQ, str);
    }

    public void setDefaults()
    {
        scriptParameters.put(WALLTIME, DEF_WALLTIME);
        scriptParameters.put(CLUSTER_QUEUE, DEF_CLUSTER_QUEUE);
        scriptParameters.put(CORES, DEF_CORES);
        scriptParameters.put(MEMORY_REQ, DEF_MEMORY_REQ);

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

    public String makeGridDownload(Hashtable<String, String> weavingValues)
    {
        String result = weaveFreemarker(downloadGridTemplate, weavingValues);
        return result;
    }

    public String makeGridUpload(Hashtable<String, String> weavingValues)
    {
        String result = weaveFreemarker(uploadGridTemplate, weavingValues);
        return result;
    }

    public String makeJDL(Hashtable<String, String> weavingValues)
    {
        String result = weaveFreemarker(jdlTemplate, weavingValues);
        return result;
    }

    public String makeGridHeader()
    {
        return gridHeader;  //To change body of created methods use File | Settings | File Templates.
    }

    public void setDependancy(String dependancy)
    {
        scriptParameters.put(DEF_DEPENDANCY, dependancy);
    }

    public String makeSumbit()
    {
        return weaveFreemarker(submitTemplate, scriptParameters);
    }

    public void setSubmitID(String s)
    {
        scriptParameters.put(DEF_SUBMIT_ID, s);
    }
}
