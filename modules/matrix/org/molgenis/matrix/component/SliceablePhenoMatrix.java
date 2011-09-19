package org.molgenis.matrix.component;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.data.TextDataElement;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.general.MatrixColHeaderFilter;
import org.molgenis.matrix.component.general.MatrixColValueFilter;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.matrix.component.general.MatrixRowHeaderFilter;
import org.molgenis.matrix.component.general.MatrixRowValueFilter;
import org.molgenis.matrix.component.interfaces.BasicMatrix;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;

/**
 * Sliceable version of the ObservationMatrix
 * 
 * 
 */
public class SliceablePhenoMatrix implements SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>>
{
	// required
	private Investigation investigation;
	private Database database;
	private Class<ObservationTarget> rowClass;
	private Class<ObservableFeature> colClass;
	private Class<ObservedValue> valueClass;

	// caches, may result in performance issues
	private List<ObservableFeature> colHeaders = null;
	private List<ObservationTarget> rowHeaders = null;

	// collection of all rules except limit/offset
	List<MatrixQueryRule> rules = new ArrayList<MatrixQueryRule>();

	// indicator if rowHeader, rowIndices or colHeader, colIndices are dirty
	private boolean rowDirty = true;
	private boolean colDirty = true;

	private int rowLimit = 10;
	private int rowOffset = 0;
	private int colLimit = 10;
	private int colOffset = 0;

	/**
	 * Construct sliceable matrix for one Data set.
	 * 
	 * @param database
	 * @param data
	 */
	public SliceablePhenoMatrix(Database database)
	{
		this.database = database;
		this.rowClass = ObservationTarget.class;
		this.colClass = ObservableFeature.class;
		this.valueClass = ObservedValue.class;
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> slice(MatrixQueryRule rule)
			throws MatrixException
	{
		this.validate(rule);
		switch (rule.getFilterType())
		{
		// row headers need to be refreshed in case of:
			case rowIndex:
				this.rowDirty = true;
				break;
			case rowHeader:
				this.rowDirty = true;
				break;
			case colValues:
				this.rowDirty = true;
				break;
			case colValueProperty:
				this.rowDirty = true;
				break;
			// col headers need to be refreshed in case of:
			case colIndex:
				this.colDirty = true;
				break;
			case colHeader:
				this.colDirty = true;
				break;
			case rowValues:
				this.colDirty = true;
				break;
			case rowValueProperty:
				this.rowDirty = true;
				break;
		}
		rules.add(rule);
		return this;
	}

	@Override
	public List<ObservationTarget> getRowHeaders() throws MatrixException
	{
		// reload the rowheaders if filters have changed.
		if (rowDirty)
		{
			try
			{
				Query<ObservationTarget> query = this.createSelectQuery(getRowClass());
				this.rowHeaders = query.find();
				rowDirty = false;
			}
			catch (Exception e)
			{
				throw new MatrixException(e);
			}
		}
		return rowHeaders;
	}

	public Integer getRowCount() throws MatrixException
	{
		// fire a count query on headers
		try
		{
			return this.createCountQuery(getRowClass()).count();
		}
		catch (DatabaseException e)
		{
			throw new MatrixException(e);
		}
	}

	@Override
	public List<Integer> getRowIndices() throws MatrixException
	{
		// retrieve the indices from the headers (we use the id value).
		List<Integer> rowIndices = new ArrayList<Integer>();
		for (ObservationTarget row : getRowHeaders())
		{
			rowIndices.add(row.getId());
		}
		return rowIndices;
	}

	@Override
	public List<ObservableFeature> getColHeaders() throws MatrixException
	{
		// reload the rowheaders if filters have changed.
		if (colDirty)
		{
			try
			{
				Query<ObservableFeature> query = this.createSelectQuery(getColClass());
				this.colHeaders = query.find();
				colDirty = false;
			}
			catch (Exception e)
			{
				throw new MatrixException(e);
			}
		}
		return colHeaders;
	}

	@Override
	public List<Integer> getColIndices() throws MatrixException
	{
		// get col indexes from col headers
		List<Integer> colIndices = new ArrayList<Integer>();
		for (ObservableFeature col : getColHeaders())
		{
			colIndices.add(col.getId());
		}
		return colIndices;
	}

	public Integer getColCount() throws MatrixException
	{
		// fire count query on col headers
		try
		{
			return this.createCountQuery(getColClass()).count();
		}
		catch (DatabaseException e)
		{
			throw new MatrixException(e);
		}
	}

	@Override
	public BasicMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> getResult() throws Exception
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void reset() throws Exception
	{
		this.rules = new ArrayList<MatrixQueryRule>();
		// empty the caches
		colDirty = true;
		colOffset = 0;
		rowDirty = true;
		rowOffset = 0;

	}

	@Override
	public List<String> getRowPropertyNames()
	{
		try
		{
			return this.getRowClass().newInstance().getFields();
		}
		catch (Exception e)
		{
			// should never happen
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<String> getColPropertyNames()
	{
		try
		{
			return this.getColClass().newInstance().getFields();
		}
		catch (Exception e)
		{
			// should never happen
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<String> getValuePropertyNames()
	{
		try
		{
			return this.getValueClass().newInstance().getFields();
		}
		catch (Exception e)
		{
			// should never happen
			throw new RuntimeException(e);
		}
	}

	private <D extends ObservationElement> Query<D> createCountQuery(
			Class<D> xClass) throws MatrixException
	{
		return this.createQuery(xClass, true);
	}

	private <D extends ObservationElement> Query<D> createSelectQuery(
			Class<D> xClass) throws MatrixException
	{
		return this.createQuery(xClass, false);
	}

	/**
	 * 
	 * @param field
	 *            , either ObservedValue.FEATURE or ObservedValue.TARGET
	 * @throws MatrixException
	 */
	private <D extends ObservationElement> Query<D> createQuery(
			Class<D> xClass, boolean countAll) throws MatrixException
	{
		// If xClass == getRowClass():
		// A. filter on rowIndex + rowHeaderProperty
		// B. filter on colValue: 1 subquery per column
		// C. filter on rowOffset and rowLimit

		try
		{
			// parameterize the refresh of the dim, either TARGET or FEATURE
			String xDim = TextDataElement.TARGET;
			MatrixQueryRule.Type xIndexFilterType = MatrixQueryRule.Type.rowIndex;
			MatrixQueryRule.Type xHeaderFilterType = MatrixQueryRule.Type.rowHeader;
			MatrixQueryRule.Type xValuesFilterType = MatrixQueryRule.Type.colValues;
			MatrixQueryRule.Type xValuePropertyFilterType = MatrixQueryRule.Type.colValueProperty;
			if (xClass.equals(getColClass()))
			{
				xDim = TextDataElement.FEATURE;
				xIndexFilterType = MatrixQueryRule.Type.colIndex;
				xHeaderFilterType = MatrixQueryRule.Type.colHeader;
				xValuesFilterType = MatrixQueryRule.Type.rowValues;
				xValuePropertyFilterType = MatrixQueryRule.Type.rowValueProperty;
			}

			// Impl

			// Impl A: header query
			Query<D> xQuery = database.query(xClass);
			for (MatrixQueryRule rule : rules)
			{
				// rewrite rule(type=rowIndex) to rule(type=rowHeader, field=id)
				if (rule.getFilterType().equals(xIndexFilterType))
				{
					rule.setField(ObservedValue.ID);
					rule.setFilterType(xHeaderFilterType);
				}
				// add rowHeader filters to query / remember sort rules
				if (rule.getFilterType().equals(xHeaderFilterType))
				{
					xQuery.addRules(rule);
				}
				// ignore all other rules
			}

			// select * from Individual where id in (select target from
			// observedvalue where feature = 1 AND value > 10 AND target in
			// (select target from observedvalue ));

			// Impl B: create subquery per column, order matters because of
			// sorting (not supported).
			Map<Integer, Query<ObservedValue>> subQueries = new LinkedHashMap<Integer, Query<ObservedValue>>();
			for (MatrixQueryRule rule : rules)
			{
				// only add colValues / rowValues as subquery
				if (rule.getFilterType().equals(xValuePropertyFilterType))
				{
					// create a new subquery for each colValues column
					if (subQueries.get(rule.getDimIndex()) == null)
					{
						Query<ObservedValue> subQuery = database.query(this.getValueClass());
						//filter on data
						//if(data != null)
						//	subQuery.eq(TextDataElement.DATA, data.getIdValue());
						//filter on the column/row
						subQuery.eq(xDim, rule.getDimIndex());
						subQueries.put(rule.getDimIndex(), subQuery);
					}
					subQueries.get(rule.getDimIndex()).addRules(rule);
				}
				// ignore all other rules
			}
			
			//if no queries where made we still need one for the right 'data'
//			if(data != null && subQueries.size() == 0)
//			{
//				Query<V> subQuery = database.query(this.getValueClass());
//				//filter on data and first column
//				subQuery.eq(TextDataElement.DATA, data.getIdValue());
//				subQuery.eq(xDim, 0);
//				subQuery.sortASC(xDim+"Index");
//				subQueries.put(0, subQuery);
//			}
			// add each subquery as condition on ID
			for (Query<ObservedValue> q : subQueries.values())
			{
				String sql = q.createFindSql();
				// strip 'select ... from' and replace with 'select id from'
				sql = "SELECT TextDataElement."+xDim+" "
						+ sql.substring(sql.indexOf("FROM"));
				// use QueryRule.Operator.IN_SUBQUERY
				xQuery.subquery(ObservationElement.ID, sql);
			}

			// add limit and offset, unless count
			if (!countAll)
			{
				if (xClass.equals(getColClass()))
				{
					xQuery.limit(colLimit);
					xQuery.offset(colOffset);
				}
				else
				{
					xQuery.limit(rowLimit);
					xQuery.offset(rowOffset);
				}
			}

			return xQuery;
		}
		catch (Exception e)
		{
			throw new MatrixException(e);
		}
	}

	private void validate(MatrixQueryRule rule) throws MatrixException
	{
		try
		{
			switch (rule.getFilterType())
			{
			// rowheader and colheader can do all operators
				case rowHeader:
					if (!this.getRowPropertyNames().contains(rule.getField()))
					{
						throw new MatrixException(
								"rule.field not in matrix.rowPropertyNames: "
										+ rule);
					}
					break;
				case colHeader:
					if (!this.getColPropertyNames().contains(rule.getField()))
					{
						throw new MatrixException(
								"rule.field not in matrix.rowPropertyNames: "
										+ rule);
					}
					break;
				case rowValueProperty:
					break;
				case colValueProperty:
					break;					
				default:
					throw new MatrixException("rule not supported: " + rule);
			}
		}
		catch (Exception e)
		{
			throw new MatrixException("rule not supported: " + rule);
		}
	}

	@Override
	public int getRowLimit()
	{
		return rowLimit;
	}

	@Override
	public void setRowLimit(int rowLimit)
	{
		this.rowDirty = true;
		this.rowLimit = rowLimit;
	}

	@Override
	public int getRowOffset()
	{
		return rowOffset;
	}

	@Override
	public void setRowOffset(int rowOffset)
	{
		this.rowDirty = true;
		this.rowOffset = rowOffset;
	}

	@Override
	public int getColLimit()
	{
		return colLimit;
	}

	@Override
	public void setColLimit(int colLimit)
	{
		this.colDirty = true;
		this.colLimit = colLimit;
	}

	@Override
	public int getColOffset()
	{
		return colOffset;
	}

	@Override
	public void setColOffset(int colOffset)
	{
		this.colDirty = true;
		this.colOffset = colOffset;
	}

	protected Class<ObservationTarget> getRowClass()
	{
		return rowClass;
	}

	protected void setRowClass(Class<ObservationTarget> rowClass)
	{
		this.rowClass = rowClass;
	}

	protected Class<ObservableFeature> getColClass()
	{
		return colClass;
	}

	protected void setColClass(Class<ObservableFeature> colClass)
	{
		this.colClass = colClass;
	}

	protected Class<ObservedValue> getValueClass()
	{
		return valueClass;
	}

	protected void setValueClass(Class<ObservedValue> valueClass)
	{
		this.valueClass = valueClass;
	}

	@Override
	public List<ObservedValue>[][] getValues() throws Exception
	{
		// get the indices (map to real coordinates)
		final List<Integer> rowIndexes = getRowIndices();
		final List<Integer> colIndexes = getColIndices();

		// create matrix of suitable size
		final List<ObservedValue>[][] valueMatrix = create(getRowLimit(), getColLimit(),
				valueClass);

		// retrieve values matching the selected indexes
		Query<ObservedValue> query = database.query(valueClass);
		query.in(ObservedValue.FEATURE, this.getColIndices());
		query.in(ObservedValue.TARGET, this.getRowIndices());

		// use the streaming interface?
		List<ObservedValue> values = query.find();

		for (ObservedValue value : values)
		{
			if (valueMatrix[rowIndexes.indexOf(value.getTarget())][colIndexes.indexOf(value.getFeature())] == null) {
				valueMatrix[rowIndexes.indexOf(value.getTarget())][colIndexes.indexOf(value.getFeature())] = new ArrayList<ObservedValue>();
			}
			valueMatrix[rowIndexes.indexOf(value.getTarget())][colIndexes.indexOf(value.getFeature())].add(value);
		}

		return valueMatrix;
	}

	@Override
	public void refresh() throws Exception
	{
		this.reset();

	}

	@SuppressWarnings("unchecked")
	protected List<ObservedValue>[] create(int rows)
	{
		return (List<ObservedValue>[]) new Object[rows];
	}

	@SuppressWarnings("unchecked")
	public List<ObservedValue>[][] create(int rows, int cols, Class valueType)
	{
		// create all empty rows as well
		List<ObservedValue>[][] data = (List<ObservedValue>[][]) Array.newInstance(valueType, rows, cols);
		for (int i = 0; i < data.length; i++)
		{
			data[i] = (List<ObservedValue>[]) Array.newInstance(valueType, cols);
		}

		return data;
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByColIndex(Operator operator,
			Integer index) throws Exception
	{
		// rewrite as sliceByColProperty(id)
		return this.sliceByColProperty(ObservedValue.ID, operator, index);
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByRowIndex(Operator operator,
			Integer index) throws Exception
	{
		// this is actually a rowProperty slice!
		return this.sliceByRowProperty(ObservedValue.ID, operator, index);
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByRowOffsetLimit(int limit, int offset)
			throws Exception
	{
		this.rowLimit = limit;
		this.rowOffset = offset;
		return this;
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByColOffsetLimit(int limit, int offset)
			throws Exception
	{
		this.colLimit = limit;
		this.colOffset = offset;
		return this;
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByRowValues(int rowIndex,
			Operator operator, Object value) throws Exception
	{
		// slice by rowIndex means effectively ObservedValue.target=index &&
		// ObervedValue.value=value!
		return this.slice(new MatrixRowValueFilter(rowIndex,
				TextDataElement.VALUE, operator, value));
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByRowValues(ObservationTarget row, Operator operator,
			Object value) throws Exception
	{
		// slice by rowIndex means effectively ObservedValue.target=row.getId()
		// && ObervedValue.value=value!
		if (row.getId() == null) throw new MatrixException(
				"row.getId() not set for sliceByRowValues(" + row + ")");
		return this.slice(new MatrixRowValueFilter(row.getId(),
				TextDataElement.VALUE, operator, value));
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByColValues(int colIndex,
			Operator operator, Object value) throws Exception
	{
		// slice by rowIndex means effectively ObservedValue.feature=index &&
		// ObervedValue.value=value!
		return this.slice(new MatrixColValueFilter(colIndex,
				TextDataElement.VALUE, operator, value));
	}

	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sortCol(int colIndex, Operator operator)
			throws MatrixException
	{
		//
		// sort by value
		return this.slice(new MatrixColValueFilter(colIndex,
				TextDataElement.VALUE, operator));
	}

	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sortCol(Integer colIndex,
			String colProperty, Operator operator) throws MatrixException
	{
		return this.slice(new MatrixColValueFilter(colIndex, colProperty,
				operator));
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByColValues(ObservableFeature col, Operator operator,
			Object value) throws Exception
	{
		// slice by rowIndex means effectively ObservedValue.target=row.getId()
		// && ObervedValue.value=value!
		if (col.getId() == null) throw new MatrixException(
				"col.getId() not set for sliceByColValues(" + col + ")");
		return this.slice(new MatrixColValueFilter(col.getId(),
				TextDataElement.VALUE, operator, value));
	}

	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sortByColValues(ObservableFeature col, Operator operator)
			throws MatrixException
	{
		if (col.getId() == null) throw new MatrixException(
				"col.getId() not set for sortByColValues(" + col + ")");
		return this.slice(new MatrixColValueFilter(col.getId(),
				TextDataElement.VALUE, operator, col.getId()));
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByColValueProperty(ObservableFeature col, String property,
			Operator operator, Object value) throws MatrixException
	{
		if (col.getId() == null) throw new MatrixException(
				"col.getId() not set for sortByColValues(" + col + ")");
		return this.slice(new MatrixColValueFilter(col.getId(),
				TextDataElement.VALUE, operator, value));
	}
	
	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByColValueProperty(int colIndex, String property,
			Operator operator, Object value) throws MatrixException
	{
		return this.slice(new MatrixColValueFilter(colIndex,
				TextDataElement.VALUE, operator, value));
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByRowProperty(String property,
			Operator operator, Object value) throws MatrixException
	{
		return this.slice(new MatrixRowHeaderFilter(property, operator, value));
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByColProperty(String property,
			Operator operator, Object value) throws Exception
	{
		return this.slice(new MatrixColHeaderFilter(property, operator, value));
	}
}
