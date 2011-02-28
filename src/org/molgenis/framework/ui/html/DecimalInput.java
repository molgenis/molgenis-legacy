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
}
