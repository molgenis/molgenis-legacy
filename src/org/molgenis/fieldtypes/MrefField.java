package org.molgenis.fieldtypes;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.MrefInput;
import org.molgenis.model.MolgenisModelException;
import org.molgenis.model.elements.Field;

/**
 * Many to many reference.
 * 
 * Example MOLGENIS DSL, 
 * <pre><field name="myfield" type="mref" xref_entity="OtherEntity" xref_field="id" xref_label="name"/>
 *</pre>
 * This example would in the UI show a seletion box with 'name' elements.
 */
public class MrefField extends FieldType
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

	@Override
	public String getFormatString()
	{
		return "";
	}

	@Override
	public HtmlInput createInput(String name, String xrefEntityClassName,
			Database db) throws InstantiationException, IllegalAccessException
	{
		return new MrefInput(name, xrefEntityClassName, db);
	}

	@Override
	public String getCppPropertyType() throws MolgenisModelException
	{
		Field f_ref = f.getXrefField();
		return "vector<"+getFieldType(f_ref).getCppPropertyType()+">";
	}
	
	@Override
	public String getCppJavaPropertyType() throws MolgenisModelException
	{
		return "Ljava/util/List;";
	}

}
