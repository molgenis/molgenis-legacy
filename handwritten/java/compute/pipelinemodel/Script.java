package compute.pipelinemodel;

import java.io.*;
import java.util.Arrays;
import java.util.Vector;

//now script has
//local name
//remote name

//remote directory

//data - the listing of the script
public class Script implements Serializable
{
    private String ID = null;

    private String localname = null;
    private String remotename = null;

    private byte[] scriptData = null;

    private String remoteDir = null;


    private boolean hasAdditionalFiles = false;
    private Vector<FileToSaveRemotely> filesToSaveRemotely = new Vector();

    private boolean isShort = false;

    public Script(String scriptID, String outputRemoteLocation, byte[] bytes)
    {
        this.ID = scriptID;
        this.remotename = ID + ".sh";
        this.remoteDir = outputRemoteLocation;
        this.scriptData = bytes;
    }

    public String getID()
    {
        return ID;
    }

    public void setID(String ID)
    {
        this.ID = ID;
    }

    //todo delete
    public Script(String localname, String remotename)
    {
        this.localname = localname;
        this.remotename = remotename;

        try
        {
            this.scriptData = getFileAsBytes(localname);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //todo delete
    public Script(String remotename, byte[] scriptListing)
    {
        this.remotename = remotename;
        this.scriptData = scriptListing;
    }


    public String getLocalname()
    {
        return localname;
    }

    public String getRemotename()
    {
        return remotename;
    }

    private final byte[] getFileAsBytes(String filename) throws IOException
    {

        File file = new File(filename);

        final BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream(file));
        final byte[] bytes = new byte[(int) file.length()];
        bis.read(bytes);
        bis.close();
        return bytes;
    }

    public byte[] getScriptData()
    {
        return scriptData;
    }

    public String getRemoteDir()
    {
        return remoteDir;
    }

    public void setRemoteDir(String remoteDir)
    {
        this.remoteDir = remoteDir;
    }

    public boolean isHasAdditionalFiles()
    {
        return hasAdditionalFiles;
    }

    public void setHasAdditionalFiles(boolean hasAdditionalFiles)
    {
        this.hasAdditionalFiles = hasAdditionalFiles;
    }

    public void addFileToTransfer(FileToSaveRemotely file)
    {
        filesToSaveRemotely.add(file);
    }

    public FileToSaveRemotely getFileToSaveRemotely(int i)
    {
        return filesToSaveRemotely.elementAt(i);
    }

    public boolean isShort()
    {
        return isShort;
    }

    public void setShort(boolean aShort)
    {
        isShort = aShort;
    }

    public int getNumberFileToSaveRemotely()
    {
        return filesToSaveRemotely.size(); 
    }

    @Override
    public String toString()
    {
        return "\nScript{" +
                "ID='" + ID + '\'' +
                ", \nremotename='" + remotename + '\'' +
                ", \nscriptData=" + new String(scriptData) +
                ", \nremoteDir='" + remoteDir + '\'' +
                '}';
    }
}
