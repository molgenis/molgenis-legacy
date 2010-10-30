package org.molgenis.generators.fieldtypes;

import org.molgenis.model.MolgenisModelException;
import org.molgenis.model.elements.Field;

public class BoolField extends AbstractField
{
	@Override
	public String getJavaPropertyType()
	{
		return "Boolean";
	}
	
	@Override
	public String getJavaAssignment(String value)
	{
		if(value == null || value.equals("")) return "null";
		return ""+Boolean.parseBoolean(value.toString());
	}
	
	@Override
	public String getJavaPropertyDefault()
	{
		return getJavaAssignment(f.getDefaultValue());
	}

	@Override
	public String getMysqlType() throws MolgenisModelException
	{
		return "BOOL";
	}

	@Override
	public String getHsqlType()
	{
		return "INTEGER";
	}
	
	@Override
	public String getXsdType()
	{
		return "boolean";
	}
}
