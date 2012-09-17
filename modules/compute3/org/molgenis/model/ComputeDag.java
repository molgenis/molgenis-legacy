package org.molgenis.model;

import org.molgenis.compute.ComputeJob;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 14/05/2012
 * Time: 15:16
 * To change this template use File | Settings | File Templates.
 */


//Grid DAG
public class ComputeDag
{
    private String id = null;
    private Vector<ComputeJob> jobs = new Vector<ComputeJob>();

    private Vector<ComputeJob> firstJobs = new Vector<ComputeJob>();

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public static Vector<ComputeDag> createDags(Vector<ComputeJob> computeJobs)
    {
        return null;
    }

    public Vector<ComputeJob> setFirstJobs()
    {
        return null;
    }

}
