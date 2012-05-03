package org.molgenis.gridhandler;

import org.molgenis.compute.ComputeJob;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 03/05/2012
 * Time: 15:05
 * To change this template use File | Settings | File Templates.
 */
public class CommandLineImputationGridHandler extends CommandLineGridHandler
{
    private List<Hashtable<String, String>> listImputationTargetsResults = new ArrayList<Hashtable<String, String>>();

    public int getNextJobID()
    {
        return 1;
    }

    public void setComputeJob(ComputeJob job)
    {
    }

    public void writeCurrentTupleToFile()
    {
    }
}
