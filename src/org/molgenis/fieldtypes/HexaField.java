package org.molgenis.fieldtypes;

import org.molgenis.model.MolgenisModelException;

public class HexaField extends FieldType
{
	@Override
	public String getJavaAssignment(String value) throws MolgenisModelException
	{
		if(value == null || value.equals("") ) return "null";
		return "\""+value+"\"";
	}
	
	@Override
	public String getJavaPropertyDefault() throws MolgenisModelException
	{
		return getJavaAssignment(f.getDefaultValue());
	}

	@Override
	public String getJavaPropertyType() throws MolgenisModelException
	{
		return "String";
	}

	@Override
	public String getMysqlType() throws MolgenisModelException
	{
		return "VARCHAR(255)";
	}

	@Override
	public String getHsqlType() throws MolgenisModelException
	{
		return "VARCHAR(255)";
	}
	@Override
	public String getXsdType() throws MolgenisModelException
	{
		return "string";
	}

	@Override
	public String getFormatString()
	{
		return "%s";
	}

}
