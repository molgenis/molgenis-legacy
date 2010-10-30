package org.molgenis.generators.fieldtypes;

import org.molgenis.model.MolgenisModelException;
import org.molgenis.model.elements.Field;

public class TextField extends AbstractField
{
	@Override
	public String getJavaAssignment(String value)
	{
		if(value == null ||value.equals("") ) return "null";
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
		return "TEXT";
	}

	@Override
	public String getHsqlType() throws MolgenisModelException
	{
		//these guys don't have TEXT?
		return "VARCHAR";
	}
	
	@Override
	public String getXsdType() throws MolgenisModelException
	{
		// TODO Auto-generated method stub
		return "text";
	}

	@Override
	public String getJavaPropertyType() throws MolgenisModelException
	{
		return "String";
	}

}
