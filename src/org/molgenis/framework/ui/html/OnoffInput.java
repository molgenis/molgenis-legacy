package org.molgenis.framework.ui.html;


/**
 * Input for checkbox data (yes/no).
 */
public class OnoffInput extends HtmlInput
{
	private String onValue = "on";
	// constructor(s)
	/**
	 * ...
	 * 
	 * @param name
	 * @param value
	 */
	public OnoffInput(String name, Object value)
	{
		super(name, value);
		this.setReadonly(false);			
	}

	// HtmlInput overloads
	/**
	 * 
	 */
	public String toHtml()
	{
		if (this.isHidden())
		{
			StringInput input = new StringInput(this.getName(), this.getValue());
			input.setHidden(true);
			return input.toHtml();
		}	
		String readonly = ( isReadonly() ? "readonly " : "");
		String checked = (getObject() != null && Boolean.valueOf(getObject().toString())) ? "checked" : "";

		String html = "<input id=\""+this.getId()+"\" type=\"checkbox\" " + readonly + checked + " name=\"" +  this.getName() + " \" value=\""+this.onValue+"\"/>";	
		
		return html;
	}
	
	public String getHtmlValue()
	{
		String value = (getObject() != null && getObject().equals(1)) ? "yes" : "no";
		return value;
	}

	public void setOnValue(String value)
	{
		this.onValue = value;
	}
}
