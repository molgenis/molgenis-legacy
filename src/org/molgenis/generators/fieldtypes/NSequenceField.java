package org.molgenis.generators.fieldtypes;

import org.molgenis.model.MolgenisModelException;

public class NSequenceField extends FieldType
{
	@Override
	public String getJavaPropertyType()
	{
		return "String";
	}
	
	@Override
	public String getJavaAssignment(String value)
	{
		//FIXME check if it is a valid nsequence
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
		return "TEXT";
	}

	@Override
	public String getHsqlType() throws MolgenisModelException
	{
		// TODO Auto-generated method stub
		return "TEXT";
	}
	
	@Override
	public String getXsdType() throws MolgenisModelException
	{
		// TODO Auto-generated method stub
		return "text";
	}


}
