package org.molgenis.compute.scriptserver;


import org.molgenis.compute.pipelinemodel.Pipeline;
import java.util.concurrent.ExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: Nov 17, 2010
 * Time: 10:37:41 AM
 * To change this template use File | Settings | File Templates.
 */
public interface MCF
{

    public static final String GRID = "gridgain";
    public static final String SSH = "ssh";
    //set pipeline for execution
    void setClusterPipeline(Pipeline pipeline);

    void setGridPipeline(Pipeline pipeline);

    int getNumberActivePipelines();
    Pipeline getActivePipeline(int i);

    int getNumberFinishedPipelines();
    Pipeline getArchivePipeline(int i);

    void removePipeline(String id);

    ExecutorService getExecutor();

    void removeFinishedPipelines();

    String getBasis();
}
