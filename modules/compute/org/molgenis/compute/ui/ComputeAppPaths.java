package org.molgenis.compute.ui;

import java.util.Vector;

import org.molgenis.compute.ComputeJob;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 21/06/2011
 * Time: 10:57
 * To change this template use File | Settings | File Templates.
 */
public class ComputeAppPaths
{


    public enum AppStatus
    {
        idle, started, finished;
    }

    private AppStatus prevStatus = AppStatus.idle;


    ComputeJob application = null;
    Vector<String> logpaths = new Vector<String>();
    String outpath;
    String errpath;
    String extralog;


    public String getExtralog()
    {
        return extralog;
    }

    public void setExtralog(String extralog)
    {
        this.extralog = extralog;
    }

    public ComputeJob getApplication()
    {
        return application;
    }

    public void setApplication(ComputeJob application)
    {
        this.application = application;
    }


    public void addLogpath(String s)
    {
        logpaths.addElement(s);
    }

    public String getLogpath(int i)
    {
        return logpaths.elementAt(i);
    }

    public int getLogpathSize()
    {
        return logpaths.size();
    }


    public String getOutpath()
    {
        return outpath;
    }

    public void setOutpath(String outpath)
    {
        this.outpath = outpath;
    }

    public String getErrpath()
    {
        return errpath;
    }

    public void setErrpath(String errpath)
    {
        this.errpath = errpath;
    }

    public AppStatus getPrevStatus()
    {
        return prevStatus;
    }

    public void setPrevStatus(AppStatus prevStatus)
    {
        this.prevStatus = prevStatus;
    }
}
