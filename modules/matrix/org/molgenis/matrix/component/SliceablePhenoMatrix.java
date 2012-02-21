package org.molgenis.matrix.component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.matrix.component.interfaces.BasicMatrix;
import org.molgenis.matrix.component.interfaces.DatabaseMatrix;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;

/**
 * Sliceable version of the PhenoMatrix. This assumes the rows are
 * ObservationTarget, the columns ObservableFeature and there can be zero or
 * more ObservedValue for each combination (hence return List &lt; ObservedValue
 * &gt; for each value 'V')
 * 
 * Slicing will be done by setting filters.
 * 
 * The data is retrieved by (a) retrieving visible columns and rows and (2)
 * retrieval of the matching data using columns and rows as filters. The whole
 * set is filtered by investigation.
 * 
 */
public class SliceablePhenoMatrix<R extends ObservationElement, C extends ObservationElement> extends
		AbstractObservationElementMatrix<R, C, ObservedValue> implements SliceableMatrix<R, C, ObservedValue>,
		DatabaseMatrix
{

	Database db;

	public void setDatabase(Database db)
	{
		this.db = db;
	}

	public SliceablePhenoMatrix(Class<R> rowClass, Class<C> colClass)
	{
		// this.database = database;
		this.rowClass = rowClass;
		this.colClass = colClass;
		this.valueClass = ObservedValue.class;
	}

	public SliceablePhenoMatrix(Class<R> rowClass, Class<C> colClass, Class<ObservedValue> valueClass)
	{
		// this.database = database;
		this.rowClass = rowClass;
		this.colClass = colClass;
		this.valueClass = valueClass;
	}

	@Override
	public List<R> getRowHeaders() throws MatrixException
	{
		// reload the rowheaders if filters have changed.
		if (rowDirty)
		{
			try
			{
				Query<R> query = this.createSelectQuery(getRowClass());
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
	public List<C> getColHeaders() throws MatrixException
	{
		// reload the rowheaders if filters have changed.
		if (colDirty)
		{
			// if no queries then we return empty list
			boolean hasRules = false;
			for (MatrixQueryRule r : this.getRules())
			{
				if (MatrixQueryRule.Type.colHeader.equals(r.getFilterType()))
				{
					hasRules = true;
					break;
				}
			}
			if (hasRules)
			{
				Query<C> query = this.createSelectQuery(getColClass());
				try
				{
					this.colHeaders = query.find();
				}
				catch (DatabaseException e)
				{
					this.colHeaders = new ArrayList<C>();
				}
			}
			else
			{
				this.colHeaders = new ArrayList<C>();
			}
			colDirty = false;
		}
		return colHeaders;
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
	public BasicMatrix<R, C, ObservedValue> getResult() throws Exception
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Helper method to create a 'count' query. Difference with a normal query
	 * is that there is no limit/offset on it
	 */
	private <D extends ObservationElement> Query<D> createCountQuery(Class<D> xClass) throws MatrixException
	{
		return this.createQuery(xClass, true);
	}

	/** Helper method to produce a selection query for columns or rows */
	private <D extends ObservationElement> Query<D> createSelectQuery(Class<D> xClass) throws MatrixException
	{
		return this.createQuery(xClass, false);
	}

	/**
	 * 
	 * @param field
	 *            , either ObservedValue.FEATURE or ObservedValue.TARGET
	 * @throws MatrixException
	 */
	private <D extends ObservationElement> Query<D> createQuery(Class<D> xClass, boolean countAll)
			throws MatrixException
	{
		// If xClass == getRowClass():
		// A. filter on rowIndex + rowHeaderProperty
		// B. filter on colValue: 1 subquery per column
		// C. filter on rowOffset and rowLimit
		try
		{
			// parameterize the refresh of the dim, either TARGET or FEATURE
			MatrixQueryRule.Type xIndexFilterType = MatrixQueryRule.Type.rowIndex;
			MatrixQueryRule.Type xHeaderFilterType = MatrixQueryRule.Type.rowHeader;
			//MatrixQueryRule.Type xValuesFilterType = MatrixQueryRule.Type.colValues;
			MatrixQueryRule.Type xValuePropertyFilterType = MatrixQueryRule.Type.colValueProperty;
			if (xClass.equals(getColClass()))
			{
				xIndexFilterType = MatrixQueryRule.Type.colIndex;
				xHeaderFilterType = MatrixQueryRule.Type.colHeader;
				//xValuesFilterType = MatrixQueryRule.Type.rowValues;
				xValuePropertyFilterType = MatrixQueryRule.Type.rowValueProperty;
			}

			// Impl

			// Impl A: header query
			Query<D> xQuery = db.query(xClass);
			for (MatrixQueryRule rule : rules)
			{
				// rewrite rule(type=row/colIndex) to rule(type=row/colHeader,
				// field=id)
				if (rule.getFilterType().equals(xIndexFilterType))
				{
					rule.setField("id");
					rule.setFilterType(xHeaderFilterType);
				}
				// add row/colHeader filters to query / remember sort rules
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
						@SuppressWarnings("unchecked")
						Query<ObservedValue> subQuery = (Query<ObservedValue>) db.query(this.getValueClass());
						// filter on data
						// if(data != null)
						// subQuery.eq(TextDataElement.DATA, data.getIdValue());
						// filter on the column/row
						subQuery.eq(
								xValuePropertyFilterType == MatrixQueryRule.Type.colValueProperty ? ObservedValue.FEATURE
										: ObservedValue.TARGET, rule.getDimIndex());
						subQueries.put(rule.getDimIndex(), subQuery);
					}
					subQueries.get(rule.getDimIndex()).addRules(rule);
				}
				// ignore all other rules
			}

			// add each subquery as condition on
			// ObservedValue.FEATURE/ObservedValue.TARGET
			for (Query<ObservedValue> q : subQueries.values())
			{
				String sql = q.createFindSql();
				// strip 'select ... from' and replace with 'select id from'
				sql = "SELECT ObservedValue."
						+ (xClass.equals(rowClass) ? ObservedValue.TARGET : ObservedValue.FEATURE) + " "
						+ sql.substring(sql.indexOf("FROM"));
				// use QueryRule.Operator.IN_SUBQUERY
				xQuery.subquery(ObservationElement.ID, sql);
				System.out.println("SQL: " + sql);
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
			
			// sort on name
			xQuery.sortASC(ObservationElement.NAME);

			// check for empty column filters
			if (xClass.equals(getColClass()))
			{
				System.out.println("header filter: " + xQuery);
			}

			return xQuery;
		}
		catch (Exception e)
		{
			throw new MatrixException(e);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<? extends ObservedValue>[][] getValueLists() throws MatrixException
	{
		try
		{
			// get the indices (map to real coordinates)
			final List<Integer> rowIndexes = getRowIndices();
			final List<Integer> colIndexes = getColIndices();

			if (rowIndexes.size() == 0 || colIndexes.size() == 0)
			{
				return create(rowIndexes.size(), colIndexes.size());
			}

			// create matrix of suitable size
			final List<ObservedValue>[][] valueMatrix = create(rowIndexes.size(), colIndexes.size());

			// retrieve values matching the selected indexes
			@SuppressWarnings("unchecked")
			Query<ObservedValue> query = (Query<ObservedValue>) db.query(valueClass);
			query.in(ObservedValue.FEATURE, this.getColIndices());
			query.in(ObservedValue.TARGET, this.getRowIndices());

			// use the streaming interface?
			List<ObservedValue> values = query.find();

			for (ObservedValue value : values)
			{
				if (valueMatrix[rowIndexes.indexOf(value.getTarget_Id())][colIndexes.indexOf(value.getFeature_Id())] == null)
				{
					valueMatrix[rowIndexes.indexOf(value.getTarget_Id())][colIndexes.indexOf(value.getFeature_Id())] = new ArrayList<ObservedValue>();
				}
				valueMatrix[rowIndexes.indexOf(value.getTarget_Id())][colIndexes.indexOf(value.getFeature_Id())].add(value);
			}

			return valueMatrix;
		}
		catch (Exception e)
		{
			throw new MatrixException(e);
		}
	}

	public List<ObservedValue>[][] create(int rows, int cols)
	{
		// create all empty rows as well
		@SuppressWarnings("unchecked")
		List<ObservedValue>[][] data = new ArrayList[rows][cols];
		for (int i = 0; i < data.length; i++)
		{
			for (int j = 0; j < cols; j++)
				data[i][j] = new ArrayList<ObservedValue>();
		}

		return data;
	}

	@Override
	public ObservedValue[][] getValues() throws MatrixException
	{
		throw new UnsupportedOperationException("use getValueLists");
	}
	
	@Override
	public Class<R> getRowClass()
	{
		return rowClass;
	}

	@Override
	public void setRowClass(Class<R> rowClass)
	{
		this.rowClass = rowClass;
	}

	@Override
	public Class<C> getColClass()
	{
		return colClass;
	}

	@Override
	public void setColClass(Class<C> colClass)
	{
		this.colClass = colClass;
	}
}
