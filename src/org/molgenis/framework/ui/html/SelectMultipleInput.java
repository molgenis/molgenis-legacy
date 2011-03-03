/**
 * File: invengine.screen.form.SelectInput <br>
 * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved <br>
 * Changelog:
 * <ul>
 * <li>2006-03-07, 1.0.0, DI Matthijssen
 * <li>2006-05-14; 1.1.0; MA Swertz integration into Inveninge (and major
 * rewrite)
 * <li>2006-05-14; 1.2.0; RA Scheltema major rewrite + cleanup
 * </ul>
 */

package org.molgenis.framework.ui.html;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.molgenis.util.Entity;
import org.molgenis.util.ValueLabel;

/**
 * Input for multiple select. This means that a user can select multiple items
 * from a predefined selection.
 */
public class SelectMultipleInput extends HtmlInput
{
	private List<ValueLabel> options = new Vector<ValueLabel>();

	/**
	 * 
	 * @param name Name of the Input
	 * @param objects list of selected values
	 */
	public SelectMultipleInput(String name, List<Object> objects)
	{
		super(name, objects);
	}

	@Override
	public String toHtml()
	{
		String readonly = (isReadonly() ? "readonly " : "");

		if (this.isHidden())
		{
			StringInput input = new StringInput(this.getName(), this.getValue());
			input.setLabel(this.getLabel());
			input.setDescription(this.getDescription());
			input.setHidden(true);
			return input.toHtml();
		}

		StringBuffer optionsHtml = new StringBuffer();
		List<?> values = (List<?>) super.getObject();
		if (values == null) values = new ArrayList<Object>();
		List<String> stringValues = new ArrayList<String>();
		for (Object v : values)
		{
			stringValues.add(v.toString());
		}
		if (!this.isReadonly() || super.getValue().toString().equals(""))
		{
			// start with empty option
			optionsHtml.append("\t<option value=\"\"></option>\n");
		}
		for (ValueLabel choice : options)
		{
			if (stringValues.contains(choice.getValue().toString()))
			{
				optionsHtml.append("\t<option selected value=\""
						+ choice.getValue() + "\">" + choice.getLabel()
						+ "</option>\n");
			}
			else if (!this.isReadonly())
			{
				optionsHtml.append("\t<option value=\"" + choice.getValue()
						+ "\">" + choice.getLabel() + "</option>\n");
			}
		}
		return "<select id=\"" + this.getId() + "\" multiple name=\""
				+ this.getName() + "\" " + readonly + " style=\""
				+ this.getStyle() + "\">\n" + optionsHtml.toString()
				+ "</select>\n";
	}

	@Override
	/**
	 * Note, this returns the option label, not its value!
	 */
	public String getValue()
	{
		StringBuffer result = new StringBuffer();
		List<?> values = (List<?>) super.getObject();
		if (values == null) values = new ArrayList<Object>();
		List<String> stringValues = new ArrayList<String>();

		for (Object v : values)
		{
			stringValues.add(v.toString());
		}

		for (ValueLabel choice : options)
		{
			if (stringValues.contains(choice.getValue().toString()))
			{
				result.append(choice.getLabel() + ", ");
			}
		}

		String optionstring = result.toString();
		if (result != null && !"".equals(result)
				&& optionstring.lastIndexOf(",") >= 0) return optionstring
				.substring(0, optionstring.lastIndexOf(","));
		return optionstring;
	}

	public List<ValueLabel> getChoices()
	{
		return options;
	}

	public void setOptions(ValueLabel... choices)
	{
		this.options = Arrays.asList(choices);
	}

	public void setOptions(List<ValueLabel> choices)
	{
		this.options = choices;
	}
	
	/** Set the options for the input
	 * 
	 * @param entities list of entities to add as options (values)
	 * @param valueField field used for identification
	 * @param labelField field used for label (what shows on the screen)
	 */
	public void setOptions(List<Object> entities, String valueField,
			String labelField)
	{
		// clear list
		this.getChoices().clear();

		// add new values and labels
		for (Object e : entities)
		{
			this.addOption(((Entity) e).get(valueField), ((Entity) e).get(labelField));
		}
	}
	
	public void addOption(Object value, Object label)
	{
		this.getChoices().add(
				new ValueLabel(value.toString(), label.toString()));
	}

	public void setOptions(String... choices)
	{
		List<ValueLabel> choicePairs = new ArrayList<ValueLabel>();
		for (String choice : choices)
		{
			choicePairs.add(new ValueLabel(choice, choice));
		}
		this.setOptions(choicePairs);
	}
}
