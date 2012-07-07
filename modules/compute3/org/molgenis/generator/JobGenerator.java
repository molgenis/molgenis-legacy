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
    public static final String EXE = "exe";
    public static final String LOG = "log";

    //key words in templates for cluster (other cluster key words are reused from ModelLoader)
    public static final String JOB_ID = "jobname";
    public static final String JOB_DEPENDENCIES = "depend";

    public static final String DEPENDENCY_HEAD = " -W depend=afterok";

    public static final String FLAG = "#FOREACH";
    public static final String GRID_TEMP_DIR = "$TMPDIR";
    public static final String GRID_LOCATION_PREFIX = "lfn://grid/";
    public static final String SOURCE_SCRIPT = "source getdata.sh";

    //key words in template for macros
    public static final String MACRO_BACKEND = "backend";
    public static final String MACRO_TYPE = "type";
    public static final String MACRO_PATH = "path";
    public static final String MACRO_NAME = "name";
    public static final String MACRO_EXTENSIONS = "extensions";


    Vector<ComputeJob> generateComputeJobsFoldedWorksheet(Workflow workflow, List<Tuple> worksheet, String backend);

    Vector<ComputeJob> generateComputeJobsDB(Workflow workflow, List<ObservationTarget> worksheet, String backend);

    boolean generateActualJobs(Vector<ComputeJob> computeJobs, String backend, Hashtable<String,String> config);
    boolean generateActualJobsWithMacros(Vector<ComputeJob> computeJobs, String backend, Hashtable<String,String> config);

    void setConfig(Hashtable<String,String> config);

    void setWorksheet(List<Tuple> worksheet);

}
