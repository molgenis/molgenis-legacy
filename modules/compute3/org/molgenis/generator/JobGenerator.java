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

    //key words in templates for grid
    public static final String LFN_NAME = "lfn_name";
    public static final String INPUT = "input";
    public static final String OUTPUT = "output";
    public static final String LOG = "log";

    //key words in templates for cluster (other cluster key words are reused from ModelLoader)
    public static final String JOB_ID = "jobname";
    public static final String JOB_DEPENDENCIES = "depend";

    public static final String DEPENDENCY_HEAD = " -W depend=afterok";

    public static final String FLAG = "#FOREACH";

    Vector<ComputeJob> generateComputeJobsFoldedWorksheet(Workflow workflow, List<Tuple> worksheet, String backend);

    Vector<ComputeJob> generateComputeJobsDB(Workflow workflow, List<ObservationTarget> worksheet, String backend);

    boolean generateActualJobs(Vector<ComputeJob> computeJobs, String backend, Hashtable<String,String> config);

    void setConfig(Hashtable<String,String> config);

    void setWorksheet(List<Tuple> worksheet);
}
