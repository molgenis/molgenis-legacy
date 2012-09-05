package org.molgenis.compute.test.generator;

import org.molgenis.compute.design.ComputeParameter;
import org.molgenis.compute.test.temp.Target;
import org.molgenis.util.Tuple;

import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: georgebyelas
 * Date: 27/08/2012
 * Time: 16:37
 * To change this template use File | Settings | File Templates.
 */
public class RealFoldingMaster implements FoldingMaster
{
    public Hashtable<String, String> createValues(Collection<ComputeParameter> listParameters, List<Target> targets, Hashtable<String, String> userValues)
    {
        return null;
    }

    public List<Tuple> createTuples(Collection<ComputeParameter> listParameters, List<Tuple> targets, Hashtable<String, String> userValues)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
