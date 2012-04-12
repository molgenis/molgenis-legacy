package org.molgenis.generator;

import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 12/04/2012
 * Time: 14:51
 * To change this template use File | Settings | File Templates.
 */
public class GridTransferContainer
{
    private Hashtable<String, String> inputs = new Hashtable<String, String>();
    private Hashtable<String, String> outputs = new Hashtable<String, String>();
    private Hashtable<String, String> exes = new Hashtable<String, String>();
    private Hashtable<String, String> logs = new Hashtable<String, String>();

    public void addInput(String id, String value)
    {
        inputs.put(id, value);
    }

    public void addOutput(String id, String value)
    {
        outputs.put(id, value);
    }

    public void addExe(String id, String value)
    {
        exes.put(id, value);
    }

    public void addLog(String id, String value)
    {
        logs.put(id, value);
    }

    public Hashtable<String, String> getInputs()
    {
        return inputs;
    }

    public Hashtable<String, String> getOutputs()
    {
        return outputs;
    }

    public Hashtable<String, String> getExes()
    {
        return exes;
    }

    public Hashtable<String, String> getLogs()
    {
        return logs;
    }
}
