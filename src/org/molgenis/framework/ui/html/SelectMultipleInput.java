/**
 * File: invengine.screen.form.SelectInput <br>
 * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved <br>
 * Changelog:
 * <ul>
 * <li> 2006-03-07, 1.0.0, DI Matthijssen
 * <li> 2006-05-14; 1.1.0; MA Swertz integration into Inveninge (and major
 * rewrite)
 * <li> 2006-05-14; 1.2.0; RA Scheltema major rewrite + cleanup
 * </ul>
 */

package org.molgenis.framework.ui.html;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.molgenis.util.ValueLabel;

/**
 * Input for multiple references. This means that a user can select multiple items from a selection.
 */
public class SelectMultipleInput extends HtmlInput
{
	private List<ValueLabel> options = new Vector<ValueLabel>();

	// constructor(s)
	public SelectMultipleInput(String name, Object value)
	{
		super(name, value);
	}

	@Override
	public String toHtml()
	{
		String readonly = ( isReadonly() ? "readonly " : "");

		if (this.isHidden())
		{
			StringInput input = new StringInput(this.getName(), this.getValue());
			input.setLabel(this.getLabel());
			input.setDescription(this.getDescription());			
			input.setHidden(true);
			return input.toHtml();
		}

		StringBuffer optionsHtml = new StringBuffer();
		List<?> values = (List<?>)super.getObject();
		if(values == null) values = new ArrayList<Object>();
		List<String> stringValues = new ArrayList<String>();
		for(Object v: values)
		{
			stringValues.add(v.toString());
		}
		
		for (ValueLabel choice : options)
		{
			if (stringValues.contains(choice.getValue().toString()))
			{
				optionsHtml.append("\t<option selected value=\"" + choice.getValue() + "\">" + choice.getLabel() + "</option>\n");
			}
			else if (!this.isReadonly())
			{
				optionsHtml.append("\t<option value=\"" + choice.getValue() + "\">" + choice.getLabel() + "</option>\n");
			}
		}
		if (super.getValue().toString().equals(""))
		{
			optionsHtml.append("\t<option selected value=\"\"></option>\n");
			// empty option
		}
		else if (!this.isReadonly())
		{
			optionsHtml.append("\t<option value=\"\"></option>\n");
			// empty option
		}
		return "<select id=\""+this.getId()+"\" multiple name=\"" + this.getName() + "\" " + readonly + " style=\""+this.getStyle()+"\">\n" + optionsHtml.toString() + "</select>\n";
	}

	@Override
	/**
	 * Note, this returns the option label, not its value!
	 */
	public String getValue()
	{
		StringBuffer result = new StringBuffer();
		List<?> values = (List<?>)super.getObject();
		if(values == null) values = new ArrayList<Object>();
		List<String> stringValues = new ArrayList<String>();
		
		for(Object v: values)
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
		if(result != null && !"".equals(result)  && optionstring.lastIndexOf(",") >= 0 )
			return optionstring.substring(0, optionstring.lastIndexOf(","));
		return optionstring;
	}

	public List<ValueLabel> getChoices()
	{
		return options;
	}

	public void setOptions( ValueLabel... choices )
	{
		this.options = Arrays.asList(choices);
	}

	public void setOptions( List<ValueLabel> choices )
	{
		this.options = choices;
	}

	public void setOptions( String... choices )
	{
		List<ValueLabel> choicePairs = new ArrayList<ValueLabel>();
		for (String choice : choices)
		{
			choicePairs.add(new ValueLabel(choice, choice));
		}
		this.setOptions(choicePairs);
	}
}
