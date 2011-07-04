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
import java.util.Date;
import java.util.Locale;

import org.molgenis.util.Tuple;

/**
 * Input for datetime data. Depends on javascript to showDateInput().
 */

public class DatetimeInput extends HtmlInput<Date>
{
	public DatetimeInput(String name)
	{
		super(name, null);
	}

	public DatetimeInput(String name, Date value)
	{
		super(name, value);
	}

	public DatetimeInput(String name, String label, Date value,
			boolean nillable, boolean readonly)
	{
		super(name, value);
		if (label != null && !label.equals("null")) this.setLabel(label);
		this.setReadonly(readonly);
		this.setNillable(nillable);
	}

	public DatetimeInput(Tuple p) throws HtmlInputException
	{
		set(p);
	}

	// tohtml
	public String toHtml()
	{

		String readonly = isReadonly() ? " class=\"readonly\" readonly=\"readonly\" "
				: "onclick=\"showDateInput(this,true) " + "";

		if (this.isHidden())
		{
			StringInput input = new StringInput(this.getName(), this.getValue());
			input.setHidden(true);
			return input.toHtml();
		}

		return "<input type=\"text\" id=\"" + this.getId() + "\" name=\""
				+ getName() + "\"  size=\"32\" value=\"" + getValue() + "\" "
				+ readonly + "\" autocomplete=\"off\"/>";
	}

	public String getValue()
	{
		Object dateObject = super.getObject();
		if (dateObject == null) return "";

		// If it's already a string, return it
		if (dateObject instanceof String)
		{
			return dateObject.toString();
		}

		DateFormat formatter = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss",
				Locale.US);
		String result = formatter.format(dateObject);
		result = result.substring(0, 1).toUpperCase() + result.substring(1);
		return result;
	}

	@Override
	public String toHtml(Tuple p) throws HtmlInputException
	{
		return new DatetimeInput(p).render();
	}
}
