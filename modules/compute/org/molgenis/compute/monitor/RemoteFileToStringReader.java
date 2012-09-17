package org.molgenis.compute.monitor;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 26/07/2011
 * Time: 16:08
 * To change this template use File | Settings | File Templates.
 */
import java.io.*;
import java.util.concurrent.Callable;

public class RemoteFileToStringReader implements Callable<String>, Serializable
{
    public static final String FILE_IS_NOT_EXISTS = "not-exists";

    private String filename = null;

    public String call() throws Exception
    {
        //read specified file
        String result = new String(getFileAsBytes(filename));
        return result;
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
