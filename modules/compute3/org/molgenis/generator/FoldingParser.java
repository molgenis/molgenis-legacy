package org.molgenis.generator;

import org.molgenis.util.Pair;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 26/04/2012
 * Time: 13:17
 * To change this template use File | Settings | File Templates.
 */


//The whole this class is a bullshit, that we have because we do not not how to use our freemarker templates properly
public class FoldingParser
{
    //
    public String parseTemplateLineByHand(String parTemplate, Hashtable<String, Object> line, int i, Hashtable<String, String> simpleValues)
    {
        Hashtable<String, String> values = new Hashtable<String, String>();

        Enumeration ekeys = line.keys();
        while (ekeys.hasMoreElements())
        {
            String ekey = (String) ekeys.nextElement();
            String value;
            if (line.get(ekey) instanceof Collection<?>)
            {
                List<String> eValue = (List<String>) line.get(ekey);
                value = eValue.get(i);
            }
            else
                value = (String) line.get(ekey);
            values.put(ekey, value);
        }

        values.putAll(simpleValues);

        String result = doByHand(parTemplate, values);
        return result;
    }

    //this is really the worst part to parse freemarker by hand to prepare new list of templates to fill with freemarker :(
    public String doByHand(String parTemplate, Hashtable<String, String> values)
    {
        Enumeration ekeys = values.keys();
        while (ekeys.hasMoreElements())
        {
            String ekey = (String) ekeys.nextElement();
            String value = (String) values.get(ekey);

            String freemarkerKey = "${" + ekey + "}";

            if (parTemplate.contains(freemarkerKey))
                parTemplate = parTemplate.replace(freemarkerKey, value);

        }
        return parTemplate;
    }

    public int getFolderedLineSize(Hashtable<String, Object> line)
    {
        //we need to find maximum folded values number
        int max = 1;
        Enumeration ekeys = line.keys();
        while (ekeys.hasMoreElements())
        {
            String ekey = (String) ekeys.nextElement();
            Object eValue = line.get(ekey);

            if (eValue instanceof Collection<?>)
            {
                List<String> l = (List<String>) eValue;
                if (l.size() > max)
                    max = l.size();
            }
        }
        return max;
    }

    public String parseTemplateOneLineByHand(String parTemplate, Hashtable<String, Object> line, Hashtable<String, String> simpleValues)
    {
        Hashtable<String, String> values = new Hashtable<String, String>();

        Enumeration ekeys = line.keys();
        while (ekeys.hasMoreElements())
        {
            String ekey = (String) ekeys.nextElement();
            String value = (String) line.get(ekey);
            values.put(ekey, value);
        }

        values.putAll(simpleValues);

        String result = doByHand(parTemplate, values);
        return result;
    }

    public boolean isValueSimple(Pair<String, Object> value)
    {
        Object v = value.getB();
        if (v instanceof Collection<?>)
        {
            List<String> strings = (List<String>) v;
            for (String str : strings)
            {
                if (str.contains("${"))
                    return false;
            }
        }
        else
        {
            String str = (String) value.getB();
            if (str.contains("${"))
                return false;
        }
        return true;
    }
}
