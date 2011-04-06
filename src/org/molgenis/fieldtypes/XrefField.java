package org.molgenis.fieldtypes;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.XrefInput;
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

	@Override
	public String getFormatString()
	{
		return "";
	}

	@Override
	public HtmlInput createInput(String name, String xrefEntityClassName,
			Database db) throws InstantiationException, IllegalAccessException
	{
		return new XrefInput(name, xrefEntityClassName, db);
	}

	@Override
	public String getCppPropertyType() throws MolgenisModelException
	{
		Field f_ref = f.getXrefField();
		return getFieldType(f_ref).getCppPropertyType();
	}
	
	@Override
	public String getCppJavaPropertyType() throws MolgenisModelException
	{
		Field f_ref = f.getXrefField();
		return getFieldType(f_ref).getCppJavaPropertyType();
	}



	
}
