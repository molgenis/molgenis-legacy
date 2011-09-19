package org.molgenis.matrix.component.interfaces;

import java.util.List;

import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.MatrixException;

/**
 * Basic abstraction to deal with matrix formed data.
 * 
 * @param <R>
 *            metadata describing rows. May be simple String or advanced Sample
 *            class.
 * @param <C>
 *            metadata describing columns. May be simple String or advanced
 *            Sample class.
 * @param <V>
 *            type of the values (cells of the matrix). Typically Integer,
 *            String but can also be complex type or List<?>.
 */
public interface BasicMatrix<R, C, V>
{

	/**
	 * Row metadata of this matrix
	 */
	public List<R> getRowHeaders() throws MatrixException;

	/**
	 * Column metadata of this matrix.
	 */
	public List<C> getColHeaders() throws MatrixException;

	/**
	 * The unique indices of the row elements.
	 * 
	 * These do not be 0-rowCount but can be any identifiers that are convenient
	 * (e.g. database identifiers).
	 */
	public List<Integer> getRowIndices() throws MatrixException;

	/**
	 * The unique indices of the column elements.
	 * 
	 * These do not be 0-colCount but can be any identifiers that are convenient
	 * (e.g. database identifiers).
	 */
	public List<Integer> getColIndices() throws MatrixException;

	/**
	 * The currently selected matrix values that are going to be rendered in the
	 * view.
	 */
	public V[][] getValues() throws MatrixException;

	/**
	 * Efficiently return the column count of this matrix.
	 * 
	 * @return columnCount
	 */
	public Integer getColCount() throws MatrixException;

	/**
	 * Efficiently return the row count of this matrix
	 * 
	 * @return rowCount
	 */
	public Integer getRowCount() throws MatrixException;

	/**
	 * Will reload the matrix from backend (if applicable).
	 * @throws MatrixException 
	 */
	void refresh() throws MatrixException;



}
