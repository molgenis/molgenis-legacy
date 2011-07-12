package org.molgenis.matrix;

import java.util.List;

/**
 * Adds editing capabilities to a Matrix.
 * At some point we want to merge this with Matrix so that all our matrices become editable.
 */
public interface EditableMatrix<E,A,V> extends Matrix<E,A,V>
{
	public void setRow(int row, List<V> rowValues) throws MatrixException;
	
	public void setCol(int col, List<V> colValues) throws MatrixException;
	
	public void setRow(E row, List<V> rowValues) throws MatrixException;
	
	public void setCol(A col, List<V> colValues) throws MatrixException;
	
	public void setValue(int row, int col, V value) throws MatrixException;

	public void setValue(E rowName, A colName, V value) throws MatrixException;
	
	public void store() throws MatrixException;
}
