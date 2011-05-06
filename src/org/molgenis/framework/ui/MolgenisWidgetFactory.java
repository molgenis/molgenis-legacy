package org.molgenis.framework.ui;

import java.util.Date;

import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.DateInput;
import org.molgenis.framework.ui.html.StringInput;

public class MolgenisWidgetFactory
{
	public DateInput date(String name, String label, Date value, boolean nillable, boolean readonly)
	{
		return new DateInput(name, "null".equals(label) ? null : label, value, nillable, readonly);
	}
	
	public Date now()
	{
		return new Date();
	}
	
	public ActionInput action(String name, String label)
	{
		return new ActionInput(name, "null".equals(label) ? null : label);
	}
	
	public StringInput string(String name, String label, String value, boolean nillable, boolean readonly)
	{
		return new StringInput(name, "null".equals(label) ? null : label, "null".equals(value) ? null : value, nillable, readonly);
	}
}
