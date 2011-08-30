package org.molgenis.matrix.component.interfaces;

import java.util.List;

import org.molgenis.framework.db.QueryRule;
import org.molgenis.matrix.component.general.MatrixQueryRule;



public interface SliceableMatrix<R, C, V>
{

	/** Get list of current rules */
	//public List<MatrixQueryRule> getFilter();
	
	
	/**
	 * Example 1: QueryRule("col", Operator.GREATER, 20) Grab a matrix slice by
	 * column index. Remember indices start from 0. GREATER 20 on a matrix with
	 * 35 columns will slice off the first 21 columns and keep the last 14.
	 * EQUALS will result in a single column matrix, provided the index given is
	 * available in the current matrix.
	 * 
	 * Example 2: QueryRule("row", Operator.LESS_EQUAL, 10) Grab a matrix slice
	 * by row index. Remember indices start from 0. For example: LESS_EQUAL 10
	 * on a matrix with 25 rows will slice off the last 14 rows and keep the
	 * first 11. EQUALS will result in a single row matrix, provided the index
	 * given is available in the current matrix.
	 * 
	 */
	public SliceableMatrix<R, C, V> sliceByIndex(MatrixQueryRule rule) throws Exception;

	/**
	 * Example 1: QueryRule("col", Operator.OFFSET, 10) plus QueryRule("col",
	 * Operator.LIMIT, 10) From the current matrix, grab columns ten through
	 * twenty and discard the rest. Different from index-slicing because this
	 * happens regardless of indices and it therefore enables paging of results.
	 * 
	 * Example 2: QueryRule("row", Operator.OFFSET, 5) Grab all rows starting
	 * from the fifth row onward (not index 5) from the current matrix and
	 * discard the rest.
	 * 
	 * Example 3: QueryRule("row", Operator.LIMIT, 15) Grab the first fifteen
	 * rows from the result and discard the rest.
	 */
	public SliceableMatrix<R, C, V> sliceByPaging(MatrixQueryRule rule) throws Exception;

	/**
	 * Get a submatrix from this matrix by applying a generic filter ('where')
	 * to a row of values in this matrix. For example, on a matrix where rows
	 * are Patients and columns are Phenotypes, a query could be 'JohnDoe',
	 * 'greater than', '35'. This results in the same amount of Patients, but a
	 * reduced amount of Phenotypes, because we selected only those Phenotypes
	 * for this Patient which have a value larger than 35.
	 * 
	 * Example: (TODO: per index, as string?) QueryRule("2", Operator.GREATER, 35)
	 */
	public SliceableMatrix<R, C, V> sliceByRowValues(QueryRule rule) throws Exception;

	/**
	 * Get a submatrix from this matrix by applying a generic filter ('where')
	 * to a column of values in this matrix. For example, on a matrix where rows
	 * are Patients and columns are Phenotypes, a query could be 'height',
	 * 'greater than', '175'. This results in the same amount of Phenotypes, but
	 * a reduced amount of Patients, because we selected only those Patients for
	 * this Phenotype which have a value larger than 175.
	 * 
	 * Example: (TODO: per index, as string?) QueryRule("5", Operator.GREATER, 175)
	 */
	public SliceableMatrix<R, C, V> sliceByColValues(MatrixQueryRule rule) throws Exception;

	/**
	 * Get a submatrix from this matrix by applying a generic filter ('where')
	 * to the row headers. For example, on a matrix where rows are Patients and
	 * columns are Phenotypes, a query could be 'gender', 'equals', 'male'. This
	 * results in the same amount of Phenotypes, but a reduced amount of
	 * Patients. If the row headers are not entities but e.g. strings, only
	 * simple a compare on the value can be done instead of on some attribute.
	 * 
	 * Example: QueryRule("name", Operator.EQUALS, "rs562378")
	 */
	public SliceableMatrix<R, C, V> sliceByRowHeader(MatrixQueryRule rule) throws Exception;

	/**
	 * Get a submatrix from this matrix by applying a generic filter ('where')
	 * to the column headers. For example, on a matrix where rows are Patients
	 * and columns are Phenotypes, a query could be 'unit', 'equals', 'gram'.
	 * This results in the same amount of Patients, but a reduced amount of
	 * Phenotypes. If the column headers are not entities but e.g. strings, only
	 * simple a compare on the value can be done instead of on some attribute.
	 * 
	 * Example: QueryRule("chromosome_name", Operator.EQUALS, "chr3")
	 */
	public SliceableMatrix<R, C, V> sliceByColHeader(MatrixQueryRule rule) throws Exception;

	/**
	 * When done slicing, get the result. This implies that you kept track of
	 * the state of your matrix during the slicing and are now able to return
	 * the columns, rows and values.
	 */
	public BasicMatrix<R, C, V> getResult() throws Exception;

	/**
	 * Before slicing, or after the result has been retrieved, reset the
	 * SliceableMatrix to a fresh state where slice actions are once again
	 * performed on the original matrix data instead of a sliced subset.
	 */
	public void createFresh() throws Exception;
}
