package matrix;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;

public class AbstractDataMatrixQueries
{
	
	public static AbstractDataMatrixInstance<Object> getSubMatrixFilterByRowMatrixValues(AbstractDataMatrixInstance<Object> dm, QueryRule... rules) throws Exception
	{

		// do checks. can be made generic. (moved to helper function)
		for (QueryRule rule : rules)
		{
			if (!dm.getRowNames().contains(rule.getField()))
			{
				throw new Exception("QueryRule invalid: no row named '" + rule.getField() + "'");
			}
			if (dm.getData().getValueType().equals("Decimal"))
			{
				// TODO: support all!
				if (!(rule.getOperator() == Operator.LESS || rule.getOperator() == Operator.GREATER))
				{
					throw new Exception("QueryRule invalid: operator not supported for decimal values");
				}
			}
			else if (dm.getData().getValueType().equals("Text"))
			{
				// TODO: support all!
				if (!(rule.getOperator() == Operator.EQUALS))
				{
					throw new Exception("QueryRule invalid: operator not supported for text values");
				}
			}
			else
			{
				throw new Exception("Unknown valuetype: " + dm.getData().getValueType());
			}
		}

		// get row/colnames
		List<String> rowNames = dm.getRowNames();
		List<String> colNames = new ArrayList<String>();

		for (QueryRule rule : rules)
		{

			// TODO: THIS IS JUST 1 POSSIBILITY FOR 1 QUERYRULE!!! EXPAND!!

			// get colindices for which QUERYRULE <-> values
			if (dm.getData().getValueType().equals("Decimal"))
			{
				if (rule.getOperator() == Operator.GREATER)
				{
					Object[] rowVals = dm.getRow(rule.getField());
					System.out.println("rowVals size: " + rowVals.length);
					for (int i = 0; i < rowVals.length; i++)
					{
						if (rowVals[i] == null)
						{
							System.out.println("NULL");
						}
						else
						{
							System.out.println(rowVals[i].toString());
							if (Double.parseDouble(rowVals[i].toString()) > Double.parseDouble(rule.getValue()
									.toString()))
							{
								System.out.println("   greater than " + rule.getValue().toString() + ", adding: "
										+ dm.getColNames().get(i));
								colNames.add(dm.getColNames().get(i));
							}
						}

					}
				}
			}
		}

		AbstractDataMatrixInstance res = dm.getSubMatrix(rowNames, colNames);
		return res;
	}
	
	

//	EQUALS  -  decimal + text
	/** 'field' in 'value' (value being a list). */
//	IN
	/** 'field' less-than 'value' */
//	LESS
	/** 'field' equal-or-less-than 'value' */
//	LESS_EQUAL
	/** 'field' greater-than 'value' */
//	GREATER
	/** 'field' equal-or-greater-than 'value' */
//	GREATER_EQUAL
	/** 'field' equal to '%value%' (% is a wildcard) */
//	LIKE
	/** 'field' not-equal to 'value' */
//	NOT
	/**
	 * limit results to 'value' elements (value being an int). The paramater
	 * 'field' is ommitted.
	 */
//	LIMIT
	/**
	 * show results from value-th element (value being an int offset
	 * starting from 1. The paramater 'field' is ommitted.
	 */
//	OFFSET
	/**
	 * order the result by 'field', ascending. The parameter 'value' is
	 * ommitted.
	 */
//	SORTASC
	/**
	 * order the result by 'field', descending. The parameter 'value' is
	 * ommitted.
	 */
//	SORTDESC
	/**
	 * AND operation
	 */
//	AND
	/**
	 * OR operation
	 */
//	OR
	/**
	 * indicates that 'value' is a nested array of QueryRule. The parameter
	 * 'field' is ommitted.
	 */
//	NESTED
	/** show the last elements from the list, so LIMIT from the end */
//	LAST
	/** enables the joining of two fields; value is a fieldname */
//	JOIN
	
	
}
