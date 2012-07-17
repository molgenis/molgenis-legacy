package org.molgenis.datatable.test;

import java.util.Iterator;
import java.util.List;

import org.molgenis.datatable.model.AbstractFilterableTupleTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Tuple;
import org.testng.annotations.BeforeClass;

public class TestFilterableTupleTable
{

	class MockFilterableTupleTable extends AbstractFilterableTupleTable {

		@Override
		public List<Field> getColumns() throws TableException
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Tuple> getRows() throws TableException
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Iterator<Tuple> iterator()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getCount() throws TableException
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void close() throws TableException
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setVisibleColumns(List<String> fieldNames)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public List<Field> getVisibleColumns()
		{
			// TODO Auto-generated method stub
			return null;
		}
	}

	private MockFilterableTupleTable table;
	
	@BeforeClass
	public void setUp() {
		table = new MockFilterableTupleTable();
	}
	
}
