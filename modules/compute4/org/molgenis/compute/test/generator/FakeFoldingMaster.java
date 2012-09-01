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
 * Date: 22/08/2012
 * Time: 12:49
 * To change this template use File | Settings | File Templates.
 */
public class FakeFoldingMaster implements FoldingMaster
{

    public Hashtable<String, String> createValues(Collection<ComputeParameter> listParameters, List<Target> targets, Hashtable<String, String> userValues)
    {
        Hashtable<String, String> values = new Hashtable<String, String>();

        for(ComputeParameter c : listParameters)
        {
            String name = c.getName();
            String value = c.getDefaultValue();

            values.put(name, value);
        }

        return values;
    }

    public List<Tuple> createTuples(Collection<ComputeParameter> listParameters, List<Tuple> targets, Hashtable<String, String> userValues)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
