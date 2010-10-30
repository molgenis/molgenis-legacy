package org.molgenis.generators.fieldtypes;

import org.molgenis.model.MolgenisModelException;
import org.molgenis.model.elements.Field;

public class LongField extends AbstractField
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
		return "LONG";
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
