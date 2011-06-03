package compute.pipelinemodel;

import compute.monitor.LoggingReader;

import java.util.Vector;


public class Pipeline
{

    private String id = null;

    private Vector<Step> steps = new Vector();
    private LoggingReader monitor = null;

    private String logfile;


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

    public String getLogfile()
    {
        return logfile;
    }

    public void setLogfile(String logfile)
    {
        this.logfile = logfile;
    }

    @Override
    public String toString()
    {
        return "Pipeline{" +
                "id='" + id + '\'' +
                ", steps=" + steps +
                '}';
    }
}
