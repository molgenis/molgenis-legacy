package org.molgenis.datatable.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.molgenis.datatable.model.FilterableTupleTable;
import org.molgenis.datatable.model.ProtocolTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.model.elements.Field;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Tuple;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import app.DatabaseFactory;

public class TestProtocolTable
{
	FilterableTupleTable table;

	Database db;

	@BeforeClass
	public void setup() throws DatabaseException
	{
		BasicConfigurator.configure();

		db = DatabaseFactory.create();
		Protocol protocol = db.query(Protocol.class).eq(Protocol.NAME, "TestProtocol").find().get(0);
		table = new ProtocolTable(db, protocol);
	}

	@Test
	public void testFilter() throws TableException, DatabaseException
	{
		final List<QueryRule> filters = new ArrayList<QueryRule>();
		filters.add(new QueryRule("target", Operator.EQUALS, "patient1"));
		filters.add(new QueryRule("meas1", Operator.EQUALS, "meas1:val0"));
		filters.add(new QueryRule("meas2", Operator.EQUALS, "meas2:val0"));
		table.setFilters(filters);

		List<Tuple> rows = table.getRows();
		Assert.assertEquals(rows.size(), 1);

		for (final Tuple t : table.getRows())
		{
			System.out.println(t);
		}
	}

	@Test
	public void testColLimitOffset() throws TableException
	{
		table.setLimit(5);

		table.setColLimit(5);

		List<Field> cols = table.getColumns();
		Assert.assertEquals(cols.size(), 5);
		Assert.assertEquals(cols.get(0).getName(), "target");
		Assert.assertEquals(cols.get(1).getName(), "meas1");

		List<Tuple> rows = table.getRows();
		Assert.assertEquals(rows.size(), 5);
		Assert.assertEquals(rows.get(0).getString("target"), "patient1");
		Assert.assertEquals(rows.get(0).getString("meas4"), "meas4:val0");

		table.setColOffset(6);

		cols = table.getColumns();
		Assert.assertEquals(cols.size(), 5);

		// we expect 1 row header + 10 columns
		Assert.assertEquals(table.getAllColumns().size(), 11);
		Assert.assertEquals(cols.get(0).getName(), "meas6");

		rows = table.getRows();
		Assert.assertEquals(rows.get(0).getString("meas6"), "meas6:val0");

		table.reset();
	}

	@Test
	public void testAllColumns() throws TableException
	{
		List<Field> fields = table.getAllColumns();
		Assert.assertEquals(fields.get(0).getName(), "target");
	}
}
