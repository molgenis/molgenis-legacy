package org.molgenis.framework.ui.html;

import org.molgenis.util.Tuple;

/**
 * Input for decimal data.
 */
public class DecimalInput extends TextLineInput<Double>
{
	public DecimalInput(String name, Double value)
	{
		super (name, value);
	}
	
	public DecimalInput(String name)
	{
		this (name, null);
	}

	public DecimalInput(String name, String label, Double value,
			boolean nillable, boolean readonly)
	{
		super(name,value);
		this.setLabel(label);
		this.setNillable(nillable);
		this.setReadonly(readonly);
	}
	
	public DecimalInput(Tuple params) throws HtmlInputException
	{
		set(params);
	}

	protected DecimalInput()
	{
	}
}
