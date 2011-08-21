package org.molgenis.framework.ui.html;

import org.molgenis.util.Tuple;

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

	public HiddenInput()
	{
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String toHtml(Tuple params) throws HtmlInputException
	{
		params.set(HIDDEN,true);
		return super.toHtml(params);
	}

}
