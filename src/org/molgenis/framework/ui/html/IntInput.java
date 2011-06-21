package org.molgenis.framework.ui.html;

/**
 * Input for integer data.
 */
public class IntInput extends TextLineInput
{
	
	public IntInput(String name)
	{
		super(name,null);
	}

	public IntInput(String name, Object value)
	{
		super( name, value );
	}

	public IntInput(String name, String label, Integer value,
			boolean nillable, boolean readonly)
	{
		super(name,label, value == null ? null : value.toString(),nillable,readonly);
	}
}
