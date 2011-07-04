package org.molgenis.fieldtypes;

import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.HtmlInputException;
import org.molgenis.model.MolgenisModelException;

/**
 * This type is used when the field type is not known.
 * For example if(MolgenisFieldType.getType(name) instanceof UnknownType( {//handle this bad situation 
 */
public class UnknownField extends FieldType
{

	@Override
	public String getFormatString()
	{
		return null;
	}

	@Override
	public String getHsqlType() throws MolgenisModelException
	{
		return null;
	}

	@Override
	public String getJavaAssignment(String value) throws MolgenisModelException
	{
		return null;
	}

	@Override
	public String getJavaPropertyDefault() throws MolgenisModelException
	{
		return null;
	}

	@Override
	public String getJavaPropertyType() throws MolgenisModelException
	{
		return null;
	}

	@Override
	public String getMysqlType() throws MolgenisModelException
	{
		return null;
	}

	@Override
	public String getXsdType() throws MolgenisModelException
	{
		return null;
	}

	@Override
	public HtmlInput createInput(String name, String xrefEntityClassName) throws HtmlInputException
	{
		return null;
	}

	@Override
	public String getCppPropertyType() throws MolgenisModelException
	{
		return "void*";
	}
	
	@Override
	public String getCppJavaPropertyType()
	{
		return "";
	}

}
