package compute.pipelinemodel;

import java.io.Serializable;

public class FileToSaveRemotely implements Serializable
{
    private String remoteName = null;
    private byte[] fileData = null;

    public FileToSaveRemotely(String remoteName, byte[] fileData)
    {
        this.remoteName = remoteName;
        this.fileData = fileData;
    }

    public String getRemoteName()
    {
        return remoteName;
    }

    public byte[] getFileData()
    {
        return fileData;
    }
}
