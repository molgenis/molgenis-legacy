package org.molgenis.generators.fieldtypes;

import org.molgenis.model.MolgenisModelException;

public class LongField extends FieldType
{
	@Override
	public String getJavaPropertyType()
	{
		return "Long";
	}

	@Override
	public String getJavaAssignment(String value)
	{
		if (value == null || value.equals("") ) return "null";
		return "" + Long.parseLong(value) + "L";
	}

	
	@Override
	public String getJavaPropertyDefault()
	{
		return getJavaAssignment(f.getDefaultValue());
	}
	
	@Override
	public String getMysqlType() throws MolgenisModelException
	{
		return "BIGINT";
	}

	@Override
	public String getHsqlType()
	{
		return "LONG";
	}
	@Override
	public String getXsdType()
	{
		return "boolean";
	}

}
