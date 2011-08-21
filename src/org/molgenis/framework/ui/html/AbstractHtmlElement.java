package org.molgenis.framework.ui.html;

import java.text.ParseException;
import java.util.UUID;

import org.molgenis.util.Tuple;

public abstract class AbstractHtmlElement implements HtmlElement
{
	/** unique id of this input */
	private String id;

	/** The css class of this input. */
	private String clazz;

	/** Constructor using id only */
	public AbstractHtmlElement(String id)
	{
		this.id = id;
	}

	/** No-arg constructor. Will autogenerate id */
	public AbstractHtmlElement()
	{
		this.id = UUID.randomUUID().toString().replace("-", "");
	}
	
	@Override
	public void set(Tuple params) throws HtmlInputException
	{
		this.setId(params.getString(ID));
		this.setClazz(params.getString(CLASS));
	}
	
	@Override
	public abstract String render();

	@Override
	public abstract String render(Tuple params) throws ParseException,
			HtmlInputException;

	@Override
	public String getCustomHtmlHeaders()
	{
		return null;
	}

	//PROPERTIES
	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public void setId(String id)
	{
		this.id = id;
	}

	@Override
	public String getClazz()
	{
		return this.clazz;
	}

	@Override
	public void setClazz(String clazz)
	{
		this.clazz = clazz;
	}
}
