package org.molgenis.framework.ui.html;

import java.util.Vector;
import org.molgenis.util.ValueLabel;

/**
 * Input for checkbox data.
 */
public class CheckboxInput extends HtmlInput
{
	private Vector<ValueLabel> options = new Vector<ValueLabel>();
	
	/**
	 * Construct a checkbox input with a name, a label and a description, as well as one or more options and
	 * a selected value.
	 * 
	 * @param name
	 * @param label
	 * @param description
	 * @param options
	 * @param value
	 */
	public CheckboxInput(String name, String label, String description, Vector<ValueLabel> options, Vector<String> value)
	{
		super(name, value);
		super.setLabel(label);
		super.setDescription(description);
		this.options = options;
		this.setReadonly(false);			
	}

	public String toHtml()
	{
		if (this.isHidden())
		{
			StringInput input = new StringInput(this.getName(), this.getValue());
			input.setHidden(true);
			return input.toHtml();
		}
		
		StringBuffer optionString = new StringBuffer("");
		String readonly = ( isReadonly() ? " class=\"readonly\" readonly " : "");
		String checked;
		
		if (!(options.isEmpty()))
		{
			for (ValueLabel option : options)
			{
				checked = ( ((Vector<String>)getObject()).contains(option.getValue().toString()) ? " checked " : "");
				optionString.append("<input id=\"" + this.getId() + "\" type=\"checkbox\" " + readonly + checked + 
						" name=\"" + this.getName() + "\" value=\"" + option.getValue() + "\">" + option.getLabel() + 
						"<br />\n");
			}			
		}
		else {
			checked = ( ((Vector<String>)getObject()).contains(this.getName()) ? " checked " : "");
			optionString.append("<input id=\"" + this.getId() + "\" type=\"checkbox\" " + readonly + checked + 
					" name=\"" +  this.getName() + "\">" + this.getLabel());		
		}
		
		return optionString.toString();
	}
	
	@Override
	public String getValue()
	{
		String value = "";
		for (ValueLabel i: options)
		{
			if (((Vector<String>)getObject()).contains(i.getValue()))
			{
				value += i.getLabel() + ", ";
			}
		}
		// remove trailing comma
		if (value.length() > 2) {
			return value.substring(0, value.length() - 2);
		}
		return value;
	}

}
