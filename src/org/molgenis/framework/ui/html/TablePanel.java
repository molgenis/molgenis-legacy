package org.molgenis.framework.ui.html;

import java.util.ArrayList;
import java.util.List;

public class TablePanel extends HtmlInput
{
	List<HtmlInput> inputs = new ArrayList<HtmlInput>();

	public TablePanel(String name)
	{
		super(name,null);
	}
	
	public void add(HtmlInput input)
	{
		inputs.add(input);
	}

	@Override
	/**
	 * Each input will rendered with a label and in its own div to enable scripting.
	 */
	public String toHtml()
	{
		String result = "<table>";
		for (HtmlInput i : inputs)
		{
			result += "<tr><td><label for=\"" + i.getName() + "\">" + i.getLabel()
					+ "</label></td><td>" + i.toHtml()+"</td></tr>";
		}
		result += "</table>";
		return result;
	}

}
