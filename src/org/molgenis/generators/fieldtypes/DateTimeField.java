package org.molgenis.generators.fieldtypes;

import org.molgenis.model.MolgenisModelException;

public class DateTimeField extends AbstractField
{
	@Override
	public String getJavaPropertyType() throws MolgenisModelException
	{
		return "java.util.Date";
	}
	
	@Override
	public String getJavaAssignment(String value)
	{
		if(value == null || value.equals("")) return "null";
		return "java.sql.Timestamp.valueOf(\""+value+"\")";
	}
	
	@Override
	public String getJavaPropertyDefault()
	{
		if(f.isAuto()) return "new java.sql.Date(new java.util.Date().getTime())";
		else return getJavaAssignment(f.getDefaultValue());
	}

	@Override
	public String getMysqlType() throws MolgenisModelException
	{
		return "DATETIME";
	}
	
	@Override
	public String getXsdType()
	{
		return "dateTime";
	}

	@Override
	public String getJavaSetterType() throws MolgenisModelException
	{
		return "Timestamp";
	}

	@Override
	public String getHsqlType()
	{
		return "DATETIME";
	}
}
