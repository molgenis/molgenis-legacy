package org.molgenis.generators.fieldtypes;

import org.molgenis.model.MolgenisModelException;
import org.molgenis.model.elements.Field;

public class EnumField extends AbstractField
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
