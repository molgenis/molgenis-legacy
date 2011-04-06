package org.molgenis.fieldtypes;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.html.DecimalInput;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.model.MolgenisModelException;

public class DecimalField extends FieldType
{
	@Override
	public String getJavaPropertyType()
	{
		return "Double";
	}

	@Override
	public String getJavaAssignment(String value)
	{
		if(value == null || value.equals("") ) return "null";
		return ""+Double.parseDouble(value);
	}
	
	@Override
	public String getJavaPropertyDefault()
	{
		return getJavaAssignment(f.getDefaultValue());
	}
	
	@Override
	public String getMysqlType() throws MolgenisModelException
	{
		return "DECIMAL(65,30)";
	}

	@Override
	public String getHsqlType()
	{
		return "DOUBLE";
	}
	
	@Override
	public String getXsdType()
	{
		return "decimal";
	}

	@Override
	public String getFormatString()
	{
		return "%.20g";
	}

	@Override
	public HtmlInput createInput(String name, String xrefEntityClassName,
			Database db) throws InstantiationException, IllegalAccessException
	{
		return new DecimalInput(name);
	}

	@Override
	public String getCppJavaPropertyType()
	{
		return "Ljava/lang/Double;";
	}

	@Override
	public String getCppPropertyType() throws MolgenisModelException
	{
		return "double";
	}
}
