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
 * Input for datetime data. Depends on javascript to showDateInput().
 */

public class DatetimeInput extends HtmlInput
{
	public DatetimeInput(String name)
	{
		super(name,null);
	}
	
	public DatetimeInput(String name, Object value)
	{
		super(name, value);
	}

	// tohtml
	public String toHtml()
	{

		String readonly = isReadonly() ? " class=\"readonly\" readonly=\"readonly\" " : "onclick=\"showDateInput(this,true) " + "";

		if( this.isHidden() )
		{
			StringInput input = new StringInput(this.getName(), this.getValue());
			input.setHidden(true);
			return input.toHtml();
		}

		return "<input type=\"text\" id=\"" + this.getId() + "\" name=\"" + getName() + "\"  size=\"32\" value=\"" + getValue() + "\" " + readonly + "\" autocomplete=\"off\"/>";
	}

	public String getValue()
	{
		if( super.getObject() == null )
			return "";
		if( ((String)super.getObject()).equals("") )
			return "";
		DateFormat formatter = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss", Locale.US);
		String result = formatter.format(super.getObject());
		result = result.substring(0, 1).toUpperCase() + result.substring(1);
		return result;
	}
}
