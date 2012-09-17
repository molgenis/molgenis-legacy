package org.molgenis.matrix.component.general;

/**
 * Convenience class for defining MatrixQueryRule.Type.colHeader
 * Here you need both column AND value.property
 */
public class MatrixColValueFilter extends MatrixQueryRule
{
	/** Value filter for unary operators such as SORTASC
	 * 
	 * @param colIndex the index of the column this value filter applies to
	 * @param colProperty the property of the value this filter applies to
	 * @param operator the operator
	 */
	public MatrixColValueFilter(Integer colIndex, String colProperty, Operator operator)
	{
		super(MatrixQueryRule.Type.colValueProperty, colIndex, colProperty, operator, colProperty);
	}
	
	/** For binary operators such as EQUALS */
	public MatrixColValueFilter(Integer colIndex, String colProperty, Operator operator, Object value)
	{
		super(MatrixQueryRule.Type.colValueProperty, colIndex, colProperty, operator, value);
	}
        
	public MatrixColValueFilter(int protocolId, Integer colIndex, String colProperty, Operator operator, Object value)
	{
		super(MatrixQueryRule.Type.colValueProperty, protocolId, colIndex, colProperty, operator, value);
	}        
        
        public MatrixColValueFilter(int protocolId, Integer measurementId, Operator operator, Object value)
	{
		super(MatrixQueryRule.Type.colValueProperty, protocolId, measurementId, operator, value);
        }
}
