package compute.remoteexecutor;

import java.io.Serializable;

public class RemoteResult implements Serializable
{
    private String outString = null;
    private String errString = null;
    private byte[] data = null;

    public byte[] getData()
    {
        return data;
    }

    public void setData(byte[] data)
    {
        this.data = data;
    }

    public String getOutString()
    {
        return outString;
    }

    public void setOutString(String outString)
    {
        this.outString = outString;
    }

    public String getErrString()
    {
        return errString;
    }

    public void setErrString(String errString)
    {
        this.errString = errString;
    }
}