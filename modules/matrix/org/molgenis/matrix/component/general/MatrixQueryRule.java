package org.molgenis.matrix.component.general;

import org.molgenis.framework.db.QueryRule;

public class MatrixQueryRule extends QueryRule
{

	/**
	 * Special constructor for QueryRules in the context of Matrix. Allows more
	 * combinations needed for twodimensional data filtering.
	 */
	public MatrixQueryRule(String field, Operator operator, Object value)
	{
		if (operator == Operator.SORTASC || operator == Operator.SORTDESC || operator == Operator.LAST
				|| operator == Operator.AND || operator == Operator.OR)
		{
			throw new IllegalArgumentException("QueryRule(): Operator." + operator
					+ " cannot be used with two arguments");
		}
		this.setField(field);
		this.setOperator(operator);
		this.setValue(value);
	}
	
	@Override
	public String toString(){
		return this.field + " " + this.operator + " " + this.value;
	}

}
