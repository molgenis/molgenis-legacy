package org.molgenis.matrix.component.general;

/**
 * Convenience class for defining MatrixQueryRule.Type.colHeader
 */
public class MatrixColHeaderFilter extends MatrixQueryRule
{
	/** For unary operators such as SORTASC */
	public MatrixColHeaderFilter(String property, Operator operator)
	{
		super(MatrixQueryRule.Type.colHeader, property,operator, null);
	}
	
	/** For binary operators such as EQUALS */
	public MatrixColHeaderFilter(String property, Operator operator, Object value)
	{
		super(MatrixQueryRule.Type.colHeader, property,operator, value);
	}
}
