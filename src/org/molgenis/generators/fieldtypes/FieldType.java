package org.molgenis.generators.fieldtypes;

import java.util.List;

import org.molgenis.generators.FieldTypeRegistry;
import org.molgenis.model.MolgenisModelException;
import org.molgenis.model.elements.Entity;
import org.molgenis.model.elements.Field;

/** TODO merge this with the Field framework */
public abstract class FieldType
{
	private FieldTypeRegistry registry;
	protected Field f;

	public void setRegistry(FieldTypeRegistry registry)
	{
		this.registry = registry;
	}
	
	public FieldType getFieldType(Field f) throws MolgenisModelException
	{
		return registry.get(f);
	}

	public Entity getEntityByName(String name)
	{
		return (Entity)f.getEntity().get(name);
	}
	
	public String getJavaSetterType() throws MolgenisModelException
	{
		return this.getJavaPropertyType();
	}

	/**
	 * Product the Java type of this field type. Default: "String".
	 * 
	 * @return type in java code
	 * @throws MolgenisModelException
	 */
	abstract public String getJavaPropertyType() throws MolgenisModelException;

	/**
	 * Produce a valid Java snippet to set the default of a field, using the
	 * 'getDefault' function of that field. Default: "\""+f.getDefault()+"\"".
	 * 
	 * @return default in java code
	 * @throws MolgenisModelException 
	 */
	abstract public String getJavaPropertyDefault() throws MolgenisModelException;

	/**
	 * Produce a valid Java snippet to set a value for  field.
	 * 
	 * @return default in java code
	 * @throws MolgenisModelException 
	 */
	public abstract String getJavaAssignment(String value) throws MolgenisModelException;
	
	/**
	 * Produce a valid mysql snippet indicating the mysql type. E.g. "BOOL".
	 * 
	 * @return mysql type string
	 * @throws MolgenisModelException 
	 */
	abstract public String getMysqlType() throws MolgenisModelException;
	
	/**
	 * Produce valid XSD type
	 */
	abstract public String getXsdType() throws MolgenisModelException;
	
	/**
	 * Convert a list of string to comma separated values.
	 * 
	 * @param elements
	 * @return csv
	 */
	public String toCsv(List<String> elements)
	{
		String result = "";

		for (String str : elements)
		{
			result += ((elements.get(0) == str) ? "" : ",") + "'" + str + "'";
		}

		return result;
	}
	
	/**
	 * Produce a valid hsql snippet indicating the mysql type. E.g. "BOOL".
	 * 
	 * @return hsql type string
	 * @throws MolgenisModelException 
	 */
	public abstract String getHsqlType() throws MolgenisModelException;

	public void setField(Field f)
	{
		this.f = f;
	}
}
