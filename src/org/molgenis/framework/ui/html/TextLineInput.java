package org.molgenis.framework.ui.html;

/**
 * Input for string data. Renders as a normal <code>input</code>.
 */
public class TextLineInput extends HtmlInput
{

	public TextLineInput(String name)
	{
		this(name, null);
	}

	public TextLineInput(String name, Object value)
	{
		super(name, value);
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
}
