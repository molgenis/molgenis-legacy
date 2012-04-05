package org.molgenis.generator;

import org.molgenis.compute.ComputeJob;
import org.molgenis.protocol.Workflow;
import org.molgenis.util.Tuple;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 05/04/2012
 * Time: 09:46
 * To change this template use File | Settings | File Templates.
 */
public class GenericJobGenerator implements JobGenerator
{

    public Vector<ComputeJob> generateComputeJobs(Workflow workflow, List<Tuple> worksheet)
    {
        return null;
    }

    public boolean generateActualJobs(Vector<ComputeJob> computeJobs, String backend, Hashtable<String, String> config)
    {
        return false;
    }
}
