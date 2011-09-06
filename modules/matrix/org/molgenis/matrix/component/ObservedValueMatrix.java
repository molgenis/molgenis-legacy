package org.molgenis.matrix.component;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.matrix.component.interfaces.BasicMatrix;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;

/**
 * This matrix implementation allows to visualize ObservedValue filtered by
 * rowtype and coltype and any additional filters you choose to use. Optionally,
 * you can also filter on 'relation'.
 * 
 * Strategy is that the row/col list will be reduced by slicing. Then it is easy
 * to retrieve a subset of the data afterwards.
 * 
 * Issues:
 * <ul>
 * <li>
 * The type can be used for feature, target or relation. So we must specify what
 * goes where in R and C and optionally, if the third parameter should be used
 * to filter.</li>
 * <li>Often, one may want to filter on protocolApplication also.</li>
 * <li>inside the database we must be able to apply a cast of the value! Because
 * it is all string values now which screws up '>' and sorting.
 * </ul>
 * 
 */
public class ObservedValueMatrix<R extends ObservationElement, C extends ObservationElement>
		implements SliceableMatrix<R, C, List<ObservedValue>>
{
	public enum Mapping
	{
		FEATURE("feature"), TARGET("target"), RELATION("relation");

		private String tag;

		private Mapping(String name)
		{
			tag = name;
		}

		public String toString()
		{
			return tag;
		}
	};

	// row and column types
	private Class<R> rowClass;
	private Class<C> colClass;

	// predefined list of rows and cols
	private List<R> originalRows;
	private List<C> originalCols;

	// what value of observedValue goes where
	private Mapping colField = Mapping.FEATURE;
	private Mapping rowField = Mapping.TARGET;

	// limit of current view
	int rowLimit = 10;
	int rowOffset = 0;

	int colLimit = 10;
	int colOffset = 0;

	// keep track of query filter rules
	List<MatrixQueryRule> rules = new ArrayList<MatrixQueryRule>();

	// database we work with
	Database database;

	/**
	 * Construct unlimited matrix based on types only. This may be very
	 * expensive!
	 * 
	 * @param rowClass
	 * @param colClass
	 */
	public ObservedValueMatrix(Database database, Class<R> rowClass,
			Class<C> colClass)
	{
		this.database = database;
		this.rowClass = rowClass;
		this.colClass = colClass;
	}

	/**
	 * Construct matrix based on predefined list of rows and columns
	 * 
	 * @param rows
	 * @param colClass
	 */
	public ObservedValueMatrix(List<R> rows, List<C> cols)
	{
		throw new UnsupportedOperationException("not yet implemented");
		// this.originalRows = rows;
		// this.originalCols = cols;
	}

	@Override
	public SliceableMatrix<R, C, List<ObservedValue>> sliceByRowValues(
			int index, Operator operator, Object value) throws Exception
	{
		// TODO
		return null;
	}

	@Override
	public SliceableMatrix<R, C, List<ObservedValue>> sliceByColValues(
			int index, Operator operator, Object value) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<R, C, List<ObservedValue>> sliceByRowValues(R row,
			Operator operator, Object value) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<R, C, List<ObservedValue>> sliceByColValues(C col,
			Operator operator, Object value) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public SliceableMatrix<R, C, List<ObservedValue>> sliceByRowProperty(
			String property, Operator operator, Object value)
	{
		//only add the rule
		this.rules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader,
				property, operator, value));
		return this;
	}

	@Override
	public SliceableMatrix<R, C, List<ObservedValue>> sliceByColProperty(
			String property, Operator operator, Object value) throws Exception
	{
		this.rules.add(new MatrixQueryRule(MatrixQueryRule.Type.colHeader,
				property, operator, value));
		return this;
	}

	@Override
	public BasicMatrix<R, C, List<ObservedValue>> getResult() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<ObservedValue>[][] getValues() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<R> getRowHeaders() throws Exception
	{
		// here we join RowHeaderClass X ObservedValue to get visible results

		// first we create a query of headers as this is smallest set
		// therefore:
		// - create a subquery that satisfies rowHeader filters
		// - except limit offset (which are not in filter list)
		Query<R> rowQuery = database.query(rowClass);
		for (MatrixQueryRule rule : rules)
		{
			if (rule.getFilterType().equals(MatrixQueryRule.Type.rowHeader))
			{
				rowQuery.addRules(rule);
			}
		}
		String rowSql = rowQuery.createFindSql();

		// second filter remaining row headers based on colValue filters
		Query<ObservedValue> valueQuery = database.query(ObservedValue.class);
		for (MatrixQueryRule rule : rules)
		{
			if (rule.getFilterType().equals(MatrixQueryRule.Type.colValues))
			{
				rowQuery.addRules(rule);
			}
		}
		String valueSql = valueQuery.createFindSql();

		// create a join of the rowQuery and valueQuery
		// such that we can construct instances of
		// while applying limit and offset

		String sql = "SELECT (" + rowSql + ") AS " + rowClass.getSimpleName()
				+ " INNER JOIN (" + valueSql + ") AS ObserverdValue" + "WHERE "
				+ rowClass.getSimpleName() + ".ID = ObservedValue." + rowField
				+ " LIMIT " + rowLimit + " OFFSET " + rowOffset;

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<C> getColHeaders() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getRowIndices() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getColIndices() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public SliceableMatrix<R, C, List<ObservedValue>> sliceByRowIndex(
			Operator operator, int index) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<R, C, List<ObservedValue>> sliceByRowOffsetLimit(
			int limit, int offset) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<R, C, List<ObservedValue>> sliceByColOffsetLimit(
			int limit, int offset) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Deprecated
	public SliceableMatrix<R, C, List<ObservedValue>> sliceByPaging(
			MatrixQueryRule rule) throws Exception
	{
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void createFresh() throws Exception
	{
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public SliceableMatrix<R, C, List<ObservedValue>> sliceByIndex(
			MatrixQueryRule rule) throws Exception
	{
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public List<C> getVisibleCols() throws Exception
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	@Deprecated
	public SliceableMatrix<R, C, List<ObservedValue>> sliceByColIndex(
			Operator operator, int index) throws Exception
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	@Deprecated
	public SliceableMatrix<R, C, List<ObservedValue>> sliceByColValues(
			MatrixQueryRule rule) throws Exception
	{
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public SliceableMatrix<R, C, List<ObservedValue>> sliceByRowValues(
			QueryRule rule) throws Exception
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	@Deprecated
	public SliceableMatrix<R, C, List<ObservedValue>> sliceByRowHeader(
			MatrixQueryRule rule) throws Exception
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	@Deprecated
	public SliceableMatrix<R, C, List<ObservedValue>> sliceByColHeader(
			MatrixQueryRule rule) throws Exception
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	@Deprecated
	public List<ObservedValue>[][] getVisibleValues() throws Exception
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	@Deprecated
	public List<R> getVisibleRows() throws Exception
	{
		throw new UnsupportedOperationException();
	}

}
