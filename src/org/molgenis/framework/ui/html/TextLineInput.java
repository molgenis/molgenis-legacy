package org.molgenis.framework.ui.html;

import java.text.ParseException;

import org.molgenis.util.Tuple;

/**
 * Input for string data. Renders as a normal <code>input</code>.
 */
public class TextLineInput<E> extends HtmlInput<E>
{
	public TextLineInput(String name)
	{
		this(name, null);
	}
	
	@Deprecated
	public TextLineInput(String name, String label, E value, boolean nillable, boolean readonly)
	{
		this(name,label,value,nillable,readonly,null);
	}
	
	public TextLineInput(String name, String label, E value, boolean nillable, boolean readonly, String description)
	{
		super(name,label,value,nillable,readonly,description);
	}

	public TextLineInput(String name, E value)
	{
		super(name, value);
	}
	
	public TextLineInput(Tuple t) throws HtmlInputException
	{
		super(t);
	}

	protected TextLineInput()
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toHtml()
	{
		String classAtt = (this.getClazz() != null ? this.getClazz() : "");
		classAtt += (this.isReadonly()) ? " readonly " : "";
		if (!this.isNillable() && INJECT_JQUERY)
		{
			if(classAtt.length()>0) classAtt += " ";
			classAtt += "required";
		}
		// 'disabled' doesn't send the value. We need the value if it is
		// key...therefore we use 'readonly'.

		if (this.isHidden())
		{
			return "<input type=\"hidden\" id=\"" + getId() + "\" name=\"" + getName() + "\" value=\"" + getValue() + "\">";
		}

		String attributes = "";
		if (INJECT_JQUERY && getSize() != null) attributes += " maxlength=\"" + getSize() + "\"";
		if (INJECT_JQUERY && getStyle() != null) attributes += " style=\"" + getStyle() + "\"";

		return "<input type=\"text\" id=\"" + getId() + "\" class=\"" + classAtt + "\" name=\"" + getName()
				+ "\" value=\"" + getValue() + "\" " + attributes + tabIndex + " />";
	}

	@Override
	public String toHtml(Tuple params) throws ParseException,
			HtmlInputException
	{
		return new TextLineInput(params).render();
	}
}
