package org.molgenis.framework.ui.html;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TablePanel extends HtmlInput
{
	LinkedHashMap<String, HtmlInput> inputs = new LinkedHashMap<String, HtmlInput>();

	public TablePanel(String name)
	{
		super(name,null);
	}
	
	public void add(HtmlInput input)
	{
		this.inputs.put(input.getName(), input);
	}

	public HtmlInput get(String name)
	{
		return this.inputs.get(name);
	}

	@Override
	/**
	 * Each input will rendered with a label and in its own div to enable scripting.
	 */
	public String toHtml()
	{
		String result = "<table>";
		for (HtmlInput i : this.inputs.values())
		{
			result += "<tr><td><label for=\"" + i.getName() + "\">" + i.getLabel()
					+ "</label></td><td>" + i.toHtml() + (!i.isNillable() ? " *" : "") + "</td></tr>";
		}
		result += "</table>";
		return result;
	}

}
