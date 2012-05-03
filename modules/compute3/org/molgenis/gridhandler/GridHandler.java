package org.molgenis.gridhandler;

import org.molgenis.compute.ComputeJob;
import org.molgenis.util.Tuple;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 03/05/2012
 * Time: 13:58
 * To change this template use File | Settings | File Templates.
 */
public interface GridHandler
{
    int getNextJobID();
    void setWorksheet(List<Tuple> worksheet);
    void setComputeJob(ComputeJob job);

    void writeCurrentTupleToFile();
}
