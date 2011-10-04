package org.molgenis.compute.pipelinemodel;

import java.util.Vector;

//step is a bunch of scripts
public class Step
{
    private String id = null;

    private Vector<Script> scripts = new Vector();

    //for execution monitoring
    private boolean isFinished = false;
    private boolean isActive = false;

    private int scriptsStarted = 0;
    private int scriptsFinished = 0;

    //step serial number
    private int stepNumber = -1;

    public Step(String id)
    {
        this.id = id;
    }

    public Step()
    {

    }

    public void addScript(Script newScript)
    {
        scripts.add(newScript);
    }

    public Script getScript(int i)
    {
        return scripts.elementAt(i);
    }

    public int getNumberOfScripts()
    {
        return scripts.size();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void addScripts(Vector<Script> scripts)
    {
        this.scripts.addAll(scripts);
    }

    public boolean isFinished()
    {
        return isFinished;
    }

    public void setFinished(boolean finished)
    {
        isFinished = finished;
    }

    public boolean isActive()
    {
        return isActive;
    }

    public void setActive(boolean active)
    {
        isActive = active;
    }

    public int getScriptsStarted()
    {
        return scriptsStarted;
    }

    public void setScriptsStarted(int scriptsStarted)
    {
        this.scriptsStarted = scriptsStarted;
    }

    public int getScriptsFinished()
    {
        return scriptsFinished;
    }

    public void setScriptsFinished(int scriptsFinished)
    {
        this.scriptsFinished = scriptsFinished;
    }


    public Script getScript(String id)
    {
        for(int i = 0; i < scripts.size(); i++)
        {
            Script script = scripts.elementAt(i);
            if(script.getID().equalsIgnoreCase(id))
                return script;
        }
        return null;
    }

    @Override
    public String toString()
    {
        return "\nnumber: "+ stepNumber +" size: "+ scripts.size() +"  {\n" + ", id='" + id + '\'' +
                "\nscripts=" + scripts +

                '}';
    }

    public void setNumber(int stepNumber)
    {
        this.stepNumber = stepNumber;
    }
}
