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

// jdk
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.molgenis.util.Entity;
import org.molgenis.util.ValueLabel;

/**
 * Input for choosing from an pre-defined series of options. Each option is of
 * class ValueLabel to define values and labels. The options will be shown as
 * dropdown select box.
 */
public class SelectInput extends HtmlInput
{
	private List<ValueLabel> options = new Vector<ValueLabel>();
	private String targetfield;
	private String onchange;

	public SelectInput(String name, String label)
	{
		this(name);
		this.setLabel(label);
	}

	public SelectInput(String name)
	{
		super(name, null);
	}

	public SelectInput(String name, Object value)
	{
		super(name, value);
	}

	@Override
	public String toHtml()
	{
		String readonly = (this.isReadonly()) ? " readonly class=\"readonly\" "
				: "";

		String onchange = (this.onchange != null) ? " onchange=\""
				+ this.onchange + "\"" : "";

		if (this.isHidden())
		{
			StringInput input = new StringInput(this.getName(), super
					.getValue());
			input.setLabel(this.getLabel());
			input.setDescription(this.getDescription());
			input.setHidden(true);
			return input.toHtml();
		}

		StringBuffer optionsHtml = new StringBuffer();
		
		if ((!this.isReadonly() && this.isNillable()) ||
			(super.getValue().toString().equals("") && this.isNillable()))
		{
			// start with empty option
			optionsHtml.append("\t<option value=\"\"></option>\n");
		}
		for (ValueLabel choice : options)
		{
			if (super.getValue().equals(choice.getValue().toString()))
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

		return "<select class=\"" + this.getClazz() + "\" id=\"" + this.getId()
				+ "\" name=\"" + this.getName() + "\" " + readonly + onchange
				+ ">\n" + optionsHtml.toString() + "</select>\n";
	}

	@Override
	/**
	 * Returns the label of the selected value.
	 */
	public String getValue()
	{
		for (ValueLabel choice : options)
		{
			if (super.getValue().equals(choice.getValue().toString()))
			{
				return choice.getLabel().toString();
			}
		}
		return "";
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

	public void setOptions(String... choices)
	{
		List<ValueLabel> choicePairs = new ArrayList<ValueLabel>();
		for (String choice : choices)
		{
			choicePairs.add(new ValueLabel(choice, choice));
		}
		this.setOptions(choicePairs);
	}
	
	/**
	 * Set the options for this selectbox using a list of strings
	 * 
	 * @param choices List of Strings representing the options for this selectbox
	 */
	public void setOptionsFromStringList(List<String> choices)
	{
		List<ValueLabel> choicePairs = new ArrayList<ValueLabel>();
		for (String choice : choices)
		{
			choicePairs.add(new ValueLabel(choice, choice));
		}
		this.setOptions(choicePairs);
	}

	public String getTargetfield()
	{
		return targetfield;
	}

	public void setTargetfield(String targetfield)
	{
		this.targetfield = targetfield;
	}

	public String getOnchange()
	{
		return this.onchange;
	}

	public void setOnchange(String onchange)
	{
		this.onchange = onchange;
	}

	public void addOption(Object value, Object label)
	{
		this.getChoices().add(
				new ValueLabel(value.toString(), label.toString()));
	}

	/** Set the options for the input
	 * 
	 * @param entities list of entities to add as options (values)
	 * @param valueField field used for identification
	 * @param labelField field used for label (what shows on the screen)
	 */
	public void setOptions(List<? extends Entity> entities, String valueField,
			String labelField)
	{
		// clear list
		this.getChoices().clear();

		// add new values and labels
		for (Entity e : entities)
		{
			this.addOption(e.get(valueField), e.get(labelField));
		}
	}
}
