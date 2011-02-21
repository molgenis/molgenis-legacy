package org.molgenis.generators.fieldtypes;

import org.molgenis.model.MolgenisModelException;

public class EnumField extends FieldType
{
	@Override
	public String getJavaPropertyType()
	{
		return "String";
	}
	
	@Override
	public String getJavaAssignment(String value)
	{
		if(value == null || value.equals("") ) return "null";
		return "\""+value+"\"";
	}
	
	@Override
	public String getJavaPropertyDefault()
	{
		return getJavaAssignment(f.getDefaultValue());
	}

	@Override
	public String getMysqlType() throws MolgenisModelException
	{
		return "ENUM("+this.toCsv(f.getEnumOptions())+")";
	}


	@Override
	public String getHsqlType()
	{
		return "VARCHAR(1024)";
	}
	@Override
	public String getXsdType()
	{
		return "string";
	}

}
