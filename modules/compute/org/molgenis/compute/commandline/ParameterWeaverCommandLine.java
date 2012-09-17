package org.molgenis.compute.commandline;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: Mar 14, 2011
 * Time: 1:31:21 PM
 * To change this template use File | Settings | File Templates.
 */

public class ParameterWeaverCommandLine
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

    public static final String DO_DOWNLOAD = "#INPUTS";
    public static final String DO_EXECUTABLE = "#EXES";
    public static final String DO_UPLOAD  = "#OUTPUTS";

    //temporary for targets
    public static final String READ_TARGETS  = "#TARGETS";


    private Hashtable<String, String> scriptParameters = new Hashtable<String, String>();

    private String scriptClusterTemplate = null;
    private String submitTemplate = null;
    private String downloadGridTemplate = null;
    private String executableGridTemplate = null;
    private String uploadGridTemplate = null;
    private String jdlTemplate = null;

    private String logfilename = null;
    private String errfilename = null;
    private String outfilename = null;
    private String extrafilename = null;

    private String executionGridDirectory = null;

    private String verificationTemplate = "java -jar /data/gcc/tools/GATK-1.0.5069/Sting/dist/GenomeAnalysisTK.jar " +
            "-R /data/gcc/resources/hg19/indices/human_g1k_v37.fa  " +
            "-I /data/gcc/test_george/819/110214_SN163_391A80MTLABXX_4_AGAGAT.sorted.bam " +
            "-T CountReads " +
            ">>${location}/extra/${scriptID}.txt\n";

    private String gridHeader = "#!/bin/bash\n";

    public ParameterWeaverCommandLine(String templateDir)
    {
        readTemplates(templateDir);
    }

    private void readTemplates(String templateDir)
    {
        try
        {
            scriptClusterTemplate = getFileAsString(templateDir + System.getProperty("file.separator") + "templ-cluster.ftl");
            submitTemplate = getFileAsString(templateDir + System.getProperty("file.separator") + "templ-submit.ftl");
            downloadGridTemplate = getFileAsString(templateDir + System.getProperty("file.separator") + "templ-download-grid.ftl");
            executableGridTemplate = getFileAsString(templateDir + System.getProperty("file.separator") + "templ-exe-grid.ftl");
            uploadGridTemplate = getFileAsString(templateDir + System.getProperty("file.separator") + "templ-upload-grid.ftl");
            jdlTemplate = getFileAsString(templateDir + System.getProperty("file.separator") + "templ-jdl-grid.ftl");

            logfilename = getFileAsString(templateDir + System.getProperty("file.separator") + "templ-logfile.ftl");
            errfilename = getFileAsString(templateDir + System.getProperty("file.separator") + "templ-errorfile.ftl");
            outfilename = getFileAsString(templateDir + System.getProperty("file.separator") + "templ-outfile.ftl");
            extrafilename = getFileAsString(templateDir + System.getProperty("file.separator") + "templ-extrafile.ftl");

            executionGridDirectory = getFileAsString(templateDir + System.getProperty("file.separator") + "templ-exe-grid-dir.ftl");

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

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

    public String processGridHeader(String element, String template, Hashtable<String, String> weavingValues)
    {
        String header = "";
        int posInput = template.indexOf(element);
        int posNewLine = template.indexOf("\n", posInput);
        String list = template.substring(posInput, posNewLine);

        Vector<String> inputs = findNames(list);

        for(int i = 0; i < inputs.size(); i++)
        {
            Hashtable<String, String> local = new Hashtable<String, String>();

            //here generate header (downloading part for the grid + replace weaving value is a new one for the grid)
            //example /tools/tool1.exe will become /scratch/tool1.exe
            String str = inputs.elementAt(i);

            String srmValue = weavingValues.get(str);
            int posSlash = srmValue.lastIndexOf("/");
            String justName = srmValue.substring(posSlash + 1);

            local.put("srm_name", srmValue);
            local.put("just_name", justName);

            weavingValues.put(str, executionGridDirectory + justName);

            String result = "";
            if(element.equalsIgnoreCase(DO_DOWNLOAD))
                result = weaveFreemarker(downloadGridTemplate, local);
            else if(element.equalsIgnoreCase(DO_EXECUTABLE))
                result = weaveFreemarker(executableGridTemplate, local);
            else if(element.equalsIgnoreCase(DO_UPLOAD))
                result = weaveFreemarker(uploadGridTemplate, local);
            header += result;
        }

        return header;
    }

    private Vector<String> findNames(String list)
    {
        Vector<String> names = new Vector<String>();
        int posEmpty = list.indexOf(" ") + 1;
        list = list.substring(posEmpty);

        while(list.indexOf(",") > -1)
        {
            int posComa = list.indexOf(",");
            String name = list.substring(0, posComa).trim();
            names.addElement(name);
            list = list.substring(posComa+1);
        }
        names.add(list.trim());
        return names;
    }

    private final String getFileAsString(String filename) throws IOException
    {
        File file = new File(filename);

        if(!file.exists())
        {
            System.out.println("reading template error: " + filename + "does not exist");
        }
        final BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream(file));
        final byte[] bytes = new byte[(int) file.length()];
        bis.read(bytes);
        bis.close();
        return new String(bytes);
    }

    public Vector<String> parseHeaderElement(String header, String protocol)
    {
        int posInput = protocol.indexOf(header);
        int posNewLine = protocol.indexOf("\n", posInput);
        String list = protocol.substring(posInput, posNewLine);

        return findNames(list);
    }
}
