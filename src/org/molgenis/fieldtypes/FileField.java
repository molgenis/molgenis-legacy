package org.molgenis.fieldtypes;

import org.molgenis.framework.ui.html.FileInput;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.HtmlInputException;
import org.molgenis.model.MolgenisModelException;

public class FileField extends FieldType
{
	@Override
	public String getJavaPropertyType()
	{
		return "String";
	}
	
	@Override
	public String getJavaAssignment(String value)
	{
		if(value == null || value.equals("")) return "null";
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
		return "VARCHAR(1024)";
	}

	@Override
	public String getHsqlType()
	{
		return "VARCHAR(1024)";
	}
	@Override
	public String getXsdType()
	{
		return "url";
	}

	@Override
	public String getFormatString()
	{
		return "%s";
	}

	@Override
	public HtmlInput createInput(String name, String xrefEntityClassName) throws HtmlInputException
	{
		return new FileInput(name);
	}

	@Override
	public String getCppPropertyType() throws MolgenisModelException
	{
		return "string";
	}
	
	@Override
	public String getCppJavaPropertyType()
	{
		return "Ljava/lang/String;";
	}
}
