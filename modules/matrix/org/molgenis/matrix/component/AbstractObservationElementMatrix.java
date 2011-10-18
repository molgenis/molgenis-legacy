package org.molgenis.matrix.component;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.general.MatrixColHeaderFilter;
import org.molgenis.matrix.component.general.MatrixColValueFilter;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.matrix.component.general.MatrixRowHeaderFilter;
import org.molgenis.matrix.component.general.MatrixRowValueFilter;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.pheno.Observation;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;

/** Abstract observation matrix */
public abstract class AbstractObservationElementMatrix<R extends ObservationElement, C extends ObservationElement, V extends Observation> implements
		SliceableMatrix<R, C, V>
{

	protected Database database;
	protected Class<R> rowClass;
	protected Class<C> colClass;
	protected Class<? extends V> valueClass;
	protected List<C> colHeaders = null;
	protected List<R> rowHeaders = null;
	protected List<MatrixQueryRule> rules = new ArrayList<MatrixQueryRule>();
	protected boolean rowDirty = true;
	protected boolean colDirty = true;
	protected int rowLimit = 10;
	protected int rowOffset = 0;
	protected int colLimit = 10;
	protected int colOffset = 0;

	@Override
	public SliceableMatrix<R, C, V> slice(MatrixQueryRule rule) throws MatrixException
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
	public List<Integer> getRowIndices() throws MatrixException
	{
		// retrieve the indices from the headers (we use the id value).
		List<Integer> rowIndices = new ArrayList<Integer>();
		for (R row : getRowHeaders())
		{
			rowIndices.add(row.getId());
		}
		return rowIndices;
	}

	@Override
	public List<Integer> getColIndices() throws MatrixException
	{
		// get col indexes from col headers
		List<Integer> colIndices = new ArrayList<Integer>();
		for (C col : getColHeaders())
		{
			colIndices.add(col.getId());
		}
		return colIndices;
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

	protected Class<R> getRowClass()
	{
		return rowClass;
	}

	protected void setRowClass(Class<R> rowClass)
	{
		this.rowClass = rowClass;
	}

	protected Class<C> getColClass()
	{
		return colClass;
	}

	protected void setColClass(Class<C> colClass)
	{
		this.colClass = colClass;
	}

	protected Class<? extends V> getValueClass()
	{
		return valueClass;
	}

	protected void setValueClass(Class<V> valueClass)
	{
		this.valueClass = valueClass;
	}

	@SuppressWarnings("unchecked")
	protected V[] create(int rows)
	{
		return (V[]) new Object[rows];
	}

	@Override
	public SliceableMatrix<R, C, V> sliceByColIndex(Operator operator, Integer index) throws Exception
	{
		// rewrite as sliceByColProperty(id)
		return this.sliceByColProperty(ObservedValue.ID, operator, index);
	}

	@Override
	public SliceableMatrix<R, C, V> sliceByRowIndex(Operator operator, Integer index) throws Exception
	{
		// this is actually a rowProperty slice!
		return this.sliceByRowProperty(ObservedValue.ID, operator, index);
	}

	@Override
	public SliceableMatrix<R, C, V> sliceByRowOffsetLimit(int limit, int offset)
			throws Exception
	{
		this.rowLimit = limit;
		this.rowOffset = offset;
		return this;
	}

	@Override
	public SliceableMatrix<R, C, V> sliceByColOffsetLimit(int limit, int offset)
			throws Exception
	{
		this.colLimit = limit;
		this.colOffset = offset;
		return this;
	}

	@Override
	public SliceableMatrix<R, C, V> sliceByRowValues(int rowIndex, Operator operator, Object value)
			throws Exception
	{
		// slice by rowIndex means effectively ObservedValue.target=index &&
		// ObervedValue.value=value!
		return this.slice(new MatrixRowValueFilter(rowIndex,
				ObservedValue.VALUE, operator, value));
	}

	@Override
	public SliceableMatrix<R, C, V> sliceByRowValues(R row, Operator operator, Object value)
			throws Exception
	{
		// slice by rowIndex means effectively ObservedValue.target=row.getId()
		// && ObervedValue.value=value!
		if (row.getId() == null) throw new MatrixException(
				"row.getId() not set for sliceByRowValues(" + row + ")");
		return this.slice(new MatrixRowValueFilter(row.getId(),
				ObservedValue.VALUE, operator, value));
	}

	@Override
	public SliceableMatrix<R, C, V> sliceByColValues(int colIndex, Operator operator, Object value)
			throws Exception
	{
		// slice by rowIndex means effectively ObservedValue.feature=index &&
		// ObervedValue.value=value!
		return this.slice(new MatrixColValueFilter(colIndex,
				ObservedValue.VALUE, operator, value));
	}

	public SliceableMatrix<R, C, V> sortCol(int colIndex, Operator operator) throws MatrixException
	{
		//
		// sort by value
		return this.slice(new MatrixColValueFilter(colIndex,
				ObservedValue.VALUE, operator));
	}

	public SliceableMatrix<R, C, V> sortCol(Integer colIndex, String colProperty, Operator operator)
			throws MatrixException
	{
		return this.slice(new MatrixColValueFilter(colIndex, colProperty,
				operator));
	}

	@Override
	public SliceableMatrix<R, C, V> sliceByColValues(C col, Operator operator, Object value)
			throws Exception
	{
		// slice by rowIndex means effectively ObservedValue.target=row.getId()
		// && ObervedValue.value=value!
		if (col.getId() == null) throw new MatrixException(
				"col.getId() not set for sliceByColValues(" + col + ")");
		return this.slice(new MatrixColValueFilter(col.getId(),
				ObservedValue.VALUE, operator, value));
	}

	public SliceableMatrix<R, C, V> sortByColValues(C col, Operator operator)
			throws MatrixException
	{
		if (col.getId() == null) throw new MatrixException(
				"col.getId() not set for sortByColValues(" + col + ")");
		return this.slice(new MatrixColValueFilter(col.getId(),
				ObservedValue.VALUE, operator, col.getId()));
	}

	@Override
	public SliceableMatrix<R, C, V> sliceByColValueProperty(C col, String property,
			Operator operator, Object value) throws MatrixException
	{
		if (col.getId() == null) throw new MatrixException(
				"col.getId() not set for sortByColValues(" + col + ")");
		return this.slice(new MatrixColValueFilter(col.getId(),
				property, operator, value));
	}

	@Override
	public SliceableMatrix<R, C, V> sliceByColValueProperty(int colIndex, String property,
			Operator operator, Object value) throws MatrixException
	{
		return this.slice(new MatrixColValueFilter(colIndex,
				property, operator, value));
	}

	@Override
	public SliceableMatrix<R, C, V> sliceByRowValueProperty(R row, String property,
			Operator operator, Object value) throws MatrixException
	{
		if (row.getId() == null) throw new MatrixException(
				"row.getId() not set for sortByColValues(" + row + ")");
		return this.slice(new MatrixRowValueFilter(row.getId(),
				property, operator, value));
	}

	@Override
	public SliceableMatrix<R, C, V> sliceByRowValueProperty(int rowIndex, String property,
			Operator operator, Object value) throws MatrixException
	{
		return this.slice(new MatrixRowValueFilter(rowIndex,
				property, operator, value));
	}
	
	@Override
	public SliceableMatrix<R, C, V> sliceByRowProperty(String property, Operator operator, Object value)
			throws MatrixException
	{
		return this.slice(new MatrixRowHeaderFilter(property, operator, value));
	}

	@Override
	public SliceableMatrix<R, C, V> sliceByColProperty(String property, Operator operator, Object value)
			throws Exception
	{
		return this.slice(new MatrixColHeaderFilter(property, operator, value));
	}

	void validate(MatrixQueryRule rule) throws MatrixException
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
	public List<MatrixQueryRule> getRules()
	{
		return rules;
	}
	
	/**
	 * Empty filter rules and cached data.
	 */
	@Override
	public void refresh() throws MatrixException
	{
		this.reset();
	}
	
	/**
	 * Empty filter rules and cached data.
	 */
	@Override
	public void reset() throws MatrixException
	{
		// first empty the rules
		this.rules = new ArrayList<MatrixQueryRule>();

		this.reload();
	}
	
	/**
	 * Empty caches and reload matrix data, whilst keeping any
	 * filters intact.
	 * 
	 * @throws MatrixException
	 */
	@Override
	public void reload() throws MatrixException
	{
		// empty the caches
		colDirty = true;
		colOffset = 0;
		rowDirty = true;
		rowOffset = 0;
	}
}
