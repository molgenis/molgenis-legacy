package org.molgenis.matrix.component;

import org.molgenis.framework.db.QueryRule;

public interface SliceableMatrix {

	/**
	 * Grab a matrix slice from the data backend by specifying coordinates. Row-
	 * and column indices start at 0 for the first element. Retrieving nRows 1,
	 * nCols 1 from index 0,0 returns the first (upper left most) element of the
	 * matrix. Also pass along the stepsize.
	 * 
	 * @param matrix
	 * @param rowIndex
	 * @param nRows
	 * @param colIndex
	 * @param nCols
	 * @return
	 * @throws Exception
	 */
	public RenderableMatrix getSubMatrixByOffset(RenderableMatrix matrix,
			int rowIndex, int nRows, int colIndex, int nCols, int stepSize)
			throws Exception;

	/**
	 * Get a submatrix from this matrix by applying a generic filter ('where')
	 * to a row of values in this matrix. For example, on a matrix where rows
	 * are Patients and columns are Phenotypes, a query could be 'JohnDoe',
	 * 'greater than', '35'. This results in the same amount of Patients, but a
	 * reduced amount of Phenotypes, because we selected only those Phenotypes
	 * for this Patient which have a value larger than 35.
	 */
	public RenderableMatrix getSubMatrixByRowValueFilter(
			RenderableMatrix matrix, QueryRule... rules) throws Exception;

	/**
	 * Get a submatrix from this matrix by applying a generic filter ('where')
	 * to the row headers. For example, on a matrix where rows are Patients and
	 * columns are Phenotypes, a query could be 'gender', 'equals', 'male'. This
	 * results in the same amount of Phenotypes, but a reduced amount of
	 * Patients. If the row headers are not entities but e.g. strings, only
	 * simple a compare on the value can be done instead of on some attribute.
	 */
	public RenderableMatrix getSubMatrixByRowHeaderFilter(
			RenderableMatrix matrix, QueryRule... rules) throws Exception;

	/**
	 * Get a submatrix from this matrix by applying a generic filter ('where')
	 * to a column of values in this matrix. For example, on a matrix where rows
	 * are Patients and columns are Phenotypes, a query could be 'height',
	 * 'greater than', '175'. This results in the same amount of Phenotypes, but
	 * a reduced amount of Patients, because we selected only those Patients for
	 * this Phenotype which have a value larger than 175.
	 */
	public RenderableMatrix getSubMatrixByColValueFilter(
			RenderableMatrix matrix, QueryRule... rules) throws Exception;

	/**
	 * Get a submatrix from this matrix by applying a generic filter ('where')
	 * to the column headers. For example, on a matrix where rows are Patients
	 * and columns are Phenotypes, a query could be 'unit', 'equals', 'gram'.
	 * This results in the same amount of Patients, but a reduced amount of
	 * Phenotypes. If the column headers are not entities but e.g. strings, only
	 * simple a compare on the value can be done instead of on some attribute.
	 */
	public RenderableMatrix getSubMatrixByColHeaderFilter(
			RenderableMatrix matrix, QueryRule... rules) throws Exception;
}
