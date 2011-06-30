package org.molgenis.compute;

import org.molgenis.protocol.ComputeApplication;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 21/06/2011
 * Time: 10:57
 * To change this template use File | Settings | File Templates.
 */
public class ComputeAppPaths
{
    ComputeApplication application = null;
    String logpath;
    String outpath;
    String errpath;

    public String getExtralog()
    {
        return extralog;
    }

    public void setExtralog(String extralog)
    {
        this.extralog = extralog;
    }

    String extralog;

    public ComputeApplication getApplication()
    {
        return application;
    }

    public void setApplication(ComputeApplication application)
    {
        this.application = application;
    }

    public String getLogpath()
    {
        return logpath;
    }

    public void setLogpath(String logpath)
    {
        this.logpath = logpath;
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
}
