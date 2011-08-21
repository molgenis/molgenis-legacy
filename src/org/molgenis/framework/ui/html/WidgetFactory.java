package org.molgenis.framework.ui.html;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.molgenis.util.SimpleTuple;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateModel;

/**
 * WidgetFactory is a helper class to create input widgets such as date, action,
 * ints etc. It is used by WidgetFactory.ftl to easily create widgets in you
 * ftl. For example in ftl: <@date name="mydate"/>
 */
public class WidgetFactory
{
	public static class HtmlInputAdapter implements TemplateDirectiveModel
	{
		HtmlInput<?> input = null;

		private HtmlInputAdapter(HtmlInput<?> input)
		{
			this.input = input;
		}

		@SuppressWarnings({ "rawtypes" })
		@Override
		public void execute(Environment env, Map params,
				TemplateModel[] loopVars, TemplateDirectiveBody body)
		{
			// transform params
			SimpleTuple t = new SimpleTuple();
			for (Object key : params.keySet())
			{
				if (params.get(key) instanceof SimpleScalar)
				{
					t.set(key.toString(), ((SimpleScalar) params.get(key))
							.toString());
				}
				else
				{
					t.set(key.toString(), params.get(key));
				}
			}

			// try to get html
			try
			{
				env.getOut().write(input.render(t));
			}
			catch (Exception e)
			{
				try
				{
					env.getOut().write(
							"macro failed: " + e.getMessage() + "<br/>");
				}
				catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}

	public static void configure(Configuration conf)
	{
		Map<String,HtmlInput<?>> map = new LinkedHashMap<String,HtmlInput<?>>();
		map.put("action", new ActionInput());
		map.put("bool", new BoolInput());
		map.put("string", new StringInput());
		map.put("checkbox", new CheckboxInput());
		map.put("date", new DateInput());
		map.put("datetime", new DateInput());
		map.put("decimal", new DecimalInput());
		map.put("enum", new EnumInput());
		map.put("file", new FileInput());
		map.put("int", new IntInput());
		map.put("xref", new XrefInput());
		map.put("mref", new MrefInput());
		map.put("text", new TextInput());
		map.put("hidden", new HiddenInput());
		
		for(String key: map.keySet())
		{
			conf.setSharedVariable(key, new HtmlInputAdapter(map.get(key)));
		}

	}
	// private Database db;
	//	
	// private static String NAME = "name";
	// private static String LABEL = "name";
	// private static String VALUE = "name";
	// private static String NILLABlE = "nillable";
	// private static String READONLY = "readonly";
	//	
	//
	// /**
	// * Constructor
	// *
	// * @param db
	// * database that is used to construct XREFs and other data
	// * dependant widgets
	// */
	// public WidgetFactory(Database db)
	// {
	// this.db = db;
	// }
	//
	// /** Factory method for <@date */
	// public DateInput date(String name, String label, Date value,
	// boolean nillable, boolean readonly)
	// {
	// return new DateInput(name, "null".equals(label) ? null : label, value,
	// nillable, readonly);
	// }
	//
	// /** Factory method for <@datetime */
	// public DatetimeInput datetime(String name, String label, Date value,
	// boolean nillable, boolean readonly)
	// {
	// return new DatetimeInput(name, "null".equals(label) ? null : label,
	// value, nillable, readonly);
	// }
	//
	// /** Helper method for date default value */
	// public Date now()
	// {
	// return new Date();
	// }
	//
	// /** Factory method for <@action */
	// public ActionInput action(String name, String label)
	// {
	// return new ActionInput(name, "null".equals(label) ? null : label);
	// }
	//
	// /** Factory method for <@string */
	// public StringInput string(String name, String label, String value,
	// boolean nillable, boolean readonly)
	// {
	// return new StringInput(name, "null".equals(label) ? null : label,
	// "null".equals(value) ? null : value, nillable, readonly);
	// }
	//
	// /** Factory method for <@int */
	// public IntInput integer(String name, String label, Integer value,
	// boolean nillable, boolean readonly)
	// {
	// return new IntInput(name, "null".equals(label) ? null : label, "null"
	// .equals(value) ? null : value, nillable, readonly);
	// }
	//
	// /** Factory method for <@double */
	// public DecimalInput decimal(String name, String label, Double value,
	// boolean nillable, boolean readonly)
	// {
	// return new DecimalInput(name, "null".equals(label) ? null : label,
	// "null".equals(value) ? null : value, nillable, readonly);
	// }
	//
	// /** Factory method for <@xref */
	// public EntityInput xref(String name, String entity, String label,
	// Double value, boolean nillable, boolean readonly)
	// {
	// return new XrefInput(name, entity, db, "null".equals(label) ? null
	// : label, "null".equals(value) ? null : value, nillable,
	// readonly);
	// }
	//
	// /** Factory method for <@mref */
	// public MrefInput mref(String name, String entity, String label,
	// Double value, boolean nillable, boolean readonly)
	// {
	// return new MrefInput(name, entity, db, "null".equals(label) ? null
	// : label, "null".equals(value) ? null : value, nillable,
	// readonly);
	// }
	//
	// /** Factory method for <@file */
	// public FileInput file(String name, String entity, String label,
	// String value, boolean nillable, boolean readonly)
	// {
	// return new FileInput(name, "null".equals(label) ? null : label, "null"
	// .equals(value) ? null : value, nillable, readonly);
	// }
	//
	// /** Factory method for <@file */
	// public BoolInput bool(String name, String entity, String label,
	// Boolean value, boolean nillable, boolean readonly)
	// {
	// return new BoolInput(name, "null".equals(label) ? null : label, value,
	// nillable, readonly);
	// }
	//
	// /** Factory method for <@checkbox */
	// public CheckboxInput checkbox(String name, List<String> options,
	// List<String> optionLabels, String value, String label,
	// boolean nillable, boolean readonly)
	// {
	// return new CheckboxInput(name, options, optionLabels, "null"
	// .equals(label) ? null : label, value, nillable, readonly);
	// }

}
