package org.molgenis.matrix.component.general;

import org.molgenis.framework.db.QueryRule;

public class MatrixQueryRule extends QueryRule
{
	public enum Type {
		rowHeader, colHeader, rowValues, colValues, index, paging
	}
	
	private Type filterType;
	
	
	/**
	 * Special constructor for QueryRules in the context of Matrix. Allows more
	 * combinations needed for twodimensional data filtering.
	 */
	public MatrixQueryRule(Type type, String field, Operator operator, Object value)
	{
		this.filterType = type;
		
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

	public Type getFilterType() {
		return filterType;
	}

	public String toString(){
		return "Filter type: " + this.getFilterType() + ", queryrule: " + super.toString();
	}

}
