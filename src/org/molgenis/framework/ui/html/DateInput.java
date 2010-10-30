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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Input for Date. Depends on javascript to showDateInput().
 */

public class DateInput extends HtmlInput
{
	/** uses default value of today as value*/
	public DateInput(String name)
	{
		this(name, new java.sql.Date(new java.util.Date().getTime()));
	}
	
	public DateInput(String name, Object value)
	{
		super(name, value);
	}

	// tohtml
	public String toHtml()
	{

		String readonly = ( isReadonly() ? " class=\"readonly\" readonly " : "onclick=\"showDateInput(this) " + "");

		if( this.isHidden() )
		{
			StringInput input = new StringInput(this.getName(), this.getValue());
			input.setHidden(true);
			return input.toHtml();
		}

		return "<input type=\"text\" id=\"" + this.getId() + "\" name=\"" + getName() + "\" value=\"" + getValue() + "\" " + readonly + "\" autocomplete=\"off\"/>";
	}

	public String getValue()
	{
		if( getObject() == null )
			return "";
		DateFormat formatter = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
		String result = formatter.format(super.getObject());
		result = result.substring(0, 1).toUpperCase() + result.substring(1);
		return result;
	}
}
