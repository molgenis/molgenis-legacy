package org.molgenis.framework.ui.html;

import java.util.Vector;
import org.molgenis.util.ValueLabel;

/**
 * Input for radio button data.
 */
public class RadioInput extends HtmlInput
{
	private Vector<ValueLabel> options = new Vector<ValueLabel>();
	
	/**
	 * Construct a radio button input with a name, a label and a description, as well as one or more options and
	 * a selected value.
	 * 
	 * @param name
	 * @param label
	 * @param description
	 * @param options
	 * @param value
	 */
	public RadioInput(String name, String label, String description, Vector<ValueLabel> options, String value)
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
				checked = this.getValue().equals(option.getValue().toString()) ? " checked " : "";
				optionString.append("<input id=\"" + this.getId() + "\" type=\"radio\" " + readonly + checked + 
						" name=\"" + this.getName() + "\" value=\"" + option.getValue() + "\">" + option.getLabel() + 
						"<br />\n");
			}			
		}
		else {
			checked = this.getValue().equals(this.getName()) ? " checked " : "";
			optionString.append("<input id=\"" + this.getId() + "\" type=\"radio\" " + readonly + checked + 
					" name=\"" +  this.getName() + "\">" + this.getLabel());		
		}
		
		return optionString.toString();
	}

}
