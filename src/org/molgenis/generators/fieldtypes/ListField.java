package org.molgenis.generators.fieldtypes;

import org.molgenis.model.MolgenisModelException;

public class ListField extends AbstractField
{
	@Override
	public String getJavaPropertyDefault() throws MolgenisModelException
	{
		return "new java.util.ArrayList<?>()";
	}
	
	@Override
	public String getJavaAssignment(String value)
	{
		return "NOT IMPLEMENTED";
	}
	
	@Override
	public String getJavaPropertyType()
	{
		return "java.util.List<?>";
	}
	
	@Override
	public String getMysqlType() throws MolgenisModelException
	{
		//should never happen?
		return "LIST CANNOT BE IN SQL";
	}
	
	@Override
	public String getJavaSetterType() throws MolgenisModelException
	{
		return "List";
	}

	@Override
	public String getHsqlType()
	{
		return "LIST CANNOT BE IN SQL";
	}
	
	public String getXsdType()
	{
		return "" ;
	}
}
