/**
 * File: TextInput.java <br>
 * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved <br>
 * Changelog:
 * <ul>
 * <li>2006-03-08, 1.0.0, DI Matthijssen; Creation
 * <li>2006-05-14, 1.1.0, MA Swertz; Refectoring into Invengine.
 * </ul>
 * TODO look at the depreciated functions.
 */

package org.molgenis.framework.ui.html;

/**
 * Input for hyperlinks. This will automatically create a hyperlink to outside information.
 */
public class HyperlinkInput extends HtmlInput
{	
	public HyperlinkInput(String name, Object value)
	{
		super (name, value);
	}
	
	public HyperlinkInput(String name)
	{
		this (name, null);
	}
	
	/**
	 * {@inheritDoc}. Inludes hyperlink naar outside information.
	 */
	public String getValue()
	{
		return "<a href=\""+super.getValue()+"\">"+super.getValue()+"</a>";
	}
	
	/**
	 * Override because hyperlink must not be escaped
	 */
	public String getHtmlValue()
	{
		return this.getValue();
	}

	@Override
	public String toHtml()
	{
		String readonly = ( isReadonly() ? "readonly class=\"readonly\" " : "");
		

		if (this.isHidden())
		{
			StringInput input = new StringInput(this.getName(), super.getValue());
			input.setLabel(this.getLabel());
			input.setDescription(this.getDescription());				
			input.setHidden(true);
			return input.toHtml();
		}
			
		return "<input id=\""+getId()+"\" name=\""+getName()+"\" value=\""+super.getValue()+"\" "+readonly+" />";
	}
	
}
