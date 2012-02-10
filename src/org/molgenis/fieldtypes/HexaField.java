package org.molgenis.fieldtypes;

import java.text.ParseException;

import org.molgenis.MolgenisFieldTypes.FieldTypeEnum;
import org.molgenis.framework.ui.html.HexaInput;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.HtmlInputException;
import org.molgenis.model.MolgenisModelException;

public class HexaField extends FieldType
{
	@Override
	public String getJavaAssignment(String value) throws MolgenisModelException
	{
		if(value == null || value.equals("") ) return "null";
		return "\""+value+"\"";
	}
	
	@Override
	public String getJavaPropertyDefault() throws MolgenisModelException
	{
		return getJavaAssignment(f.getDefaultValue());
	}

	@Override
	public String getJavaPropertyType() throws MolgenisModelException
	{
		return "String";
	}

	@Override
	public String getMysqlType() throws MolgenisModelException
	{
		return "VARCHAR(255)";
	}
	
	@Override
	public String getOracleType() throws MolgenisModelException
	{
		return "VARCHAR2(255)";
	}

	@Override
	public String getHsqlType() throws MolgenisModelException
	{
		return "VARCHAR(255)";
	}
	@Override
	public String getXsdType() throws MolgenisModelException
	{
		return "string";
	}

	@Override
	public String getFormatString()
	{
		return "%s";
	}

	@Override
	public HtmlInput<?> createInput(String name, String xrefEntityClassName) throws HtmlInputException
	{
		return new HexaInput(name);
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

	public Class<?> getJavaType() throws MolgenisModelException
	{
		return HexaInput.class;
	}

	@Override
	public Object getTypedValue(String value) throws ParseException
	{
		throw new UnsupportedOperationException("Unable to convert hexa field " + value);
	}
	
	public FieldTypeEnum getEnumType()
	{
		return FieldTypeEnum.HEXA;
	}
}
