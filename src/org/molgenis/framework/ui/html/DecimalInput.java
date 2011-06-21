package org.molgenis.framework.ui.html;

/**
 * Input for decimal data.
 */
public class DecimalInput extends StringInput
{
	public DecimalInput(String name, Object value)
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
}
