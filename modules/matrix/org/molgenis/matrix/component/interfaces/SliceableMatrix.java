package org.molgenis.matrix.component.interfaces;

import java.util.List;

import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.general.MatrixQueryRule;

/**
 * Sliceable version of the matrix
 * 
 * @author mswertz
 * 
 * @param <R>
 * @param <C>
 * @param <V>
 */
public interface SliceableMatrix<R, C, V> extends BasicMatrix<R, C, V>
{

	/**
	 * The applied filters.
	 */
	public List<MatrixQueryRule> getRules();

	/**
	 * Generic slicing method. Prefered is to use the convenience methods
	 * 'scliceByXYZ'
	 * 
	 * @throws MatrixException
	 * @See MatrixQueryRule
	 */
	public SliceableMatrix<R, C, V> slice(MatrixQueryRule rule)
			throws MatrixException;

	/**
	 * Example 1: sliceByColIndex(Operator.GREATER, 20) Grab a matrix slice by
	 * column index. This is not the same of limit-offset paging. A matrix could
	 * (after some previous slice action) consist of row indices 20-30. 'Limit
	 * 5' would result in indices 20-25, whereas 'index < 5' would not not
	 * affect this set. Using this function you could say e.g.
	 * "give me patients 10-20 and 35-40". The paging filters are then applied
	 * to e.g. show only the first 5 of the resulting 15 patients.
	 * 
	 * NB: indexes should be unique but a matrix doesn't necessarily have
	 * sequential indexes from 0-count(col)!
	 * 
	 * @param operator
	 * @param index
	 * @return sliced result
	 * @throws Exception
	 */
	public SliceableMatrix<R, C, V> sliceByColIndex(
			QueryRule.Operator operator, Integer index) throws Exception;

	/**
	 * Example 2: sliceByColIndex(Operator.LESS_EQUAL, 10) Grab a matrix slice
	 * by row index.
	 * 
	 * @param operator
	 * @param index
	 * @return sliced result
	 * @throws Exception
	 */
	public SliceableMatrix<R, C, V> sliceByRowIndex(
			QueryRule.Operator operator, Integer index) throws Exception;

	/**
	 * Example: sliceByRowLimitOffset(10,30)
	 * 
	 * @param limit
	 * @param offset
	 * @return
	 */
	public SliceableMatrix<R, C, V> sliceByRowOffsetLimit(int limit, int offset)
			throws Exception;

	/**
	 * Example: sliceByColLimitOffset(10,30)
	 * 
	 * @param limit
	 * @param offset
	 * @return
	 */
	public SliceableMatrix<R, C, V> sliceByColOffsetLimit(int limit, int offset)
			throws Exception;

	/**
	 * Get a submatrix from this matrix by applying a generic filter ('where')
	 * to a row of values in this matrix. For example, on a matrix where rows
	 * are Patients and columns are Phenotypes, a query could be 'JohnDoe',
	 * 'greater than', '35'. This results in the same amount of Patients, but a
	 * reduced amount of Phenotypes, because we selected only those Phenotypes
	 * for this Patient which have a value larger than 35.
	 * 
	 * Example: sliceByRowValues(2, Operator.GREATER, 35)
	 */
	public SliceableMatrix<R, C, V> sliceByRowValues(int index,
			Operator operator, Object value) throws Exception;

	/**
	 * Get a submatrix from this matrix by applying a generic filter ('where')
	 * to a row of values in this matrix. For example, on a matrix where rows
	 * are Patients and columns are Phenotypes, a query could be 'JohnDoe',
	 * 'greater than', '35'. This results in the same amount of Patients, but a
	 * reduced amount of Phenotypes, because we selected only those Phenotypes
	 * for this Patient which have a value larger than 35.
	 * 
	 * Example: sliceByRowValues(Individual('age'), Operator.GREATER, 35)
	 */
	// J: how do you identify R ?
	// M: that depends on your implementation. If C extends Entity, then simply
	// C.getIdValue(). This would then be the index. If C instanceof String,
	// then they should be unique anyway.
	public SliceableMatrix<R, C, V> sliceByRowValues(R row, Operator operator,
			Object value) throws Exception;

	/**
	 * Get a submatrix from this matrix by applying a generic filter ('where')
	 * to a column of values in this matrix. For example, on a matrix where rows
	 * are Patients and columns are Phenotypes, a query could be 'height',
	 * 'greater than', '175'. This results in the same amount of Phenotypes, but
	 * a reduced amount of Patients, because we selected only those Patients for
	 * this Phenotype which have a value larger than 175.
	 * 
	 * sliceByColValues("5", Operator.GREATER, 175)
	 */
	public SliceableMatrix<R, C, V> sliceByColValues(int index,
			Operator operator, Object value) throws Exception;

	/**
	 * Get a submatrix from this matrix by applying a generic filter ('where')
	 * to a column of values in this matrix. For example, on a matrix where rows
	 * are Patients and columns are Phenotypes, a query could be 'height',
	 * 'greater than', '175'. This results in the same amount of Phenotypes, but
	 * a reduced amount of Patients, because we selected only those Patients for
	 * this Phenotype which have a value larger than 175.
	 * 
	 * sliceByColValues(ObservableFeature('height'), Operator.GREATER, 175)
	 */
	// J: how do you identify C ?
	// M: that depends on your implementation. If C extends Entity, then simply
	// C.getIdValue(). This would then be the index.
	public SliceableMatrix<R, C, V> sliceByColValues(C col, Operator operator,
			Object value) throws Exception;

	/**
	 * Get a submatrix from this matrix by applying a generic filter ('where')
	 * to the row headers. For example, on a matrix where rows are Patients and
	 * columns are Phenotypes, a query could be 'gender', 'equals', 'male'. This
	 * results in the same amount of Phenotypes, but a reduced amount of
	 * Patients. If the row headers are not entities but e.g. strings, only
	 * simple a compare on the value can be done instead of on some attribute.
	 * 
	 * Example: sliceByRowProperty("name", Operator.EQUALS, "rs562378")
	 * @throws MatrixException 
	 */
	public SliceableMatrix<R, C, V> sliceByRowProperty(String property,
			Operator operator, Object value) throws MatrixException;

	/**
	 * Get a submatrix from this matrix by applying a generic filter ('where')
	 * to the column headers. For example, on a matrix where rows are Patients
	 * and columns are Phenotypes, a query could be 'unit', 'equals', 'gram'.
	 * This results in the same amount of Patients, but a reduced amount of
	 * Phenotypes. If the column headers are not entities but e.g. strings, only
	 * simple a compare on the value can be done instead of on some attribute.
	 * 
	 * Example: sliceColProperty("chromosome_name", Operator.EQUALS, "chr3")
	 * 
	 * @throws Exception
	 */
	public SliceableMatrix<R, C, V> sliceByColProperty(String property,
			Operator operator, Object value) throws Exception;
	
	/**
	 * Perform slicing on other properties of the value. In more complex
	 * matrices we could have a class 'ObservedValue' that has 'startdate' on
	 * which we may want to filter.
	 * 
	 * @param col: the column to filter on
	 * @param property. e.g. 'protocol_name'
	 * @param operator. e.g. 'equals'
	 * @param value. e.g. 'alignment 2.0'
	 * @return
	 * @throws MatrixException 
	 */
	public SliceableMatrix<R, C, V> sliceByColValueProperty(C col, String property,
			Operator operator, Object value) throws MatrixException;
	
	/**
	 * Perform slicing on other properties of the value. In more complex
	 * matrices we could have a class 'ObservedValue' that has 'startdate' on
	 * which we may want to filter.
	 * 
	 * @param colIndex to put the filter on
	 * @param operator. e.g. 'equals'
	 * @param value. e.g. 'alignment 2.0'
	 * @return
	 * @throws MatrixException 
	 */
	SliceableMatrix<R, C, V> sliceByColValueProperty(int colIndex,
			String property, Operator operator, Object value)
			throws MatrixException;

	/**
	 * When done slicing, get the result. This implies that you kept track of
	 * the state of your matrix during the slicing and are now able to return
	 * the columns, rows and values.
	 */
	public BasicMatrix<R, C, V> getResult() throws Exception;
	
	/**
	 * Tells slicer on which row header attributes the user can filter. If
	 * the row headers are strings, you could implement this with just "value".
	 * If your headers are entities, you might want to fill this list with the
	 * attributes of the entity.
	 */
	public List<String> getRowPropertyNames();

	/**
	 * Same as getRowHeaderFilterAttributes() except for columns.
	 */
	public List<String> getColPropertyNames();
	
	/** Meta data on the values. E.g. in ObservedValue you would get list('value','protocol_name', etc);
	 * 
	 * @return
	 */
	public List<String> getValuePropertyNames();

	/**
	 * Before slicing, or after the result has been retrieved, reset the
	 * SliceableMatrix to a fresh state where slice actions are once again
	 * performed on the original matrix data instead of a sliced subset.
	 */
	public void reset() throws MatrixException;
	
	/**
	 * Empty caches and reload matrix data, whilst keeping any
	 * filters intact.
	 * 
	 * @throws MatrixException
	 */
	public void reload() throws MatrixException;
	
	public int getRowLimit();

	public void setRowLimit(int rowLimit);

	public int getRowOffset();

	public void setRowOffset(int rowOffset);

	public int getColLimit();

	public void setColLimit(int colLimit);

	public int getColOffset();

	public void setColOffset(int colOffset);

	SliceableMatrix<R, C, V> sliceByRowValueProperty(R row, String property,
			Operator operator, Object value) throws MatrixException;

	SliceableMatrix<R, C, V> sliceByRowValueProperty(int rowIndex,
			String property, Operator operator, Object value)
			throws MatrixException;

	SliceableMatrix<R, C, V> sliceByColValueProperty(int protocolId, int measurementId,
			String valuePropertyToUse, Operator op, Object object) throws MatrixException;
}
