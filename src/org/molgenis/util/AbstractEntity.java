package org.molgenis.util;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Abstract Entity class that implements common parts for each Entity.
 */
public abstract class AbstractEntity implements Entity
{
	private boolean readonly;
	
	public void set(String name, Object value) throws ParseException
	{
		//inefficient
		Tuple t = new SimpleTuple();
		t.set(name,value);
		this.set(t,false);
	}

	public void set( Tuple values ) throws ParseException
	{
		this.set(values,true);
	}
	
	public Tuple getValues()
	{
		Tuple t = new SimpleTuple();
		for(String field: this.getFields())
		{
			t.set(field,this.get(field));
		}
		return t;
	}
	
	public String getValues( String sep )
	{
		StringWriter out = new StringWriter();
		for(String field: this.getFields())
		{
			{
					Object valueO = get(field);
					String valueS;
					if (valueO != null)
						valueS = valueO.toString();
					else 
						valueS = "";
					valueS = valueS.replaceAll("\r\n"," ").replaceAll("\n"," ").replaceAll("\r"," ");
					valueS = valueS.replaceAll("\t"," ").replaceAll(sep," ");
					out.write(valueS);
				}
		}
		return out.toString();
	}
	
	public void setReadonly(boolean readonly)
	{
		this.readonly = readonly;
	}
	
	public boolean isReadonly()
	{
		return readonly;
	}
	
	@SuppressWarnings("deprecation")
	public static java.sql.Date string2date(String str) throws ParseException
	{
		try
		{
			DateFormat formatter = new SimpleDateFormat(SimpleTuple.DATEFORMAT, Locale.US);
			return new java.sql.Date(formatter.parse(str).getDate());
		}
		catch (ParseException pe)
		{
			try
			{
				DateFormat formatter = new SimpleDateFormat(SimpleTuple.DATEFORMAT2, Locale.US);
				return new java.sql.Date(formatter.parse(str).getDate());
			}
			catch (ParseException pe2)
			{
				throw new ParseException("parsing failed: expected date value formatted '" + SimpleTuple.DATEFORMAT+ " or "+SimpleTuple.DATEFORMAT2, 0);
			}
		}		
	}
	
	 
}
