package matrix;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.core.Nameable;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;

import app.JDBCDatabase;

public class AbstractDataMatrixQueries
{

	public static AbstractDataMatrixInstance<Object> getSubMatrixFilterByRowMatrixValues(
			AbstractDataMatrixInstance<Object> dm, QueryRule... rules) throws Exception
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
			if (dm.getData().getValueType().equals("Decimal"))
			{
				double value = Double.parseDouble(rule.getValue().toString());
				colNames = select(dm.getRow(rule.getField()), value, rule.getOperator(), dm.getColNames());
			}
			else
			{
				throw new Exception("Unsupported getValueType TEXT");
			}
		}

		AbstractDataMatrixInstance<Object> res = dm.getSubMatrix(rowNames, colNames);
		return res;
	}

	private static List<String> select(Object[] rowVals, double value, Operator op, List<String> colNames)
			throws Exception
	{
		List<String> colNamesResult = new ArrayList<String>();
		for (int i = 0; i < rowVals.length; i++)
		{
			if (rowVals[i] != null)
			{
				boolean add = false;
				if (op == Operator.GREATER)
				{
					if (Double.parseDouble(rowVals[i].toString()) > value)
					{
						add = true;
					}
				}
				else if (op == Operator.LESS)
				{
					if (Double.parseDouble(rowVals[i].toString()) < value)
					{
						add = true;
					}
				}
				else if (op == Operator.GREATER_EQUAL)
				{
					if (Double.parseDouble(rowVals[i].toString()) >= value)
					{
						add = true;
					}
				}
				else if (op == Operator.LESS_EQUAL)
				{
					if (Double.parseDouble(rowVals[i].toString()) <= value)
					{
						add = true;
					}
				}
				else if (op == Operator.EQUALS)
				{
					if (Double.parseDouble(rowVals[i].toString()) == value)
					{
						add = true;
					}
				}
				else
				{
					throw new Exception("Unsupported operation: " + op.toString());
				}

				if (add == true)
				{
					colNamesResult.add(colNames.get(i));
				}
			}
		}
		return colNamesResult;
	}

	public static AbstractDataMatrixInstance<Object> getSubMatrixFilterByColMatrixValues(
			AbstractDataMatrixInstance<Object> abstractDataMatrixInstance, QueryRule[] rules)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public static AbstractDataMatrixInstance<Object> getSubMatrixFilterByRowEntityValues(
			AbstractDataMatrixInstance<Object> dm, JDBCDatabase db, QueryRule... rules) throws Exception
	{
		List<String> colNames = dm.getColNames();
		// 1. query on row type entities
		Query q = db.query(db.getClassForName(dm.getData().getTargetType()));
		// 2. entities must be present in row names
		q.addRules(new QueryRule("name", Operator.IN, dm.getRowNames()));
		// 3. now add other rules
		q.addRules(rules);
		List<Nameable> subRow = q.find();
		List<String> rowNames = new ArrayList<String>();
		for (Nameable i : subRow)
		{
			rowNames.add(i.getName());
		}
		AbstractDataMatrixInstance res = dm.getSubMatrix(rowNames, colNames);
		return res;
	}

	public static AbstractDataMatrixInstance<Object> getSubMatrixFilterByColEntityValues(
			AbstractDataMatrixInstance<Object> dm, JDBCDatabase db, QueryRule... rules) throws Exception
	{
		List<String> rowNames = dm.getRowNames();
		// 1. query on column type entities
		Query q = db.query(db.getClassForName(dm.getData().getFeatureType()));
		// 2. entities must be present in column names
		q.addRules(new QueryRule("name", Operator.IN, dm.getColNames()));
		// 3. now add other rules
		q.addRules(rules);
		List<Nameable> subCol = q.find();
		List<String> colNames = new ArrayList<String>();
		for (Nameable i : subCol)
		{
			colNames.add(i.getName());
			System.out.println("ADDED: " + i.getName());
		}
		AbstractDataMatrixInstance res = dm.getSubMatrix(rowNames, colNames);
		return res;
	}

	// EQUALS - decimal + text
	/** 'field' in 'value' (value being a list). */
	// IN
	/** 'field' less-than 'value' */
	// LESS
	/** 'field' equal-or-less-than 'value' */
	// LESS_EQUAL
	/** 'field' greater-than 'value' */
	// GREATER
	/** 'field' equal-or-greater-than 'value' */
	// GREATER_EQUAL
	/** 'field' equal to '%value%' (% is a wildcard) */
	// LIKE
	/** 'field' not-equal to 'value' */
	// NOT
	/**
	 * limit results to 'value' elements (value being an int). The paramater
	 * 'field' is ommitted.
	 */
	// LIMIT
	/**
	 * show results from value-th element (value being an int offset starting
	 * from 1. The paramater 'field' is ommitted.
	 */
	// OFFSET
	/**
	 * order the result by 'field', ascending. The parameter 'value' is
	 * ommitted.
	 */
	// SORTASC
	/**
	 * order the result by 'field', descending. The parameter 'value' is
	 * ommitted.
	 */
	// SORTDESC
	/**
	 * AND operation
	 */
	// AND
	/**
	 * OR operation
	 */
	// OR
	/**
	 * indicates that 'value' is a nested array of QueryRule. The parameter
	 * 'field' is ommitted.
	 */
	// NESTED
	/** show the last elements from the list, so LIMIT from the end */
	// LAST
	/** enables the joining of two fields; value is a fieldname */
	// JOIN

}
