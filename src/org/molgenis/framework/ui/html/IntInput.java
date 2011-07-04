package org.molgenis.framework.ui.html;

import java.text.ParseException;

import org.molgenis.util.Tuple;

/**
 * Input for integer data.
 */
public class IntInput extends TextLineInput<Integer>
{
	
	public IntInput(String name)
	{
		super(name,null);
	}

	public IntInput(String name, Integer value)
	{
		super( name, value );
	}

	public IntInput(String name, String label, Integer value,
			boolean nillable, boolean readonly, String description)
	{
		super(name,label, value, nillable,readonly, description);
	}

	protected IntInput()
	{
		super();
	}
	
	public IntInput(Tuple params) throws HtmlInputException
	{
		super(params);
	}

	@Override
	public String toHtml(Tuple params) throws ParseException,
			HtmlInputException
	{
		return new IntInput(params).render();
	}
}
