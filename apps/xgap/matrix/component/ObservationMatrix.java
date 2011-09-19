package org.molgenis.matrix.component;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.data.Data;
import org.molgenis.data.DecimalDataElement;
import org.molgenis.data.TextDataElement;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.matrix.component.interfaces.BasicMatrix;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Observation;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;

/**
 * Very naive implementation of BasicMatrix. Assumes only one value per
 * feature/target pair. Only works with xgap.Data.
 */
public class ObservationMatrix<R extends ObservationElement, C extends ObservationElement, V extends Observation>
		implements BasicMatrix<R, C, V>
{
	// required
	private Investigation investigation;
	private Database database;
	private Class<R> rowClass;
	private Class<C> colClass;
	private Class<V> valueClass;

	// optional in case of XGAP
	// then valueClass must be TextDataElement or DecimalDataElement
	private Data data;

	// caches, may result in performance issues
	List<C> colHeaders = null;
	List<R> rowHeaders = null;
	private List<Integer> colIndices = null;
	private List<Integer> rowIndices = null;
	Integer colCount = null;
	Integer rowCount = null;

	@SuppressWarnings("unchecked")
	public ObservationMatrix(Database database, Data data)
	{
		this.database = database;
		this.data = data;
		this.rowClass = (Class<R>) database.getClassForName(data
				.getTargetType());
		this.colClass = (Class<C>) database.getClassForName(data
				.getFeatureType());
		this.valueClass = (Class<V>) (data.getValueType().equals("Decimal") ? DecimalDataElement.class
				: TextDataElement.class);
	}

	@Override
	public List<R> getRowHeaders() throws Exception
	{
		if (rowHeaders == null)
		{
			// use the rowindexes to retrieve the correct headers
			List<Integer> rowIndices = this.getRowIndices();
			List<R> temp = database.query(rowClass)
					.in(ObservationElement.ID, rowIndices).find();

			// sort according to indices
			rowHeaders = temp;
			for (R rowHeader : temp)
			{
				rowHeaders
						.set(rowIndices.indexOf(rowHeader.getId()), rowHeader);
			}
		}
		return rowHeaders;
	}

	@Override
	public List<C> getColHeaders() throws Exception
	{
		if (colHeaders == null)
		{
			// use the colIndexes to retrieve the headers
			List<Integer> colIndices = this.getColIndices();
			List<C> temp = database.query(colClass)
					.in(ObservationElement.ID, colIndices).find();

			// sort according to indices
			colHeaders = temp;
			for (C colHeader : temp)
			{
				colHeaders
						.set(colIndices.indexOf(colHeader.getId()), colHeader);
			}
		}
		return colHeaders;
	}

	@Override
	public List<Integer> getRowIndices() throws Exception
	{
		if (rowIndices == null)
		{
			// query one column to get the ordering right
			List<V> firstCol = database.query(valueClass)
					.eq(TextDataElement.FEATUREINDEX, 1)
					.sortASC(TextDataElement.TARGETINDEX).find();

			// get the target ids
			rowIndices = new ArrayList<Integer>();
			for (V row : firstCol)
			{
				rowIndices.add(row.getTarget());
			}
		}
		return rowIndices;
	}

	@Override
	public List<Integer> getColIndices() throws Exception
	{
		if (colIndices == null)
		{
			// query one row to get the ordering right
			List<V> firstRow = database.query(valueClass)
					.eq(TextDataElement.TARGETINDEX, 1)
					.sortASC(TextDataElement.FEATUREINDEX).find();

			// get the feature ids
			colIndices = new ArrayList<Integer>();
			for (V col : firstRow)
			{
				colIndices.add(col.getFeature());
			}
		}
		return colIndices;
	}

	@Override
	// default sorted by featureIndex and rowIndex
	public V[][] getValues() throws Exception
	{
		// TODO: do this in batches?

		// get the indices (map to real coordinates)
		final List<Integer> rowIndexes = getRowIndices();
		final List<Integer> colIndexes = getColIndices();

		// create matrix of suitable size
		final V[][] valueMatrix = create(getRowIndices().size(), getColIndices().size(),
				valueClass);

		// retrieve values matching the selected indexes
		Query<V> query = database.query(valueClass);
		query.in(ObservedValue.FEATURE, this.getColIndices());
		query.in(ObservedValue.TARGET, this.getRowIndices());

		// use the streaming interface?
		List<V> values = query.find();

		for (V value : values)
		{
			valueMatrix[rowIndexes.indexOf(value.getTarget())][colIndexes
					.indexOf(value.getFeature())] = value;
		}

		return valueMatrix;
	}

	@SuppressWarnings("unchecked")
	protected V[] create(int rows)
	{
		return (V[]) new Object[rows];
	}

	@SuppressWarnings("unchecked")
	public V[][] create(int rows, int cols, Class<V> valueType)
	{
		// create all empty rows as well
		V[][] data = (V[][]) Array.newInstance(valueType, rows, cols);
		for (int i = 0; i < data.length; i++)
		{
			data[i] = (V[]) Array.newInstance(valueType, cols);
		}

		return data;
	}

	@Override
	public Integer getColCount() throws Exception
	{
		if (colCount == null)
		{
			// in case of XGAP filter using 'data'
			if (data != null
					&& (valueClass.equals(TextDataElement.class) || valueClass
							.equals(DecimalDataElement.class)))
			{
				colCount = database.query(valueClass)
						.equals(TextDataElement.TARGETINDEX, 0)
						.equals(TextDataElement.DATA, data.getId()).count();
			}
			// else, filter on investigation
			else
			{
				colCount = database
						.query(colClass)
						.equals(ObservedValue.INVESTIGATION,
								investigation.getId()).count();
			}
		}
		return colCount;
	}

	@Override
	public Integer getRowCount() throws Exception
	{
		if (rowCount == null)
		{
			// in case of XGAP filter using 'data'
			if (data != null
					&& (valueClass.equals(TextDataElement.class) || valueClass
							.equals(DecimalDataElement.class)))
			{
				rowCount = database.query(valueClass)
						.equals(TextDataElement.FEATUREINDEX, 0)
						.equals(TextDataElement.DATA, data.getId()).count();
			}
			// else, filter on investigation
			else
			{
				rowCount = database
						.query(rowClass)
						.equals(ObservedValue.INVESTIGATION,
								investigation.getId()).count();
			}
		}
		return rowCount;
	}

	protected Class<R> getRowClass()
	{
		return rowClass;
	}

	protected Class<C> getColClass()
	{
		return colClass;
	}

	protected Class<V> getValueClass()
	{
		return valueClass;
	}

	
	
	protected Database getDatabase()
	{
		return database;
	}

	@Override
	public void refresh()
	{
		// empty the caches
		colHeaders = null;
		rowHeaders = null;
		colIndices = null;
		rowIndices = null;
		colCount = null;
		rowCount = null;
	}

}
