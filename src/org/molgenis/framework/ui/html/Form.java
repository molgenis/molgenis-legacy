package org.molgenis.framework.ui.html;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import org.molgenis.util.Tuple;

/**
 * Not used???
 */
public class Form extends LinkedHashMap<String, Input>
{
	private static final long serialVersionUID = -8565170009471766957L;

	public void add(Input i)
	{
		this.put(i.getName().toLowerCase(), i);
	}

	public void addAll(List<HtmlInput> inputs)
	{
		for (Input i : inputs)
			this.add(i);
	}

	public void addAll(Vector<HtmlInput> inputs)
	{
		for (Input i : inputs)
			this.add(i);
	}

	@Override
	public Input get(Object key)
	{
		if (key instanceof String) return super.get(((String) key)
				.toLowerCase());
		return super.get(key);
	}

	public void setAll(Tuple t)
	{
		for (String key : t.getFields())
		{
			// only sets known fields!
			if (this.containsKey(key)) this.get(key).setValue(t.getObject(key));
		}
	}

	public List<HtmlInput> getInputs()
	{
		List<HtmlInput> result = new ArrayList<HtmlInput>();
		for (String key : this.keySet())
		{
			result.add((HtmlInput) this.get(key));
		}
		return result;
	}
}
