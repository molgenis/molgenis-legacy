package org.molgenis.framework.ui.html;

/**
 * Input that should be hidden from view. Used for hidden parameters that users don't want/need to see.
 */
public class HiddenInput extends StringInput
{

	public HiddenInput(String name, String value)
	{
		super(name, value);
		this.setHidden(true);
	}

}
