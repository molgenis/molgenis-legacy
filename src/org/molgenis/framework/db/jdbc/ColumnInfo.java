package org.molgenis.framework.db.jdbc;


public class ColumnInfo
{
	String tableName;
	Type type;
	
	/**
	 * Description of the different types of a field.
	 */
	public static enum Type
	{
		/** The type is unknown, this case should raise an exception. */
		UNKNOWN,
		/** The type is a simple boolean. */
		BOOL,
		/** The type is a simple integer. */
		INT,
		/** The type is a decimal value. */
		LONG,
		/** The type is a decimal value. */
		DECIMAL,
		/**
		 * The type is a variable character string. More information can be
		 * found with the appropriate functions.
		 */
		STRING,
		/** fixed length */
		CHAR,
		/** The type is free-text. The length of the string is not defined. */
		TEXT,
		/** The type is a date-field. */
		DATE,
		/** */
		DATETIME,
		/**
		 * The type of the field is user, which basically references a hidden
		 * table.
		 */
		USER,
		/** The type of the field is file. */
		FILE,
		/** The type of the field is image. */
		IMAGE,
		/** */
		ENUM,
		/** Reference to another table, which can contain only 1 value. */
		XREF_SINGLE,
		/** Reference to another table, which can contain multiple values. */
		XREF_MULTIPLE,
		/** hyperlink */
		HYPERLINK,
		/** Nucleotide sequence */
		NSEQUENCE,
		/** Nucleotide sequence */
		ONOFF,
		/** List of values */
		List,
		/** Hexa values */
		HEXA;
	}
	
	public ColumnInfo(String tableName, Type type)
	{
		this.setTableName(tableName);
		this.setType(type);
	}
	
	public String getTableName()
	{
		return tableName;
	}

	public final void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	public Type getType()
	{
		return type;
	}

	public final void setType(Type type)
	{
		this.type = type;
	}
}
