package org.molgenis.framework.ui.html;

/**
 * Input for Long integer data.
 */
public class LongInput extends TextLineInput<Long>
{

	public LongInput(String name, Long value)
	{
		super (name, value);
	}
	
	public LongInput(String name)
	{
		this (name, null);
	}

}
