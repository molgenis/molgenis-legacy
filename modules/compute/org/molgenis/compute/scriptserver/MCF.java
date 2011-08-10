package org.molgenis.compute.scriptserver;


import java.util.concurrent.ExecutorService;

import org.molgenis.compute.pipelinemodel.Pipeline;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: Nov 17, 2010
 * Time: 10:37:41 AM
 * To change this template use File | Settings | File Templates.
 */
public interface MCF
{
    //set pipeline for execution
    void setPipeline(Pipeline pipeline);

    int getNumberActivePipelines();
    Pipeline getActivePipeline(int i);

    int getNumberFinishedPipelines();
    Pipeline getArchivePipeline(int i);

    void removePipeline(String id);

    ExecutorService getExecutor();
    
}
