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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;



/**
 * Input for date. Depends on javascript to showDateInput().
 */

public class DateInput extends HtmlInput
{
	/** uses default value of today as value
	 * @throws ParseException */
	public DateInput(String name)
	{
		this(name, Calendar.getInstance().getTime());
	}
	
	public DateInput(String name, String label)
	{
		super(name,null);
		this.setLabel(label);
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

		return "<input type=\"text\" id=\"" + this.getId() + "\" name=\"" + this.getName() + "\" value=\"" + this.getValue() + "\" " + readonly + "\" size=\"32\" autocomplete=\"off\"/>";
	}

	public String getValue()
	{
		DateFormat formatter = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
		
		Object dateObject = getObject();
		if (dateObject == null) {
			return "";
		}
		if (dateObject.equals("")) {
			return "";
		}
		// If it's already a string, return it
		if (dateObject instanceof String) {
			return dateObject.toString();
		}
		
		// If it's a Date object, first format and then return
		String result;
		try {
			result = formatter.format(dateObject);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		result = result.substring(0, 1).toUpperCase() + result.substring(1);
		return result;
	}
}
