package org.molgenis.matrix.component.legacy;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.pheno.Observation;

/**
 * @See SliceableMatrix
 * @param <R>
 * @param <C>
 * @param <V>
 */
public abstract class AbstractSliceableMatrix<R, C, V> implements
		SliceableMatrix<R, C, V>
{
	// set once
	public List<R> originalRows;
	public List<C> originalCols;

	// set in createFresh() and modify with slice functions
	
	//slices of rows/cols
	public List<R> rowCopy;
	public List<C> colCopy;
	
	//slices of the indexes of rows/cols in source
	public List<Integer> rowIndicesCopy;
	public List<Integer> colIndicesCopy;

	@Override
	public SliceableMatrix<R, C, V> sliceByColIndex(
			QueryRule.Operator operator, Integer index) throws Exception
	{
		List<Integer> resultIndices = new ArrayList<Integer>();
		List<C> resultC = new ArrayList<C>();
		
		this.sliceByIndex(colCopy, colIndicesCopy, resultIndices, resultC, operator, index);
	
		colIndicesCopy = resultIndices;
		colCopy = resultC;
		
		return this;
	}
	
	@Override
	public SliceableMatrix<R, C, V> sliceByRowIndex(
			QueryRule.Operator operator, Integer index) throws Exception
	{

		List<Integer> resultIndices = new ArrayList<Integer>();
		List<R> resultR = new ArrayList<R>();
		
		this.sliceByIndex(rowCopy, rowIndicesCopy, resultIndices, resultR, operator, index);
	
		rowIndicesCopy = resultIndices;
		rowCopy = resultR;
		
		return this;
	}

	@Deprecated
	public SliceableMatrix<R, C, V> sliceByIndex(MatrixQueryRule rule)
			throws Exception
	{
		if (rule.getField().equals("row"))
		{	
			return this.sliceByRowIndex(rule.getOperator(), Integer.valueOf(rule.getValue().toString()));
		}	
		else
		{
			return this.sliceByColIndex(rule.getOperator(), Integer.valueOf(rule.getValue().toString()));
		}
	}

	public SliceableMatrix<R, C, V> sliceByColOffsetLimit(int offset, int limit) throws Exception
	{
		System.out
				.println("GenericFunctions sliceByPaging(QueryRule rule) start");

		offset = offset > colCopy.size() ? rowCopy.size() : offset;
		limit = (offset + limit) > colCopy.size() ? rowCopy.size() - offset: limit;
		
		colCopy = colCopy.subList(offset, limit);
		colIndicesCopy = colIndicesCopy.subList(offset, limit);
		
		System.out
				.println("GenericFunctions sliceByPaging(QueryRule rule) ended");
		return this;
	}
	
	public SliceableMatrix<R, C, V> sliceByRowOffsetLimit(int offset, int limit) throws Exception
	{
		System.out
				.println("GenericFunctions sliceByPaging(QueryRule rule) start");

		offset = offset > colCopy.size() ? rowCopy.size() : offset;
		limit = (offset + limit) > colCopy.size() ? rowCopy.size() - offset: limit;
		
		colCopy = colCopy.subList(offset, limit);
		colIndicesCopy = colIndicesCopy.subList(offset, limit);
		
		System.out
				.println("GenericFunctions sliceByPaging(QueryRule rule) ended");
		return this;
	}
	
	/**
	 * TODO Order of filters matters, but we take care of this in our own logic!
	 * 
	 * @throws Exception
	 */
	@Deprecated
	public SliceableMatrix<R, C, V> sliceByPaging(MatrixQueryRule rule)
			throws Exception
	{
		System.out
				.println("GenericFunctions sliceByPaging(QueryRule rule) start");
		int val = (Integer) rule.getValue();
		int colTotal = colCopy.size();
		int rowTotal = rowCopy.size();
		switch (rule.getOperator())
		{
			case LIMIT:
				if (rule.getField().equals("row"))
				{
					rowCopy = rowCopy.subList(0,
							val > rowCopy.size() ? rowCopy.size() : val); // right
																			// place
																			// ??
					rowIndicesCopy = rowIndicesCopy.subList(0,
							val > rowCopy.size() ? rowCopy.size() : val);
				}
				if (rule.getField().equals("col"))
				{
					colCopy = colCopy.subList(0,
							val > colCopy.size() ? colCopy.size() : val);
					colIndicesCopy = colIndicesCopy.subList(0,
							val > colIndicesCopy.size() ? colIndicesCopy.size()
									: val);
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
					colIndicesCopy = colIndicesCopy.subList(val, colTotal);
				}
				break;
			default:
				throw new Exception("unsupported operator for paging filter");
		}
		System.out
				.println("GenericFunctions sliceByPaging(QueryRule rule) ended");
		return this;
	}

	public void reset()
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
		this.rowIndicesCopy = new ArrayList<Integer>(
				this.getTotalNumberOfRows());
		for (int i = 0; i < this.getTotalNumberOfRows(); i++)
		{
			this.rowIndicesCopy.add(i);
		}
		this.colIndicesCopy = new ArrayList<Integer>(
				this.getTotalNumberOfCols());
		for (int i = 0; i < this.getTotalNumberOfCols(); i++)
		{
			this.colIndicesCopy.add(i);
		}
	}
	
	public List<R> getRowHeaders() throws MatrixException
	{
		return rowCopy;
	}

	@Override
	public List<C> getColHeaders(Database db) throws MatrixException
	{
		return colCopy;
	}
	public List<Integer> getRowIndices() throws MatrixException
	{
		return rowIndicesCopy;
	}

	public List<Integer> getColIndices() throws MatrixException
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
	
	private <T> void  sliceByIndex(List<T> indices, List<Integer> indicesCopy, List<Integer> resultIndices, List<T> resultC,
			Operator operator, int index) throws Exception
	{
		System.out
		.println("GenericFunctions sliceByIndex(QueryRule rule) start");
		
		switch (operator)
		{
			case EQUALS:

				for (int i = 0; i < indicesCopy.size(); i++)
				{
					if (indicesCopy.get(i).intValue() == index)
					{
						resultIndices.add(indicesCopy.get(i));
						resultC.add(indices.get(i));
					}
				}

				break;
			case LESS_EQUAL:
				for (int i = 0; i < indicesCopy.size(); i++)
				{
					if (indicesCopy.get(i).intValue() <= index)
					{
						resultIndices.add(indicesCopy.get(i));
						resultC.add(indices.get(i));
					}
				}

				break;
			case GREATER_EQUAL:

				for (int i = 0; i < indicesCopy.size(); i++)
				{
					if (indicesCopy.get(i).intValue() >= index)
					{
						resultIndices.add(indicesCopy.get(i));
						resultC.add(indices.get(i));
					}
				}

				break;
			case LESS:

				for (int i = 0; i < indicesCopy.size(); i++)
				{
					if (indicesCopy.get(i).intValue() < index)
					{
						resultIndices.add(indicesCopy.get(i));
						resultC.add(indices.get(i));
					}
				}

				break;
			case GREATER:

				for (int i = 0; i < indicesCopy.size(); i++)
				{
					if (indicesCopy.get(i).intValue() > index)
					{
						resultIndices.add(indicesCopy.get(i));
						resultC.add(indices.get(i));
					}
				}

				break;
			case SORTASC:
				//
				break;
			case SORTDESC:
				//
				break;
			case NOT:

				for (int i = 0; i < indicesCopy.size(); i++)
				{
					if (indicesCopy.get(i).intValue() != index)
					{
						resultIndices.add(indicesCopy.get(i));
						resultC.add(indices.get(i));
					}
				}

				break;
			default:
				throw new Exception("unsupported operator for index filter");
		}
		
		System.out
		.println("GenericFunctions sliceByIndex(QueryRule rule) ended");
		
	}

	@Override
	public V[][] getValues() throws MatrixException
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<? extends V>[][] getValueLists(Database db) throws MatrixException
	{
		// TODO Auto-generated method stub
		return null;
	}
}
