package org.molgenis.matrix;

/**
 * Adds editing capabilities to a Matrix.
 */
public interface EditableMatrix<E> extends Matrix<E>
{
	/**
	 * Set value
	 */
	public void setValue(int row, int col, E value);

	/**
	 * Set value
	 */
	public void setValue(String rowName, String colName, E value);
}
