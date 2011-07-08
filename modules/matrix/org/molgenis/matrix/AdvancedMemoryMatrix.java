package org.molgenis.matrix;
//package org.molgenis.matrix;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import matrix.AbstractDataMatrixInstance;
//import matrix.AbstractDataMatrixQueries;
//
//import org.molgenis.core.Nameable;
//import org.molgenis.framework.db.Database;
//import org.molgenis.framework.db.Query;
//import org.molgenis.framework.db.QueryRule;
//import org.molgenis.framework.db.QueryRule.Operator;
//
//public class AdvancedMemoryMatrix<E> extends MemoryMatrix<E>
//{
//	public AdvancedMemoryMatrix(List<String> rowNames, List<String> colNames,
//			E[][] values) throws MatrixException{
//		super(rowNames, colNames, values);
//	}
//	
//	
//	public Matrix<E> getSubMatrixFilterByRowEntityValues(Database db, QueryRule... rules) throws Exception
//	{
//		List<String> colNames = this.getColNames();
//		// 1. query on row type entities
//		//TODO: solve:
//		Query q = db.query(db.getClassForName(this.getCols().get(0).getClass().toString()));
//		// 2. entities must be present in row names
//		q.addRules(new QueryRule("name", Operator.IN, this.getRowNames()));
//		// 3. now add other rules
//		q.addRules(rules);
//		List<Nameable> subRow = q.find();
//		List<String> rowNames = new ArrayList<String>();
//		for (Nameable i : subRow)
//		{
//			rowNames.add(i.getName());
//		}
//		Matrix<E> res = this.getSubMatrixByName(rowNames, colNames);
//		return res;
//	}
//
//	public AbstractDataMatrixInstance<Object> getSubMatrixFilterByColEntityValues(Database db, QueryRule... rules) throws Exception
//	{
//		//TODO: similar to getSubMatrixFilterByRowEntityValues()
//		return null;
//	}
//
//	public Matrix<E> getSubMatrixFilterByRowMatrixValues(QueryRule... rules) throws Exception
//	{
//		checkQueryRules(this, false, rules);
//
//		// colNames is the resultset we want to get
//		List<String> colNames = null;
//		List<String> rowNames = this.getRowNames();
//
//		// iterate over queryrules
//		for (QueryRule rule : rules)
//		{
//			List<String> result = null;
//			if (this.getValueType().equals("Decimal"))
//			{
//				double value = Double.parseDouble(rule.getValue().toString());
//				result = AbstractDataMatrixQueries.selectUsingDecimal(this.getRowByName(rule.getField()), value, rule.getOperator(), this.getColNames());
//			}
//			else
//			{
//				String value = rule.getValue().toString();
//				result = AbstractDataMatrixQueries.selectUsingText(this.getRowByName(rule.getField()), value, rule.getOperator(), this.getColNames());
//			}
//
//			if (colNames == null)
//			{
//				// first queryrule being applied, store results in colnames
//				colNames = result;
//			}
//			else
//			{
//				// consecutively: basically, applying an AND operator here
//				// by removing result from colnames.. so OR not supported
//				colNames.removeAll(result);
//			}
//
//			if (colNames.size() == 0)
//			{
//				throw new Exception("No colnames in resultset, empty matrix!");
//			}
//		}
//
//		Matrix<E> res = this.getSubMatrixByName(rowNames, colNames);
//
//		return res;
//	}
//
//	public AbstractDataMatrixInstance<Object> getSubMatrixFilterByColMatrixValues(QueryRule... rules) throws Exception
//	{
//		//TODO: similar to getSubMatrixFilterByRowMatrixValues()
//		return null;
//	}
//	
//	
//	private static void checkQueryRules(Matrix dm, boolean appliedOnColumns, QueryRule... rules) throws Exception
//	{
//		for (QueryRule rule : rules)
//		{
//			if (rule.getField() == null)
//			{
//				throw new Exception("QueryRule invalid: field is null");
//			}
//			if (rule.getValue() == null)
//			{
//				throw new Exception("QueryRule invalid: value is null");
//			}
//			if (rule.getOperator() == null)
//			{
//				throw new Exception("QueryRule invalid: operator is null");
//			}
//			if (appliedOnColumns && !dm.getColNames().contains(rule.getField()))
//			{
//				throw new Exception("QueryRule invalid: no column named '" + rule.getField() + "'");
//			}
//			if (!appliedOnColumns && !dm.getRowNames().contains(rule.getField()))
//			{
//				throw new Exception("QueryRule invalid: no row named '" + rule.getField() + "'");
//			}
//		}
//	}
//	
//	
//}
