package org.molgenis.framework.ui.html;

import java.text.ParseException;

import org.molgenis.util.Tuple;

public class CustomHtml extends AbstractHtmlElement implements HtmlElement
{
	String value;
	
	public CustomHtml(String value)
	{
		this.value = value;
	}
	
	@Override
	public String getCustomHtmlHeaders()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String render()
	{
		return value;
	}

	@Override
	public String render(Tuple params) throws ParseException,
			HtmlInputException
	{
		return new CustomHtml(params.getString("value")).render();
	}

	@Override
	public String getId()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
