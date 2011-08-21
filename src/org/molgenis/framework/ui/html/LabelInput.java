package org.molgenis.framework.ui.html;

import java.text.ParseException;

import org.molgenis.util.Tuple;

/**
 * Input that only has a label but will not show an input (i.e., doesn't have a
 * value).
 */
public class LabelInput extends AbstractHtmlElement
{
	String value;

	public LabelInput(String value)
	{
		super(null);
		this.value = value;
	}
	
	public LabelInput(String id, String value)
	{
		super(id);
		this.value = value;
	}

	public LabelInput(Tuple params) throws HtmlInputException
	{
		this(params.getString("value"));
	}

	@Override
	public String render()
	{
		return "<label>" + value + "</label>";
	}

	@Override
	public String render(Tuple params) throws ParseException,
			HtmlInputException
	{
		return new LabelInput(params).render();
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		return null;
	}
}
