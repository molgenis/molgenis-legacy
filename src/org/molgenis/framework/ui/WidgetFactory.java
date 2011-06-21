package org.molgenis.framework.ui;

import java.util.Date;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.BoolInput;
import org.molgenis.framework.ui.html.CheckboxInput;
import org.molgenis.framework.ui.html.DateInput;
import org.molgenis.framework.ui.html.DatetimeInput;
import org.molgenis.framework.ui.html.DecimalInput;
import org.molgenis.framework.ui.html.FileInput;
import org.molgenis.framework.ui.html.IntInput;
import org.molgenis.framework.ui.html.MrefInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.framework.ui.html.XrefInput;

/**
 * WidgetFactory is a helper class to create input widgets such as date, action,
 * ints etc. It is used by WidgetFactory.ftl to easily create widgets in you
 * ftl. For example in ftl: <@date name="mydate"/>
 */
public class WidgetFactory
{
	private Database db;
	
	/**
	 * Constructor 
	 * @param db database that is used to construct XREFs and other data dependant widgets
	 */
	public WidgetFactory(Database db)
	{
		this.db = db;
	}
	
	/** Factory method for <@date */
	public DateInput date(String name, String label, Date value,
			boolean nillable, boolean readonly)
	{
		return new DateInput(name, "null".equals(label) ? null : label, value,
				nillable, readonly);
	}
	
	/** Factory method for <@datetime */
	public DatetimeInput datetime(String name, String label, Date value,
			boolean nillable, boolean readonly)
	{
		return new DatetimeInput(name, "null".equals(label) ? null : label, value,
				nillable, readonly);
	}

	/** Helper method for date default value */
	public Date now()
	{
		return new Date();
	}

	/** Factory method for <@action */
	public ActionInput action(String name, String label)
	{
		return new ActionInput(name, "null".equals(label) ? null : label);
	}

	/** Factory method for <@string */
	public StringInput string(String name, String label, String value,
			boolean nillable, boolean readonly)
	{
		return new StringInput(name, "null".equals(label) ? null : label,
				"null".equals(value) ? null : value, nillable, readonly);
	}
	
	/** Factory method for <@int */
	public IntInput integer(String name, String label, Integer value,
			boolean nillable, boolean readonly)
	{
		return new IntInput(name, "null".equals(label) ? null : label,
				"null".equals(value) ? null : value, nillable, readonly);
	}
	
	/** Factory method for <@double */
	public DecimalInput decimal(String name, String label, Double value,
			boolean nillable, boolean readonly)
	{
		return new DecimalInput(name, "null".equals(label) ? null : label,
				"null".equals(value) ? null : value, nillable, readonly);
	}
	
	/** Factory method for <@xref */
	public XrefInput xref(String name, String entity, String label, Double value,
			boolean nillable, boolean readonly)
	{
			return new XrefInput(name, entity, db, "null".equals(label) ? null : label,
					"null".equals(value) ? null : value, nillable, readonly);
	}
	
	/** Factory method for <@mref */
	public MrefInput mref(String name, String entity, String label, Double value,
			boolean nillable, boolean readonly)
	{
			return new MrefInput(name, entity, db, "null".equals(label) ? null : label,
					"null".equals(value) ? null : value, nillable, readonly);
	}	
	
	/** Factory method for <@file */
	public FileInput file(String name, String entity, String label, String value,
			boolean nillable, boolean readonly)
	{
			return new FileInput(name, "null".equals(label) ? null : label,
					"null".equals(value) ? null : value, nillable, readonly);
	}	
	
	/** Factory method for <@file */
	public BoolInput bool(String name, String entity, String label, Boolean value,
			boolean nillable, boolean readonly)
	{
			return new BoolInput(name, "null".equals(label) ? null : label,
					value, nillable, readonly);
	}	
	
	/** Factory method for <@checkbox */
	public CheckboxInput checkbox(String name, List<String> options, List<String> optionLabels, String value, String label,
			boolean nillable, boolean readonly)
	{
			return new CheckboxInput(name, options, optionLabels, "null".equals(label) ? null : label,
					value, nillable, readonly);
	}	
	
}
