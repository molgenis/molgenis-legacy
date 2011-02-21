package org.molgenis.generators.fieldtypes;

import org.molgenis.model.MolgenisModelException;
import org.molgenis.model.elements.Field;

public class XrefField extends FieldType 
{	
	@Override
	public String getJavaAssignment(String value)
	{
		return "NOT IMPLEMENTED";
	}
	
	@Override
	public String getJavaPropertyType() throws MolgenisModelException
	{
		//Entity e_ref = f.getXrefEntity();
		Field f_ref = f.getXrefField();
		return getFieldType(f_ref).getJavaPropertyType();
	}
	
	@Override
	public String getJavaPropertyDefault()
	{
		if(f.getDefaultValue() == null || f.getDefaultValue() == "") return "null";
		return f.getDefaultValue();
	}
	
	@Override
	public String getJavaSetterType() throws MolgenisModelException
	{
		
		return getFieldType(f.getXrefField()).getJavaSetterType();
	}
	
	@Override
	public String getMysqlType() throws MolgenisModelException
	{
		
		return getFieldType(f.getXrefField()).getMysqlType();
	}

	@Override
	public String getHsqlType() throws MolgenisModelException
	{
		return getFieldType(f.getXrefField()).getHsqlType();
	}
	
	public String getXsdType() throws MolgenisModelException
	{
		return getFieldType(f.getXrefField()).getXsdType();
	}
}
