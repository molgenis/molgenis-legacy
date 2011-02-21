package org.molgenis.generators.fieldtypes;

import org.molgenis.model.MolgenisModelException;

public class IntField extends FieldType
{
	@Override
	public String getJavaPropertyType() throws MolgenisModelException
	{
		return "Integer";
	}
	
	@Override
	public String getJavaAssignment(String value)
	{
		if(value == null || value.equals("")) return "null";
		return ""+Integer.parseInt(value);
	}
	
	@Override
	public String getJavaPropertyDefault()
	{
		return getJavaAssignment(f.getDefaultValue());
	}
	
	@Override
	public String getMysqlType() throws MolgenisModelException
	{
		return "INTEGER";
	}
	
	public String getJavaSetterType() throws MolgenisModelException
	{
		return "Int";
	}

	@Override
	public String getHsqlType()
	{
		return "INT";
	}
	@Override
	public String getXsdType()
	{
		return "int";
	}
}
