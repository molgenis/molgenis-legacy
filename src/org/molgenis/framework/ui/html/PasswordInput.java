package org.molgenis.framework.ui.html;

/**
 * Input for passwords. The password will be made unreadible.
 */
public class PasswordInput extends HtmlInput
{
	public PasswordInput(String name)
	{
		this(name,null);
	}
	
	public PasswordInput(String name, Object value)
	{
		super( name, value );
	}

	@Override
	public String toHtml()
	{
		String readonly = (this.isReadonly()) ? "readonly class=\"readonly\" " : ""; 		

		if (this.isHidden())
		{
			StringInput input = new StringInput(this.getName(), this.getValue());
			input.setHidden(true);
			return input.toHtml();
		}	
			
		return "<input type=\"password\" id=\"" + getId() + "\" name=\"" + getName() + 
			"\" value=\"" + getValue() + "\" " + readonly + tabIndex + " />";
	}

}
