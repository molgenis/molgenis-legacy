package org.molgenis.framework.ui.html;

import org.molgenis.util.tuple.Tuple;

/**
 * Input for string data. Renders as a <code>textarea</code>.
 */
public class StringInput extends ValidatingInput<String>
{
	public StringInput(Tuple t) throws HtmlInputException
	{
		super(t);
	}

	public StringInput(String name, String label, String value, boolean nillable, boolean readonly)
	{
		this(name, value);
		this.setLabel(label);
		this.setNillable(nillable);
		this.setReadonly(readonly);
	}

	public StringInput(String name)
	{
		this(name, null);
	}

	public StringInput(String name, String value)
	{
		super(name, value);
		this.setMinHeight(1);
		this.setMaxHeight(1);
	}

	public StringInput()
	{
		// TODO Auto-generated constructor stub
	}

	// public String toHtml()
	// {
	// String readonly = (this.isReadonly()) ? "readonly class=\"readonly\" "
	// : "";
	//
	// if (this.isHidden())
	// {
	// if (this.uiToolkit == UiToolkit.ORIGINAL
	// || this.uiToolkit == UiToolkit.JQUERY)
	// {
	// return "<input name=\"" + this.getName()
	// + "\"type=\"hidden\" value=\"" + this.getValue()
	// + "\"/>";
	// }
	// else if (this.uiToolkit == UiToolkit.DOJO)
	// {
	// return "<input name=\""
	// + this.getName()
	// + "\"id="
	// + this.getId()
	// + "\""
	// + "\"type=\"hidden\"  dojoType=\"dijit.form.TextBox\" value=\""
	// + this.getValue() + "\"/>";
	//
	// }
	// }
	// String validate = this.isNillable() || this.isReadonly() ? "" :
	// " required";
	// String cssClass = this.uiToolkit == UiToolkit.JQUERY ?
	// " class=\""+(this.isReadonly() ? "readonly ":
	// "")+"text ui-widget-content ui-corner-all"
	// + validate + "\""
	// : "";
	// String result = "<textarea "
	// + cssClass
	// + " id=\""
	// + this.getId()
	// + "\" name=\""
	// + this.getName()
	// + "\"  "
	// + (this.getSize() != null && this.getSize() > 0 ?
	// "onfocus=\"startcounter(this, "
	// + getSize() + ")\" onblur=\"endcounter()\""
	// : "") + " cols=\"" + this.getWidth() + "\" rows=\""
	// + this.getHeight() + "\" " + readonly + " >" + this.getValue()
	// + "</textarea>";
	// //if (this.library == Library.DEFAULT)
	// result += "<script>showTextInput(document.getElementById('"
	// + this.getId()
	// + "'),"
	// + this.getMinHeight()
	// + ","
	// + this.getMaxHeight() + ");</script>";
	// // if (this.library == Library.JQUERY) result += "<script>$(\"#"
	// // + getName() + "\").autoGrow();</script>";
	// return result;
	// }

	// public int width;
	//
	// public int getWidth()
	// {
	// return width;
	// }
	//
	// public void setWidth(int width)
	// {
	// this.width = width;
	// }
	//
	// public int height;
	//
	// public int getHeight()
	// {
	// return height;
	// }
	//
	// public void setHeight(int height)
	// {
	// this.height = height;
	// }
	//
	// private int minHeight = 1;
	//
	// public int getMinHeight()
	// {
	// return minHeight;
	// }
	//
	// public void setMinHeight(int minHeight)
	// {
	// this.minHeight = minHeight;
	// }
	//
	// private int maxHeight = 25;
	//
	// public int getMaxHeight()
	// {
	// return maxHeight;
	// }
	//
	// public void setMaxHeight(int maxHeight)
	// {
	// this.maxHeight = maxHeight;
	// }

	// public StringInput(String name)
	// {
	// this(name, null);
	// }
	//
	// public StringInput(String name, Object value)
	// {
	// super(name, value);
	// }
	//
	// @Override
	// public String toHtml()
	// {
	// String classAtt = (this.getClazz() != null ? this.getClazz() : "");
	// classAtt += (this.isReadonly()) ? " readonly " : "";
	// if (!this.isNillable() && INJECT_JQUERY)
	// {
	// if(classAtt.length()>0) classAtt += " ";
	// classAtt += "required";
	// }
	// // 'disabled' doesn't send the value. We need the value if it is
	// // key...therefore we use 'readonly'.
	//
	// if (this.isHidden())
	// {
	// return "<input type=\"hidden\" id=\"" + getId() + "\" name=\"" +
	// getName() + "\" value=\"" + getValue()
	// + "\" />";
	// }
	//
	// String attributes = "";
	// if (INJECT_JQUERY && getSize() != null) attributes += " maxlength=\"" +
	// getSize() + "\"";
	// if (INJECT_JQUERY && getStyle() != null) attributes += " style=\"" +
	// getStyle() + "\"";
	//
	// return "<input type=\"text\" id=\"" + getId() + "\" class=\"" + classAtt
	// + "\" name=\"" + getName()
	// + "\" value=\"" + getValue() + "\" " + attributes + tabIndex + " />";
	// }

	@Override
	public String toHtml(Tuple params) throws HtmlInputException
	{
		return new StringInput(params).render();
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		return "";
	}
}
