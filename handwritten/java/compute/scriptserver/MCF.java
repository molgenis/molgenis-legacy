package compute.scriptserver;

import compute.pipelinemodel.Pipeline;

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

    //temporary demo
    String getSimpleTestInfo();

    void removePipeline(String id);
    
}
