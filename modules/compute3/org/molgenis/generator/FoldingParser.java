package org.molgenis.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.molgenis.compute.ComputeParameter;
import org.molgenis.util.Pair;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

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
    private static String BAD = "The problematic instruction:";

    private boolean isTableFoldered = false;
    private Vector<String> vecNotFolderedConstants = null;
    private Collection<ComputeParameter> parameters = null;

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
        String result = weaveFreemarker(parTemplate, values);

        if (result.contains(BAD))
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
        return result;
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

    public boolean isDirectlyDependOnWorksheet(ComputeParameter parameter, List<Hashtable> table)
    {
        Hashtable row = table.get(0);
        Enumeration rowKeys = row.keys();
        while (rowKeys.hasMoreElements())
        {
            String ekey = (String) rowKeys.nextElement();
            ekey = "${" + ekey + "}";

            String parTemplate = parameter.getDefaultValue();
            if (parTemplate.contains(ekey))
                return true;
        }

        return false;
    }

    public void evaluateTable(List<Hashtable> table)
    {
        vecNotFolderedConstants = new Vector<String>();

        Hashtable<String, Object> line = table.get(0);
        Enumeration ekeys = line.keys();
        while (ekeys.hasMoreElements())
        {
            String ekey = (String) ekeys.nextElement();
            Object eValue = line.get(ekey);

            if (eValue instanceof Collection<?>)
            {
                vecNotFolderedConstants.addElement(ekey);
                isTableFoldered = true;
            }
        }
    }

    public void setParametersList(Collection<ComputeParameter> parameters)
    {
        this.parameters = parameters;
    }

    public boolean isParameterTemplateSimple(String parTemplate)
    {
        if (!isTableFoldered)
            return true;
        else
        {
            //int pos
        }
        return false;
    }

    public String weaveFreemarker(String strTemplate, Hashtable<String, String> values)
    {
        Configuration cfg = new Configuration();
        //cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        Template t = null;
        StringWriter out = new StringWriter();
        try
        {
            t = new Template("name", new StringReader(strTemplate), cfg);
            t.process(values, out);
        }
        catch (TemplateException e)
        {
            //e.printStackTrace();
        }
        catch (IOException e)
        {
            //e.printStackTrace();
        }

        return out.toString();
    }
}
