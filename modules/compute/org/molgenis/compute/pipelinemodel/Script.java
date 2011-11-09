package org.molgenis.compute.pipelinemodel;

import java.io.*;
import java.util.Vector;

//now script has
//local name
//remote name

//remote directory

//data - the listing of the script
public abstract class Script implements Serializable
{
    private String ID = null;

    private String localname = null;
    private String remotename = null;

    private byte[] scriptData = null;

    private String remoteDir = null;

    private boolean isFinished = false;
    private boolean isStarted = false;

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

    public boolean isFinished()
    {
        return isFinished;
    }

    public void setFinished(boolean finished)
    {
        isFinished = finished;
    }

    public boolean isStarted()
    {
        return isStarted;
    }

    public void setStarted(boolean started)
    {
        isStarted = started;
    }

    @Override
    public String toString()
    {
        return "Script{" +
                "ID='" + ID + '\'' +
                ", localname='" + localname + '\'' +
                ", remotename='" + remotename + '\'' +
                ", scriptData=" + new String(scriptData) +
                ", remoteDir='" + remoteDir + '\'' +
                ", hasAdditionalFiles=" + hasAdditionalFiles +
                ", filesToSaveRemotely=" + filesToSaveRemotely +
                '}';
    }

    public abstract String getSubmitCommand();
    public abstract String getMonitoringCommand();
}
