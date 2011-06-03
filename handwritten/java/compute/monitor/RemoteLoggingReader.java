package compute.monitor;

import java.util.concurrent.Callable;
import java.io.*;

import compute.remoteexecutor.RemoteResult;

public class RemoteLoggingReader implements Callable<RemoteResult>, Serializable
{
    RemoteResult returnData = new RemoteResult();

    public static final String FILE_IS_NOT_EXISTS = "not-exists";

    private String logging_filename = null;


    public RemoteLoggingReader(String logging_filename)
    {
        this.logging_filename = logging_filename;
    }

    //probably monitor logic should be performed remotely!!!
    public RemoteResult call() throws Exception
    {
        System.out.println(">>> read logging");
        returnData.setData(getFileAsBytes(logging_filename));
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

}