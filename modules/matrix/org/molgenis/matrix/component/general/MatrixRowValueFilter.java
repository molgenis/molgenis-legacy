package org.molgenis.matrix.component.general;

/**
 * Convenience class for defining MatrixQueryRule.Type.colHeader
 * Here you need both column AND value.property
 */
public class MatrixRowValueFilter extends MatrixQueryRule
{
	/** Value filter for unary operators such as SORTASC
	 * 
	 * @param colIndex the index of the column this value filter applies to
	 * @param colProperty the property of the value this filter applies to
	 * @param operator the operator
	 */
	public MatrixRowValueFilter(Integer colIndex, String colProperty, Operator operator)
	{
		super(MatrixQueryRule.Type.rowValueProperty, colIndex, colProperty, operator, null);
	}
	
	/** For binary operators such as EQUALS 
	 * 
	 * @param colIndex colum index
	 * @param property property of the value. E.g. 'protocolApplication'
	 * @param operator
	 * @param value 
	 */
	public MatrixRowValueFilter(Integer colIndex, String property, Operator operator, Object value)
	{
		super(MatrixQueryRule.Type.rowValueProperty, property,operator, value);
	}
}
