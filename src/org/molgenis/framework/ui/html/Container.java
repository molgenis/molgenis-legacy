package org.molgenis.framework.ui.html;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import org.molgenis.framework.ui.ScreenView;
import org.molgenis.util.Tuple;

/**
 * This class functions as the holder, or container, of all UI components and elements within one plugin. All "pieces"
 * of your UI puzzle should be located within a Container.
 */
public class Container extends LinkedHashMap<String, Input<?>> implements ScreenView
{
	private static final long serialVersionUID = -8565170009471766957L;

	public void add(Input<?> i)
	{
		this.put(i.getName().toLowerCase(), i);
	}

	public void addAll(List<HtmlInput<?>> inputs)
	{
		for (Input<?> i : inputs)
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

	/**
	 * Tries to set the values of the inputs within this Container
	 * using parameters in the request with the same names.
	 * 
	 * @param t The tuple used to set the values
	 */
	public void setAll(Tuple t)
	{
		for (String key : t.getFields())
		{
			// only sets known fields!
			if (this.containsKey(key)) {
				this.get(key).setValue(t.getObject(key));
			}
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
	
	public String toHtml() {
		String returnString = "";
		for (HtmlInput i : this.getInputs()) {
			returnString += i.toHtml();
		}
		return returnString;
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		return null;
	}

	@Override
	public String render()
	{
		return this.toHtml();
	}
}
