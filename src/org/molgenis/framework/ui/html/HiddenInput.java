package org.molgenis.framework.ui.html;

public class HiddenInput extends StringInput
{

	public HiddenInput(String name, Object value)
	{
		super(name, value);
		this.setHidden(true);
	}

}
