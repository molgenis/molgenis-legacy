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
	public static DataMatrixInstance getSubMatrixFilterByRowMatrixValues(
			DataMatrixInstance dm, QueryRule ... rules) throws Exception
	{
		checkQueryRulesOld(dm, false, rules);

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

		DataMatrixInstance res = dm.getSubMatrix(rowNames, colNames);

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
	public static DataMatrixInstance getSubMatrixFilterByColMatrixValues(
			DataMatrixInstance dm, QueryRule ... rules) throws Exception
	{
		checkQueryRulesOld(dm, true, rules);

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

		DataMatrixInstance res = dm.getSubMatrix(rowNames, colNames);

		return res;
	}

	public static DataMatrixInstance getSubMatrixFilterByRowEntityValues(
			DataMatrixInstance dm, Database db, QueryRule... rules) throws Exception
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
		if (rowNames.size() == 0)
		{
			throw new Exception("No rownames in resultset, empty matrix!");
		}
		
		DataMatrixInstance res = dm.getSubMatrix(rowNames, colNames);
		return res;
	}

	public static DataMatrixInstance getSubMatrixFilterByColEntityValues(
			DataMatrixInstance dm, Database db, QueryRule... rules) throws Exception
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
		}
		if (colNames.size() == 0)
		{
			throw new Exception("No colnames in resultset, empty matrix!");
		}
		DataMatrixInstance res = dm.getSubMatrix(rowNames, colNames);
		return res;
	}



	public static DataMatrixInstance getSubMatrixByRowValueFilter(
			DataMatrixInstance matrix, QueryRule ... rules) throws Exception {
		
		checkQueryRules(rules);
		
		// colNames is the resultset we want to get
		List<String> colNames = null;
		List<String> rowNames = matrix.getRowNames();

		// iterate over queryrules
		for (QueryRule rule : rules)
		{
			List<String> result = null;
			if (matrix.getData().getValueType().equals("Decimal"))
			{
				double value = Double.parseDouble(rule.getValue().toString());
				result = selectUsingDecimal(matrix.getRow(Integer.parseInt(rule.getField())), value, rule.getOperator(), matrix.getColNames());
			}
			else
			{
				String value = rule.getValue().toString();
				result = selectUsingText(matrix.getRow(Integer.parseInt(rule.getField())), value, rule.getOperator(), matrix.getColNames());
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
		
		DataMatrixInstance res = matrix.getSubMatrix(rowNames, colNames);

		return res;
	
	}
	
	public static DataMatrixInstance getSubMatrixFilterByIndex(
			DataMatrixInstance matrix, QueryRule... rules) throws Exception
	{
		checkQueryRules(rules);
		
		if(rules.length != 1)
		{
			throw new Exception("at the moment supports only 1 QueryRule at a time");
		}
		
		String field = rules[0].getField();
		Operator op = rules[0].getOperator();
		int value = Integer.parseInt(rules[0].getValue().toString());
		
		if(value < 0)
		{
			throw new Exception("Negative numbers not allowed");
		}
		
		if(field.equals("row"))
		{
			int row = getOffset(op, value);
			int nRows = getLimit(matrix.getNumberOfRows(), op, value);
			if(nRows < 1){
				throw new Exception("No rows in resultset, empty matrix!");
			}
			return matrix.getSubMatrixByOffset(row, nRows, 0, matrix.getNumberOfCols());
		}
		else if(field.equals("col"))
		{
			int col = getOffset(op, value);
			int nCols = getLimit(matrix.getNumberOfCols(), op, value);
			if(nCols < 1){
				throw new Exception("No cols in resultset, empty matrix!");
			}
			return matrix.getSubMatrixByOffset(0, matrix.getNumberOfRows(), col, nCols);
		}
		else
		{
			throw new Exception("field is not 'row' or 'col'");
		}
		
	}
	
	
	
	public static int getOffset(Operator op, int value) throws Exception
	{
		if (op == Operator.EQUALS || op == Operator.GREATER_EQUAL)
		{
			return value;
		}
		else if (op == Operator.GREATER)
		{
			return value + 1;
		}
		else if(op == Operator.LESS || op == Operator.LESS_EQUAL)
		{
			return 0;
		}
		else
		{
			throw new Exception("unsupported operator " + op);
		}
	}
	
	public static int getLimit(int numberOfDimElems, Operator op, int value) throws Exception
	{
		if (op == Operator.LESS_EQUAL)
		{
			return value;
		}
		else if (op == Operator.EQUALS)
		{
			return 1;
		}
		else if (op == Operator.GREATER)
		{
			return numberOfDimElems - value - 1;
		}
		else if (op == Operator.GREATER_EQUAL)
		{
			return numberOfDimElems - value ;
		}
		else if (op == Operator.LESS)
		{
			return value - 1;
		}
		else
		{
			throw new Exception("unsupported operator " + op);
		}
	}

	//TODO: merge with selectUsingDecimal? or does it have special needs?
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
					//using .equals here has the same effect for doubles as ==
					if (values[i].toString().equals(value))
					{
						add = true;
					}
				}
				
				//attempt to parse to double and compare in non-EQUAL cases!
				//for non-numerics: fails with java.lang.NumberFormatException: For input string "lalala"
				else if (op == Operator.GREATER)
				{
					if (Double.parseDouble(values[i].toString()) > Double.parseDouble(value))
					{
						add = true;
					}
				}
				else if (op == Operator.LESS)
				{
					if (Double.parseDouble(values[i].toString()) < Double.parseDouble(value))
					{
						add = true;
					}
				}
				else if (op == Operator.GREATER_EQUAL)
				{
					if (Double.parseDouble(values[i].toString()) >= Double.parseDouble(value))
					{
						add = true;
					}
				}
				else if (op == Operator.LESS_EQUAL)
				{
					if (Double.parseDouble(values[i].toString()) <= Double.parseDouble(value))
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
	
	private static void checkQueryRules(QueryRule... rules) throws Exception
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
		}
	}

	private static void checkQueryRulesOld(DataMatrixInstance dm, boolean appliedOnColumns, QueryRule... rules) throws Exception
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

	public static DataMatrixInstance getSubMatrix2DFilterByRow(
			DataMatrixInstance matrix, QueryRule... rules) throws Exception
	{
		checkQueryRules(rules);
		
		if(rules.length != 1)
		{
			throw new Exception("at the moment supports only 1 QueryRule at a time");
		}
		
		// we want new row ('target') names, get colnames here to reuse selectUsingDecimal/selectUsingText
		List<String> colNames = matrix.getColNames();
		
		int amount = Integer.parseInt(rules[0].getField());
		Operator op = rules[0].getOperator();
		Object value = rules[0].getValue();
		
		List<Integer> rowResult = new ArrayList<Integer>();
		if (matrix.getData().getValueType().equals("Decimal"))
		{
			double valueDbl = Double.parseDouble(rules[0].getValue().toString());
			for(int row = 0; row < matrix.getNumberOfRows(); row++)
			{
				List<String> result = selectUsingDecimal(matrix.getRow(row), valueDbl, op, colNames);
				if(result.size() >= amount)
				{
					rowResult.add(row);
				}
			}
		}
		else
		{
			String valueStr = value.toString();
			for(int row = 0; row < matrix.getNumberOfRows(); row++)
			{
				List<String> result = selectUsingText(matrix.getRow(row), valueStr, op, colNames);
				if(result.size() >= amount)
				{
					rowResult.add(row);
				}
			}
		}
		
		if (rowResult.size() == 0)
		{
			throw new Exception("No rows in resultset, empty matrix!");
		}
		
		int[] rowIndices = new int[rowResult.size()];
		int[] colIndices = new int[matrix.getNumberOfCols()];
		
		for(int col = 0; col < matrix.getNumberOfCols(); col++)
		{
			colIndices[col] = col;
		}
		
		int rowIndex = 0;
		for(int row = 0; row < rowResult.size(); row++)
		{
			rowIndices[rowIndex] = rowResult.get(row);
			rowIndex++;
		}
		
		return matrix.getSubMatrix(rowIndices, colIndices);
	}

	public static DataMatrixInstance getSubMatrix2DFilterByCol(
			DataMatrixInstance matrix, QueryRule... rules) throws Exception
	{
		checkQueryRules(rules);
		
		if(rules.length != 1)
		{
			throw new Exception("at the moment supports only 1 QueryRule at a time");
		}
		
		// we want new col ('feature') names, get rownames here to reuse selectUsingDecimal/selectUsingText
		List<String> rowNames = matrix.getRowNames();
		
		int amount = Integer.parseInt(rules[0].getField());
		Operator op = rules[0].getOperator();
		Object value = rules[0].getValue();
		
		List<Integer> colResult = new ArrayList<Integer>();
		if (matrix.getData().getValueType().equals("Decimal"))
		{
			double valueDbl = Double.parseDouble(rules[0].getValue().toString());
			for(int col = 0; col < matrix.getNumberOfCols(); col++)
			{
				List<String> result = selectUsingDecimal(matrix.getCol(col), valueDbl, op, rowNames);
				if(result.size() >= amount)
				{
					colResult.add(col);
				}
			}
		}
		else
		{
			String valueStr = value.toString();
			for(int col = 0; col < matrix.getNumberOfCols(); col++)
			{
				List<String> result = selectUsingText(matrix.getCol(col), valueStr, op, rowNames);
				if(result.size() >= amount)
				{
					colResult.add(col);
				}
			}
		}
		
		if (colResult.size() == 0)
		{
			throw new Exception("No cols in resultset, empty matrix!");
		}
		
		int[] rowIndices = new int[matrix.getNumberOfRows()];
		int[] colIndices = new int[colResult.size()];
		
		for(int row = 0; row < matrix.getNumberOfRows(); row++)
		{
			rowIndices[row] = row;
		}
		
		int colIndex = 0;
		for(int col = 0; col < colResult.size(); col++)
		{
			colIndices[colIndex] = colResult.get(col);
			colIndex++;
		}
		
		return matrix.getSubMatrix(rowIndices, colIndices);
	}

}
