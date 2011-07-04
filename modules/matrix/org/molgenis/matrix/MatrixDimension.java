package org.molgenis.matrix;

import org.molgenis.fieldtypes.FieldType;

/**
 * MatrixDimension is used to define the rows and columns of a matrix. In simple
 * cases this will be only name (not necessarily unique). In more advanced use
 * cases this can include a type or metadata (e.g. in MOLGENIS research platform
 * we attach all 'Individual' information as well).
 */
public class MatrixDimension<E>
{
	private String name;
	private E value;
	private int index;

	public MatrixDimension(String name) throws MatrixException
	{
		if ("".equals(name)) throw new MatrixException(
				"row and column names can't be 'null' or '\"\"'");
		this.name = name;
	}

	/**
	 * Get the type definition from this field using the MOLGENIS typing system
	 * 
	 * This allows flexible typing like Int, Date, String,Xref or any other type
	 * MOLGENIS has today or in the future.
	 */
	public FieldType getType()
	{
		return null;
	}

	/**
	 * Set the type definition from this field using the MOLGENIS typing system.
	 * 
	 * This allows flexible typing like Int, Date, String,Xref or any other type
	 * MOLGENIS has today or in the future.
	 * 
	 * @param type
	 */
	// public void setType(FieldType type);

	/**
	 * Name of this dimension. N.B. Doesn't have to be unique within a matrix.
	 * 
	 * @return name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Set the name of this dimension
	 */
	// public void setName();
}
