package org.molgenis.generators.fieldtypes;

import org.molgenis.model.MolgenisModelException;

public class DecimalField extends FieldType
{
	@Override
	public String getJavaPropertyType()
	{
		return "Double";
	}

	@Override
	public String getJavaAssignment(String value)
	{
		if(value == null || value.equals("") ) return "null";
		return ""+Double.parseDouble(value);
	}
	
	@Override
	public String getJavaPropertyDefault()
	{
		return getJavaAssignment(f.getDefaultValue());
	}
	
	@Override
	public String getMysqlType() throws MolgenisModelException
	{
		return "DECIMAL(65,30)";
	}

	@Override
	public String getHsqlType()
	{
		return "DOUBLE";
	}
	
	@Override
	public String getXsdType()
	{
		return "decimal";
	}
}
