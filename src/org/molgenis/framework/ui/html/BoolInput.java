package org.molgenis.framework.ui.html;

/**
 * Input for yes/no.
 */
public class BoolInput extends HtmlInput
{
	public enum Option
	{
		READONLY;
	}

	public BoolInput(String name, Object value)
	{
		super(name, value);
	}

	@Override
	public String toHtml()
	{
		//String readonly = ( isReadonly() ? " readonly " : "");

		if( this.isHidden() )
		{
			StringInput input = new StringInput(this.getName(), this.getValue());
			input.setLabel(this.getLabel());
			input.setDescription(this.getDescription());
			input.setHidden(true);
			return input.toHtml();
		}
		
		if(isReadonly())
			return "<select class=\"readonly\" id=\"" + this.getId() + "\" name=\"" + this.getName() + "\" readonly=\"readonly\">" + "<option value=\"" + getValue() + "\" selected>"+getValue()+"</option></select>\n";
		else
			return "<select id=\"" + this.getId() + "\" name=\"" + this.getName() + "\">" + "<option value=\"true\"" + (getValue().equals("yes") ? "selected" : "") + ">yes</option>" + "<option value=\"false\"" + (getValue().equals("no") ? "selected" : "") + ">no</option>" + "<option value=\"\"" + (getValue().equals("") ? "selected" : "") + "></option>" + "</select>\n";
	}

	public String getValue()
	{
		if( super.getValue().equals("true") )
			return "yes";
		if( super.getValue().equals("false") )
			return "no";
		return "";
	}
}