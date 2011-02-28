package org.molgenis.framework.ui.html;

/**
 * Input for Long integer data.
 */
public class LongInput extends StringInput
{

	public LongInput(String name, Object value)
	{
		super (name, value);
	}
	
	public LongInput(String name)
	{
		this (name, null);
	}

}
