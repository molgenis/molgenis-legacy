package org.molgenis.framework.ui.html;

import org.molgenis.util.Tuple;

/**
 * Input for passwords. The password will be made unreadible. TODO: sent encoded.
 */
public class PasswordInput extends HtmlInput<String>
{
	public PasswordInput(String name)
	{
		this(name,null);
	}
	
	public PasswordInput(String name, String value)
	{
		super( name, value );
	}
	
	public PasswordInput(String name, String label, String value, Boolean readonly, Boolean nillable, String description)
	{
		super(name,label,value,readonly,nillable, description);
	}
	
	public PasswordInput(Tuple t) throws HtmlInputException
	{
		super(t);
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

	@Override
	public String toHtml(Tuple params) throws HtmlInputException
	{
		return new PasswordInput(params).render();
	}

}
