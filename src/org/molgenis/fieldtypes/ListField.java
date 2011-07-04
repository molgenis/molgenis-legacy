package org.molgenis.fieldtypes;

import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.HtmlInputException;
import org.molgenis.model.MolgenisModelException;

@Deprecated
public class ListField extends FieldType
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

	@Override
	public String getFormatString()
	{
		return "";
	}

	/**
	 * Since this class is deprecated, this method is not implemented.
	 */
	@Override
	public HtmlInput createInput(String name, String xrefEntityClassName) throws HtmlInputException
	{
		throw new HtmlInputException("Class deprecated, will not return input.");
	}

	@Override
	public String getCppPropertyType() throws MolgenisModelException
	{
		return "vector<Entity>";
	}
	
	@Override
	public String getCppJavaPropertyType()
	{
		return "Ljava/util/List;";
	}
}
