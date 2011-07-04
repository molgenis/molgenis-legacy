package org.molgenis.framework.ui.html;

import java.text.ParseException;

import org.molgenis.util.Tuple;

/**
 * Input that only has a label but will not show an input (i.e., doesn't have a value).
 */
public class LabelInput extends HtmlInput<String>
{
	//very confusing, would like to set label without id here
	@Deprecated
	public LabelInput(String name)
	{
		super(name,null);
	}
	
	public LabelInput(String name, String value)
	{
		super(name,value);
	}

	public LabelInput(Tuple params) throws HtmlInputException
	{
		super(params);
	}

	@Override
	public String toHtml()
	{
		return "&nbsp;";
	}

	@Override
	public String toHtml(Tuple params) throws ParseException,
			HtmlInputException
	{
		return new LabelInput(params).render();
	}
}
