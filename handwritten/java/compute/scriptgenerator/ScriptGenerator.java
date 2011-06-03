package compute.scriptgenerator;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;

import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

import compute.pipelinemodel.Script;
import compute.scriptserver.Constants;


public class ScriptGenerator
{
    private static final String I = "i"; //here a chromosome number
    private static final String BATCH = "batch";

    private char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    public void generateAStep(String strTemplate, Hashtable parameters, String stepID, String outputLocation, String outputName)
    {
        String template = null;

        try
        {
            template = readFileAsString(strTemplate);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        String result = weaveAStep(template, parameters);
        writeToFile(outputLocation + System.getProperty("file.separator") + outputName + "_" + stepID + ".sh", result);

    }

    //generates a single script
    public Script generateScript(String strTemplate, Hashtable parameters, String stepID, String outputRemoteLocation)
    {
        String template = null;
        String jobID = (String) parameters.get(Constants.JOB_ID);

        try
        {
            template = readFileAsString(strTemplate);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        String result = weaveAStep(template, parameters);
        String scriptID = jobID + "_" + stepID;

        Script script = new Script(scriptID, outputRemoteLocation, result.getBytes());
        return script;
    }

    //simple generation
    public void generateAStep(String strTemplate, Hashtable parameters, String stepID, String outputLocation, String outputName, int filesNumber)
    {
        String template = null;

        try
        {
            template = readFileAsString(strTemplate);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        for (int chr = 1; chr < filesNumber; chr++)
        {
            parameters.put(I, "" + chr);
            String result = weaveAStep(template, parameters);
            writeToFile(outputLocation + System.getProperty("file.separator") + outputName + "_" + stepID + "_chr" + chr + ".sh", result);
        }
    }

    //generates a number of scripts = number of chromosomes
    public Vector<Script> generateScripts(String strTemplate, Hashtable parameters, String stepID, String outputRemoteLocation, int filesNumber)
    {
        Vector<Script> scripts = new Vector();
        String template = null;
        String jobID = (String) parameters.get(Constants.JOB_ID);


        try
        {
            template = readFileAsString(strTemplate);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        for (int chr = 1; chr < filesNumber; chr++)
        {
            parameters.put(I, "" + chr);
            String result = weaveAStep(template, parameters);
            String scriptID = jobID + "_" + stepID + "_chr" + chr;
            Script script = new Script(scriptID, outputRemoteLocation, result.getBytes());
            scripts.add(script);
        }

        return scripts;
    }

    //specific imputation script generation
    //gererates a number of scripts = number of chromosomes * floor(number of sumples/300)
    public void generateAStep(String strTemplate, Hashtable parameters, String stepID, String outputLocation, String outputName, int filesNumber, int numberSamples)
    {
        String template = null;

        try
        {
            template = readFileAsString(strTemplate);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        for (int chr = 1; chr < filesNumber; chr++)
        {
            int first = 0;
            int second = 0;

            for (int j = 0; j < numberSamples - 300; j = j + 300)
            {
                String batch = "" + alphabet[first] + alphabet[second];
                second++;

                parameters.put(I, "" + chr);
                parameters.put(BATCH, batch);
                String result = weaveAStep(template, parameters);
                writeToFile(outputLocation + System.getProperty("file.separator") + outputName + "_" + stepID + "_chr" + chr + "_" + batch + ".sh", result);

                if (second == 26)
                {
                    second = 0;
                    first++;
                }
            }
        }
    }

    public Vector<Script> generateScripts(String strTemplate, Hashtable parameters, String stepID, String outputRemoteLocation, int filesNumber,
                                          int batchSize)
    {
        Vector<Script> scripts = new Vector();
        String template = null;
        String jobID = (String) parameters.get(Constants.JOB_ID);
        int numberSamples = Integer.parseInt((String) parameters.get(Constants.SIZE));

        try
        {
            template = readFileAsString(strTemplate);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        for (int chr = 1; chr < filesNumber; chr++)
        {
            int first = 0;
            int second = 0;

            for (int j = 0; j < numberSamples - batchSize; j = j + batchSize)
            {
                String batch = "" + alphabet[first] + alphabet[second];
                second++;

                parameters.put(I, "" + chr);
                parameters.put(BATCH, batch);
                String result = weaveAStep(template, parameters);
                
                String scriptID = jobID + "_" + stepID + "_chr" + chr + "_" + batch;
                Script script = new Script(scriptID, outputRemoteLocation, result.getBytes());
                scripts.add(script);


                if (second == 26)
                {
                    second = 0;
                    first++;
                }
            }
        }

        return scripts;
    }


    private String weaveAStep(String strTemplate, Hashtable parameters)
    {
        String result;

        StringTemplate template = new StringTemplate(strTemplate, DefaultTemplateLexer.class);

        Enumeration e = parameters.keys();

        while (e.hasMoreElements())
        {
            String name = (String) e.nextElement();
            String value = (String) parameters.get(name);
            template.setAttribute(name, value);
        }

        result = template.toString();

        return result;
    }

    private static String readFileAsString(String filePath) throws IOException
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

    private void writeToFile(String outfilename, String script)
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