package org.molgenis.compute.pipelinemodel;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 07/11/2011
 * Time: 14:34
 * To change this template use File | Settings | File Templates.
 */
public class GridScript extends Script
{
    public GridScript(String scriptID, String outputRemoteLocation, byte[] bytes)
    {
        super(scriptID, outputRemoteLocation, bytes);
    }

    @Override
    public String getSubmitCommand()
    {
        String result = "glite-wms-job-submit  -d $USER -o " + getID() + " " + getRemoteDir() + "/" + getRemotename() +".jdl";
        return result;
    }
}
