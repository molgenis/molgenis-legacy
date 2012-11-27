package org.molgenis.fieldtypes;

import java.text.ParseException;

import org.molgenis.MolgenisFieldTypes.FieldTypeEnum;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.HtmlInputException;
import org.molgenis.framework.ui.html.NsequenceInput;
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
		// FIXME check if it is a valid nsequence
		if (value == null || value.equals("")) return "null";
		return "\"" + value + "\"";
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
	public String getOracleType() throws MolgenisModelException
	{
		return "BLOB";
	}

	@Override
	public String getHsqlType() throws MolgenisModelException
	{
		return "TEXT";
	}

	@Override
	public String getXsdType() throws MolgenisModelException
	{
		return "text";
	}

	@Override
	public String getFormatString()
	{
		return "%s";
	}

	@Override
	public HtmlInput<?> createInput(String name, String xrefEntityClassName) throws HtmlInputException

	{
		return new NsequenceInput(name);
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

	@Override
	public Class<?> getJavaType()
	{
		return String.class;
	}

	@Override
	public Object getTypedValue(String value) throws ParseException
	{
		return value;
	}

	@Override
	public FieldTypeEnum getEnumType()
	{
		return FieldTypeEnum.NSEQUENCE;
	}

}
