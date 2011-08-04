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

    Pipeline getPipeline(String id);
    Pipeline getPipeline(int i);

    int getNumberActivePipelines();

    void removePipeline(String id);

    ExecutorService getExecutor();
    
}
