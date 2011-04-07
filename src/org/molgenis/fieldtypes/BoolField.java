package org.molgenis.fieldtypes;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.html.BoolInput;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.model.MolgenisModelException;

public class BoolField extends FieldType
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

	@Override
	public String getFormatString()
	{
		return "%d";
	}

	@Override
	public HtmlInput createInput(String name, String xrefEntityClassName,
			Database db) throws InstantiationException, IllegalAccessException
	{
		return new BoolInput(name);
	}

	@Override
	public String getCppPropertyType() throws MolgenisModelException
	{
		return "bool";
	}

	@Override
	public String getCppJavaPropertyType()
	{
		// TODO Auto-generated method stub
		return "Ljava/lang/Boolean;";
	}
}
