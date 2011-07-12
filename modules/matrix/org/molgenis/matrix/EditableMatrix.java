package org.molgenis.matrix;

import java.util.List;

/**
 * Adds editing capabilities to a Matrix.
 */
public interface EditableMatrix<E,A,V> extends Matrix<E,A,V>
{
	public void setRow(int row, List<E> rowValues);
	
	public void setCol(int col, List<E> colValues);
	
	public void setRow(E row, List<E> rowValues);
	
	public void setCol(A col, List<E> colValues);
	
	/**
	 * Set value
	 */
	public void setValue(int row, int col, E value);

	/**
	 * Set value
	 */
	public void setValue(String rowName, String colName, E value);
	
	
}
