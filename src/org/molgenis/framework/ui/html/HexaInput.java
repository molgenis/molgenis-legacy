package org.molgenis.framework.ui.html;

/**
 * Input for hexadecimal data fields.
 */
public class HexaInput extends StringInput
{
	public HexaInput(String name)
	{
		this(name, null);
	}	
	
	public HexaInput(String name, Object value)
	{
		super(name, value);
	}
}
