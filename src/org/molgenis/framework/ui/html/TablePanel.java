package org.molgenis.framework.ui.html;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.molgenis.util.Tuple;

/*
 * Provides a panel to order your inputs.
 * TODO: this is now exactly the same as the DivPanel; we need to rewrite this
 * so that it uses html tables to order the inputs.
 */
public class TablePanel extends HtmlWidget
{
	LinkedHashMap<String, HtmlInput<?>> inputs = new LinkedHashMap<String, HtmlInput<?>>();

	public TablePanel()
	{
		this("", "");
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
	public void add(HtmlInput<?> ... inputs)
	{
		for(HtmlInput<?> input: inputs) {
			this.inputs.put(input.getName(), input);
		}
	}
	
	/**
	 * Removes the given inputs from the TablePanel.
	 * 
	 * @param HtmlInput ... inputs
	 */
	public void remove(HtmlInput<?> ... inputs)
	{
		for(HtmlInput<?> input: inputs) {
			this.inputs.remove(input.getName());
		}
	}

	@SuppressWarnings("unchecked")
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
		for (HtmlInput<?> i : this.inputs.values())
		{
			result += "<div style=\"clear:both; ";
			if (i.isHidden()) {
				result += "display:none\"";
			} else {
				result += "display:block\"";
			}
			if (i.getId() != null) {
				result += (" id=\"" + i.getId() + "\"");
			}
			result += "><label style=\"width:16em;float:left;\" for=\"" + i.getName() + "\">" + i.getLabel()
					+ "</label>" + i.toHtml() + (!i.isNillable() ? " *" : "") + "</div>";
		}
		return result;
	}
	
	/**
	 * Tries to set the values of all the inputs in the TablePanel to the
	 * corresponding ones in the request tuple.
	 * 
	 * @param request
	 */
	@SuppressWarnings("unchecked")
	public void setValuesFromRequest(Tuple request) {
		Object object;
		List<HtmlInput<?>> inputList = new ArrayList<HtmlInput<?>>();
		fillList(inputList, this);
		for (@SuppressWarnings("rawtypes") HtmlInput input : inputList) {
			object = request.getObject(input.getName());
			if (object != null) {
				input.setValue(object);
			}
		}
	}
	
	/**
	 * Add to 'inputList' all the inputs that are part of the 'startInput' TablePanel.
	 * Fully recursive, so nested TablePanels are also taken into account.
	 * 
	 * @param inputList
	 * @param startInput
	 */
	private void fillList(List<HtmlInput<?>> inputList, TablePanel startInput) {
		for (HtmlInput<?> input : startInput.inputs.values()) {
			if (input instanceof TablePanel || input instanceof RepeatingPanel) {
				fillList(inputList, (TablePanel)input);
			} else {
				inputList.add(input);
			}
		}
	}
	
	@Override
	public String toHtml(Tuple params) throws ParseException,
			HtmlInputException
	{
		//TODO?
		throw new UnsupportedOperationException();
	}
}
