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
    static final String STATUS_SUBMIT_SUCCESS =     "glite-wms-job-submit Success";
    static final String STATUS_RUN =                "glite-wms-job-status Success";

    static final String STATUS_RUN_WAITING =        "Waiting";
    static final String STATUS_RUN_SCHEDULED =      "Scheduled";
    static final String STATUS_RUN_RUNNING =        "Running";
    static final String STATUS_RUN_DONE_SUCCESS =   "Done (Success)";
    static final String STATUS_RUN_DONE_FAILED =    "Done (Exit Code !=0)";



    public GridScript(String scriptID, String outputRemoteLocation, byte[] bytes)
    {
        super(scriptID, outputRemoteLocation, bytes);
    }

    @Override
    public String getSubmitCommand()
    {
        //"cd /home/byelas\n" +
        String result = "glite-wms-job-submit  -d $USER -o " + getID() + " " + getRemoteDir() + "/" + getID() +".jdl";
        return result;
    }

    @Override
    public String getMonitoringCommand()
    {
        String result = "glite-wms-job-status -i " + getID();
        return result;
    }
}
