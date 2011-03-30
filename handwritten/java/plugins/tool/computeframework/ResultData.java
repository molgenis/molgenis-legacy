package plugins.tool.computeframework;

import java.io.Serializable;


//data to be transfered from the remote node
public class ResultData implements Serializable
{

	private static final long serialVersionUID = 415179597431377866L;
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

    @Override
    public String toString()
    {
        return "ResultData{" +
                "outString='" + outString + '\'' +
                ", errString='" + errString + '\'' +
                '}';
    }
}