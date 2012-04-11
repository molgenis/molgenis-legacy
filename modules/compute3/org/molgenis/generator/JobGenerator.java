package org.molgenis.generator;

import org.molgenis.compute.ComputeJob;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.protocol.Workflow;
import org.molgenis.util.Tuple;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 05/04/2012
 * Time: 09:45
 * To change this template use File | Settings | File Templates.
 */
public interface JobGenerator
{
    //remote back-ends
    public static final String GRID = "grid";
    public static final String CLUSTER = "cluster";
    //configuration flags
    public static final String OUTPUT_DIR = "outputdir";
    public static final String BACK_END_DIR = "backenddir";
    public static final String GENERATION_ID = "runid";
    public static final String TEMPLATE_DIR = "templatedir";

    Vector<ComputeJob> generateComputeJobsWorksheet(Workflow workflow, List<Tuple> worksheet);
    Vector<ComputeJob> generateComputeJobsDB(Workflow workflow, List<ObservationTarget> worksheet);

    boolean generateActualJobs(Vector<ComputeJob> computeJobs, String backend, Hashtable<String,String> config);

    void setConfig(Hashtable<String,String> config);
}
