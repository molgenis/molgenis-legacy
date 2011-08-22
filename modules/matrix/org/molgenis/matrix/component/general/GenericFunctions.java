package org.molgenis.matrix.component.general;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.QueryRule;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;

public abstract class GenericFunctions<R, C, V> implements SliceableMatrix<R, C, V>
{
	//set once
	public List<R> originalRows;
	public List<C> originalCols;
	
	//set in createFresh() and modify with slice functions
	public List<R> rowCopy;
	public List<C> colCopy;
	public List<Integer> rowIndicesCopy;
	public List<Integer> colIndicesCopy;

	/**
	 * Can be a generic implementation. Take the current list of generic row/col
	 * elements and slice off a part. TODO: We must identify the elements by
	 * their original index in the source matrix here. Complex filtering is not
	 * possible if we don't keep track of the actual indices somehow.
	 */
	public SliceableMatrix<R, C, V> sliceByIndex(MatrixQueryRule rule)
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
	 * TODO Order of filters matters, but we take care of this in our own logic!
	 * 
	 * @throws Exception 
	 */
	public SliceableMatrix<R, C, V> sliceByPaging(MatrixQueryRule rule) throws Exception
	{
		System.out.println("GenericFunctions sliceByPaging(QueryRule rule) start");
		int val = (Integer) rule.getValue();
		int colTotal = colCopy.size();
		int rowTotal = rowCopy.size();
		switch (rule.getOperator())
		{
			case LIMIT:
				if (rule.getField().equals("row"))
				{
					rowCopy = rowCopy.subList(0, val > rowCopy.size() ? rowCopy.size() : val); //right place ??
					rowIndicesCopy = rowIndicesCopy.subList(0, val > rowCopy.size() ? rowCopy.size() : val);
				}
				if (rule.getField().equals("col"))
				{
					colCopy = colCopy.subList(0, val > colCopy.size() ? colCopy.size() : val);
					colIndicesCopy = colIndicesCopy.subList(0, val > colIndicesCopy.size() ? colIndicesCopy.size() : val);
				}
				break;
			case OFFSET:
				if (rule.getField().equals("row"))
				{
					rowCopy = rowCopy.subList(val, rowTotal);
					rowIndicesCopy = rowIndicesCopy.subList(val, rowTotal);
				}
				if (rule.getField().equals("col"))
				{
					colCopy = colCopy.subList(val, colTotal);
					colIndicesCopy = colIndicesCopy.subList(val, rowTotal);
				}
				break;
			default:
				throw new Exception("unsupported operator for paging");
		}
		System.out.println("GenericFunctions sliceByPaging(QueryRule rule) ended");
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
		this.rowIndicesCopy = new ArrayList<Integer>(this.getTotalNumberOfRows());
		for(int i=0; i<this.getTotalNumberOfRows(); i++){
			this.rowIndicesCopy.add(i);
		}
		this.colIndicesCopy = new ArrayList<Integer>(this.getTotalNumberOfCols());
		for(int i=0; i<this.getTotalNumberOfCols(); i++){
			this.colIndicesCopy.add(i);
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
	
	public List<Integer> getRowIndices() throws Exception
	{
		return rowIndicesCopy;
	}
	
	public List<Integer> getColIndices() throws Exception
	{
		return colIndicesCopy;
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
