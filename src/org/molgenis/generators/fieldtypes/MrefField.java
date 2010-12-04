package org.molgenis.generators.fieldtypes;

import org.molgenis.model.MolgenisModelException;
import org.molgenis.model.elements.Field;

public class MrefField extends AbstractField
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
		return "java.util.List<"+getFieldType(f_ref).getJavaPropertyType()+">";
	}
	
	@Override
	public String getJavaPropertyDefault() throws MolgenisModelException
	{
		//Entity e_ref = f.getXrefEntity();
		Field f_ref = f.getXrefField();
		//if(f.getDefaultValue() == null || f.getDefaultValue() == "") "new java.util.ArrayList<"+getFieldType(f_ref).getJavaPropertyType(f_ref)+">()";
		//FIXME can there be defaults here?
		return "new java.util.ArrayList<"+getFieldType(f_ref).getJavaPropertyType()+">()";
	}
	
	@Override
	public String getJavaSetterType() throws MolgenisModelException
	{
		//Entity e_ref = f.getXrefEntity();
		Field f_ref = f.getXrefField();
		return "new java.util.ArrayList<"+getFieldType(f_ref).getJavaSetterType()+">()";
	}
	
	@Override
	public String getMysqlType() throws MolgenisModelException
	{
		//FIXME this function should be never called???
		return getFieldType(f.getXrefField()).getMysqlType();
	}

	@Override
	public String getHsqlType() throws MolgenisModelException
	{
		return getFieldType(f.getXrefField()).getHsqlType();
	}
	
	@Override
	public String getXsdType() throws MolgenisModelException
	{
		return getFieldType(f.getXrefField()).getXsdType();
	}

}
