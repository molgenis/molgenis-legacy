package org.molgenis.datatable.test;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.molgenis.datatable.model.AbstractFilterableTupleTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Tuple;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestFilterableTupleTable
{

	class MockFilterableTupleTable extends AbstractFilterableTupleTable
	{

		final TupleTable d_nested;

		MockFilterableTupleTable(TupleTable table)
		{
			d_nested = table;
		}

		@Override
		public List<Field> getColumns() throws TableException
		{
			return d_nested.getColumns();
		}

		@Override
		public List<Tuple> getRows() throws TableException
		{
			return d_nested.getRows();
		}

		@Override
		public Iterator<Tuple> iterator()
		{
			return d_nested.iterator();
		}

		@Override
		public int getCount() throws TableException
		{
			return d_nested.getCount();
		}

		@Override
		public void close() throws TableException
		{
			d_nested.close();
		}

		@Override
		public void setFilters(List<QueryRule> rules) throws TableException
		{
			// TODO Auto-generated method stub

		}

		@Override
		public List<QueryRule> getFilters()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public QueryRule getSortRule()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Field> getAllColumns() throws TableException
		{
			// TODO Auto-generated method stub
			return null;
		}
	}

	private MockFilterableTupleTable table;

	@BeforeClass
	public void setUp()
	{
		table = new MockFilterableTupleTable(MemoryTableFactory.create());
	}

	@Test
	public void testInitialization() throws TableException
	{
		assertEquals(table.getLimit(), -1);
		assertEquals(table.getOffset(), -1);
		assertEquals(table.getSortRule(), null);
		assertEquals(table.getCount(), 5);
	}

	@Test
	public void testPaging() throws TableException
	{
		table.setFilters(Arrays.asList(new QueryRule(Operator.LIMIT, 3)));
		assertEquals(table.getLimit(), 3);
		assertEquals(table.getRows().size(), 3);
		assertEquals(table.getRows().get(0).getString("firstName"), "first1");
	}
}
