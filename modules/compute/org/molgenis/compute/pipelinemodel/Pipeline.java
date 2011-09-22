package org.molgenis.compute.pipelinemodel;


import org.molgenis.compute.monitor.LoggingReader;

import java.util.Vector;


public class Pipeline
{

    private String id = null;

    private Vector<Step> steps = new Vector();
    private LoggingReader monitor = null;

    private String pipelinelogpath;

    private boolean isFinished = false;

    public int getNumberOfSteps()
    {
        return steps.size();
    }

    public Step getStep(int i)
    {
        return steps.elementAt(i);
    }

    public void addStep(Step newStep)
    {
        steps.add(newStep);
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setMonitor(LoggingReader monitor)
    {
        this.monitor = monitor;
    }

    public LoggingReader getMonitor()
    {
        return monitor;
    }

    public String getPipelinelogpath()
    {
        return pipelinelogpath;
    }

    public void setPipelinelogpath(String pipelinelogpath)
    {
        this.pipelinelogpath = pipelinelogpath;
    }

    @Override
    public String toString()
    {
        return "Pipeline{" +
                "id='" + id + '\'' +
                ", steps=" + steps +
                '}';
    }

    public String pipelineLogFile()
    {
        return monitor.getLogFile();
    }

    public boolean isFinished()
    {
        return isFinished;
    }

    public void setFinished(boolean finished)
    {
        isFinished = finished;
    }
}
