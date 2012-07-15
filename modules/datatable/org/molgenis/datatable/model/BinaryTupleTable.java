package org.molgenis.datatable.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import matrix.DataMatrixInstance;
import matrix.implementations.binary.BinaryDataMatrixInstance;

import org.molgenis.model.elements.Field;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

public class BinaryTupleTable extends AbstractTupleTable
{
	private BinaryDataMatrixInstance matrix;
	private List<Field> columns = null;

	public BinaryTupleTable(File matrixFile) throws Exception
	{
		this.matrix = new BinaryDataMatrixInstance(matrixFile);
	}

	@Override
	public List<Field> getColumns() throws TableException
	{
		if (columns == null)
		{
			for (String name : matrix.getColNames())
			{
				columns.add(new Field(name));
			}
		}
		return columns;
	}

	@Override
	public List<Tuple> getRows() throws TableException
	{
		List<Tuple> result = new ArrayList<Tuple>();

		for (Tuple row : this)
		{
			result.add(row);
		}

		return result;
	}

	private static class BinaryMatrixIterator implements Iterator<Tuple>
	{
		int count = 0;
		// source data
		DataMatrixInstance matrix;
		// wrapper state
		TupleTable table;
		
		//colLimit
		int colLimit;

		BinaryMatrixIterator(DataMatrixInstance matrix, TupleTable table)
		{
			this.matrix = matrix;
			this.table = table;
			
			colLimit = table.getColLimit() == 0 ? matrix.getNumberOfCols() : table.getColLimit();
		}

		@Override
		public boolean hasNext()
		{
			if (table.getOffset() + count >= matrix.getNumberOfRows() || (table.getLimit() > 0 && count >= table.getLimit()) )
			{
				return false;
			}
			return true;
		}

		@Override
		public Tuple next()
		{			
			try
			{
				Tuple result = new SimpleTuple();
				
				DataMatrixInstance memory = matrix.getSubMatrixByOffset(table.getOffset() + count, 1, table.getColOffset(), colLimit);
				
				for(String name: memory.getColNames())
				{
					result.set(name, memory.getCol(name)[0]);
				}
				
				//expected: tuple {target=rowname, colname1=value1, colname2=value2}

				count++;
				
				return result;
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}

		@Override
		public void remove()
		{
			// TODO Auto-generated method stub
		}

	}

	@Override
	public Iterator<Tuple> iterator()
	{
		return new BinaryMatrixIterator(matrix, this);
	}

	@Override
	public int getCount() throws TableException
	{
		return matrix.getNumberOfRows();
	}

	@Override
	public int getColCount() throws TableException
	{
		return matrix.getNumberOfCols();
	}
}
