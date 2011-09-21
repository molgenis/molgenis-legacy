package org.molgenis.compute.monitor;

import java.util.concurrent.Callable;
import java.io.*;

import org.molgenis.compute.remoteexecutor.RemoteResult;


public class RemoteFileReader implements Callable<RemoteResult>, Serializable
{
    RemoteResult returnData = new RemoteResult();

    public static final String FILE_IS_NOT_EXISTS = "not-exists";

    private String filename = null;


    public RemoteFileReader(String filename)
    {
        this.filename = filename;
    }

    public RemoteFileReader()
    {

    }

    //probably monitor logic should be performed remotely!!!
    public RemoteResult call() throws Exception
    {
        returnData.setData(getFileAsBytes(filename));
        return returnData;
    }

    private final byte[] getFileAsBytes(String filename) throws IOException
    {
        File file = new File(filename);

        if(!file.exists())
            return FILE_IS_NOT_EXISTS.getBytes();
        
        final BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream(file));
        final byte[] bytes = new byte[(int) file.length()];
        bis.read(bytes);
        bis.close();
        return bytes;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }
}