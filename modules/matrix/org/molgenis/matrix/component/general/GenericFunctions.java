package org.molgenis.matrix.component.general;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.QueryRule;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;

public abstract class GenericFunctions<R, C, V> implements SliceableMatrix<R, C, V>
{

	public List<R> originalRows;
	public List<C> originalCols;
	public List<R> rowCopy;
	public List<C> colCopy;

	/**
	 * Can be a generic implementation. Take the current list of generic row/col
	 * elements and slice off a part. TODO: We must identify the elements by
	 * their original index in the source matrix here. Complex filtering is not
	 * possible if we don't keep track of the actual indices somehow.
	 */
	public SliceableMatrix<R, C, V> sliceByIndex(QueryRule rule)
	{

		int val = (Integer) rule.getValue();
		int total = colCopy.size();
		switch (rule.getOperator())
		{
			case EQUALS:
				// magic - replace colCopy
				break;
			case LESS_EQUAL:
				// magic - replace colCopy
				break;
			case GREATER_EQUAL:
				// magic - replace colCopy
				break;
			case LESS:
				// magic - replace colCopy
				break;
			case GREATER:
				// magic - replace colCopy
				break;
		}
		return this;

	}

	/**
	 * TODO Can be a generic implementation. Take the current list of generic
	 * row/col elements and slice off a part simply by removing elements as
	 * specified in the limit/offset filter.
	 */
	public SliceableMatrix<R, C, V> sliceByPaging(QueryRule rule)
	{
		int val = (Integer) rule.getValue();
		int total = colCopy.size();
		switch (rule.getOperator())
		{
			case EQUALS:
				// this.setRowNames(this.getRowNames().subList(val, val+1));
				break;
			case LESS_EQUAL:
				if (rule.getField().equals("row")) rowCopy = rowCopy.subList(0, val);
				if (rule.getField().equals("col")) colCopy = colCopy.subList(0, val);
				break;
			case GREATER_EQUAL:
				if (rule.getField().equals("row")) rowCopy = rowCopy.subList(val, total);
				if (rule.getField().equals("col")) colCopy = colCopy.subList(val, total);
				break;
			case LESS:
				// this.setRowNames(this.getRowNames().subList(0, val));
				break;
			case GREATER:
				// this.setRowNames(this.getRowNames().subList(val+1, total));
				break;
		}
		return this;
	}

	public void createFresh()
	{
		this.rowCopy = new ArrayList<R>(this.getTotalNumberOfRows());
		for (R item : this.originalRows)
		{
			this.rowCopy.add(item);
		}
		this.colCopy = new ArrayList<C>(this.getTotalNumberOfCols());
		for (C item : this.originalCols)
		{
			this.colCopy.add(item);
		}
	}

	/*
	 * Implement some BasicMatrix functions
	 */

	public List<R> getVisibleRows() throws Exception
	{
		return rowCopy;
	}

	public List<C> getVisibleCols() throws Exception
	{
		return colCopy;
	}

	/*
	 * Implement some SourceMatrix functions
	 */

	public int getTotalNumberOfRows()
	{
		return originalRows.size();
	}

	public int getTotalNumberOfCols()
	{
		return originalCols.size();
	}

}
