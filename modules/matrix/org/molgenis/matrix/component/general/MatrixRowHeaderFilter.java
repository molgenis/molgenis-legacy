package org.molgenis.matrix.component.general;

/**
 * Convenience class for defining MatrixQueryRule.Type.rowHeader
 */
public class MatrixRowHeaderFilter extends MatrixQueryRule
{
	/** For unary operators such as SORTASC */
	public MatrixRowHeaderFilter(String property, Operator operator)
	{
		super(MatrixQueryRule.Type.rowHeader, property,operator, null);
	}
	
	/** For binary operators such as EQUALS */
	public MatrixRowHeaderFilter(String property, Operator operator, Object value)
	{
		super(MatrixQueryRule.Type.rowHeader, property,operator, value);
	}
}
