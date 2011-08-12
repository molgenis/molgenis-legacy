package matrix;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.core.Nameable;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;

public class AbstractDataMatrixQueries
{

	/**
	 * Apply filters (query rules) to the values of a matrix, to get a new
	 * subset matrix back. The 'field' specifies the row name where the filter is
	 * applied. And as expected: The 'operator' is the comparator, and 'value'
	 * is the point of reference.
	 * 
	 * @param dm
	 * @param rules
	 * @return AbstractDataMatrixInstance
	 * @throws Exception
	 */
	public static AbstractDataMatrixInstance<Object> getSubMatrixFilterByRowMatrixValues(
			AbstractDataMatrixInstance<Object> dm, QueryRule ... rules) throws Exception
	{
		checkQueryRules(dm, false, rules);

		// colNames is the resultset we want to get
		List<String> colNames = null;
		List<String> rowNames = dm.getRowNames();

		// iterate over queryrules
		for (QueryRule rule : rules)
		{
			List<String> result = null;
			if (dm.getData().getValueType().equals("Decimal"))
			{
				double value = Double.parseDouble(rule.getValue().toString());
				result = selectUsingDecimal(dm.getRow(rule.getField()), value, rule.getOperator(), dm.getColNames());
			}
			else
			{
				String value = rule.getValue().toString();
				result = selectUsingText(dm.getRow(rule.getField()), value, rule.getOperator(), dm.getColNames());
			}

			if (colNames == null)
			{
				// first queryrule being applied, store results in colnames
				colNames = result;
			}
			else
			{
				// consecutively: basically, applying an AND operator here
				// by removing result from colnames.. so OR not supported
				colNames.removeAll(result);
			}

			if (colNames.size() == 0)
			{
				throw new Exception("No colnames in resultset, empty matrix!");
			}
		}

		AbstractDataMatrixInstance<Object> res = dm.getSubMatrix(rowNames, colNames);

		return res;
	}

	/**
	 * Apply filters (query rules) to the values of a matrix, to get a new
	 * subset matrix back. The 'field' specifies the column name where the filter is
	 * applied. And as expected: The 'operator' is the comparator, and 'value'
	 * is the point of reference.
	 * 
	 * @param dm
	 * @param rules
	 * @return AbstractDataMatrixInstance
	 * @throws Exception
	 */
	public static AbstractDataMatrixInstance<Object> getSubMatrixFilterByColMatrixValues(
			AbstractDataMatrixInstance<Object> dm, QueryRule ... rules) throws Exception
	{
		checkQueryRules(dm, true, rules);

		// rowNames is the resultset we want to get
		List<String> colNames = dm.getColNames();
		List<String> rowNames = null;

		// iterate over queryrules
		for (QueryRule rule : rules)
		{
			List<String> result = null;
			if (dm.getData().getValueType().equals("Decimal"))
			{
				double value = Double.parseDouble(rule.getValue().toString());
				result = selectUsingDecimal(dm.getCol(rule.getField()), value, rule.getOperator(), dm.getRowNames());
			}
			else
			{
				String value = rule.getValue().toString();
				result = selectUsingText(dm.getCol(rule.getField()), value, rule.getOperator(), dm.getRowNames());
			}

			if (rowNames == null)
			{
				// first queryrule being applied, store results in rownames
				rowNames = result;
			}
			else
			{
				// consecutively: basically, applying an AND operator here
				// by removing result from rownames.. so OR not supported
				rowNames.removeAll(result);
			}

			if (rowNames.size() == 0)
			{
				throw new Exception("No rownames in resultset, empty matrix!");
			}
		}

		AbstractDataMatrixInstance<Object> res = dm.getSubMatrix(rowNames, colNames);

		return res;
	}

	public static AbstractDataMatrixInstance<Object> getSubMatrixFilterByRowEntityValues(
			AbstractDataMatrixInstance<Object> dm, Database db, QueryRule... rules) throws Exception
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
			AbstractDataMatrixInstance<Object> dm, Database db, QueryRule... rules) throws Exception
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

	public static List<String> selectUsingText(Object[] values, String value, Operator op, List<String> dimNames)
			throws Exception
	{
		List<String> resultNames = new ArrayList<String>();
		for (int i = 0; i < values.length; i++)
		{
			if (values[i] != null)
			{
				boolean add = false;
				if (op == Operator.EQUALS)
				{
					if (values[i].toString().equals(value))
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
					resultNames.add(dimNames.get(i));
				}
			}
		}
		return resultNames;
	}

	public static List<String> selectUsingDecimal(Object[] values, double value, Operator op, List<String> dimNames)
			throws Exception
	{
		List<String> resultNames = new ArrayList<String>();
		for (int i = 0; i < values.length; i++)
		{
			if (values[i] != null)
			{
				boolean add = false;
				if (op == Operator.GREATER)
				{
					if (Double.parseDouble(values[i].toString()) > value)
					{
						add = true;
					}
				}
				else if (op == Operator.LESS)
				{
					if (Double.parseDouble(values[i].toString()) < value)
					{
						add = true;
					}
				}
				else if (op == Operator.GREATER_EQUAL)
				{
					if (Double.parseDouble(values[i].toString()) >= value)
					{
						add = true;
					}
				}
				else if (op == Operator.LESS_EQUAL)
				{
					if (Double.parseDouble(values[i].toString()) <= value)
					{
						add = true;
					}
				}
				else if (op == Operator.EQUALS)
				{
					if (Double.parseDouble(values[i].toString()) == value)
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
					resultNames.add(dimNames.get(i));
				}
			}
		}
		return resultNames;
	}

	private static void checkQueryRules(AbstractDataMatrixInstance<Object> dm, boolean appliedOnColumns, QueryRule... rules) throws Exception
	{
		for (QueryRule rule : rules)
		{
			if (rule.getField() == null)
			{
				throw new Exception("QueryRule invalid: field is null");
			}
			if (rule.getValue() == null)
			{
				throw new Exception("QueryRule invalid: value is null");
			}
			if (rule.getOperator() == null)
			{
				throw new Exception("QueryRule invalid: operator is null");
			}
			if (appliedOnColumns && !dm.getColNames().contains(rule.getField()))
			{
				throw new Exception("QueryRule invalid: no column named '" + rule.getField() + "'");
			}
			if (!appliedOnColumns && !dm.getRowNames().contains(rule.getField()))
			{
				throw new Exception("QueryRule invalid: no row named '" + rule.getField() + "'");
			}
		}
	}

}
