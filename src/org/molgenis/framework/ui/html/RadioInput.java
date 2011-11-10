package org.molgenis.framework.ui.html;

import java.text.ParseException;
import java.util.List;
import java.util.Vector;

import org.molgenis.util.Tuple;
import org.molgenis.util.ValueLabel;

/**
 * Input for radio button data.
 */
public class RadioInput extends OptionInput<String>
{
	/**
	 * Construct a radio button input with a name, a label and a description, as
	 * well as one or more options and a selected value.
	 * 
	 * @param name
	 * @param label
	 * @param description
	 * @param options
	 * @param value
	 */
	@Deprecated
	public RadioInput(String name, String label, String description,
			Vector<ValueLabel> options, String value)
	{
		super(name, value);
		super.setLabel(label);
		super.setDescription(description);
		super.setOptions(options);
		this.setReadonly(false);
	}

	public RadioInput(String name, String label, String value,
			Boolean nillable, Boolean readonly, String description,
			List<String> options, List<String> option_labels)
			throws HtmlInputException
	{
		super(name, label, value, nillable, readonly, description, options,
				option_labels);
	}

	public RadioInput(Tuple t) throws HtmlInputException
	{
		super(t);
	}

	public String toHtml()
	{
		if (this.isHidden())
		{
			StringInput input = new StringInput(this.getName(), this.getValue());
			input.setHidden(true);
			return input.toHtml();
		}

		String optionString = "";
		String readonly = (isReadonly() ? " class=\"readonly\" readonly " : "");
		String checked;
		String selectedValueLabel = this.getValue();

		if (!(getOptions().isEmpty()))
		{
			for (ValueLabel option : getOptions())
			{
				String optionLabel = option.getLabel();
				checked = (selectedValueLabel.equals(optionLabel)) ? " checked " : "";
				optionString += "<input id=\"" + this.getId()
						+ "\" type=\"radio\" " + readonly + checked
						+ " name=\"" + this.getName() + "\" value=\""
						+ option.getValue() + "\">" + option.getLabel()
						+ "<br />";
			}
			// remove trailing <br />
			optionString = optionString.substring(0, optionString.length() - 6);
		}
		else
		{
			checked = this.getValue().equals(this.getName()) ? " checked " : "";
			optionString += "<input id=\"" + this.getId()
					+ "\" type=\"radio\" " + readonly + checked + " name=\""
					+ this.getName() + "\">" + this.getLabel();
		}

		return optionString;
	}

	@Override
	public String toHtml(Tuple params) throws ParseException,
			HtmlInputException
	{
		return new RadioInput(params).render();
	}

}
