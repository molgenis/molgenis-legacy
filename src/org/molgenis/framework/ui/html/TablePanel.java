package org.molgenis.framework.ui.html;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.molgenis.util.Tuple;

public class TablePanel extends HtmlInput
{
	LinkedHashMap<String, HtmlInput> inputs = new LinkedHashMap<String, HtmlInput>();

	public TablePanel()
	{
		this(null, null);
	}
	
	public TablePanel(String name, String label)
	{
		super(name, label);
		this.setLabel(label);
	}
	
	/**
	 * Adds the given inputs to the TablePanel.
	 * 
	 * @param HtmlInput ... inputs
	 */
	public void add(HtmlInput ... inputs)
	{
		for(HtmlInput input: inputs) {
			this.inputs.put(input.getName(), input);
		}
	}
	
	/**
	 * Removes the given inputs from the TablePanel.
	 * 
	 * @param HtmlInput ... inputs
	 */
	public void remove(HtmlInput ... inputs)
	{
		for(HtmlInput input: inputs) {
			this.inputs.remove(input.getName());
		}
	}

	public HtmlInput get(String name)
	{
		return this.inputs.get(name);
	}

	@Override
	/**
	 * Each input is rendered with a label and in its own div to enable scripting.
	 */
	public String toHtml()
	{
		String result = "";
		for (HtmlInput i : this.inputs.values())
		{
			result += "<div";
			if (i.getId() != null) {
				result += (" id=\"" + i.getId() + "\"");
			}
			if (i.isHidden()) {
				result += " style=\"display:none\"";
			}
			result += "><label for=\"" + i.getName() + "\">" + i.getLabel()
					+ "</label>" + i.toHtml() + (!i.isNillable() ? " *" : "") + "</div>";
		}
		return result;
	}
	
	/**
	 * Tries to set the values of all the inputs in the TablePanel to the
	 * corresponding ones in the request tuple.
	 * 
	 * @param Tuple request
	 */
	public void setValuesFromRequest(Tuple request) {
		String inputName;
		Object object;
		for (HtmlInput input : this.inputs.values()) {
			inputName = input.getName();
			object = request.getObject(inputName);
			if (object != null) {
				input.setValue(object);
			}
		}
	}

}
