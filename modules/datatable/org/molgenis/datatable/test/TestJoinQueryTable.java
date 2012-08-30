package org.molgenis.datatable.test;

import static org.testng.AssertJUnit.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.molgenis.datatable.model.JoinQueryCreator;
import org.molgenis.datatable.model.JoinQueryCreator.Join;
import org.molgenis.datatable.model.QueryTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Tuple;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import app.DatabaseFactory;

import com.mysema.query.sql.MySQLTemplates;
import com.mysema.query.sql.SQLTemplates;

public class TestJoinQueryTable
{
	private QueryTable table;
	private Database db;

	@BeforeMethod
	public void setUp() throws DatabaseException
	{
		db = DatabaseFactory.create();

		final SQLTemplates dialect = new MySQLTemplates();

		final List<String> tableNames = Arrays.asList("Country", "City");

		final List<Join> joins = Arrays.asList(new Join("Country", "Code", "City", "CountryCode"));

		// should work with no columns selected
		final List<String> columnNames = null;
		final List<String> hiddenFilterColumns = null;
		final JoinQueryCreator tableCreator = new JoinQueryCreator(db, tableNames, columnNames, hiddenFilterColumns,
				joins);
		table = new QueryTable(tableCreator, db.getConnection(), dialect);
	}

	@Test
	public void testInitialisation() throws TableException
	{
		assertEquals(table.getColumns().size(), 20);
		assertEquals(table.getCount(), 4079);
		final Field leftJoinColumn = findField("Code");
		final Field rightJoinColumn = findField("CountryCode");
		// values should be equal in all join columns
		for (final Tuple row : table)
		{
			assertEquals(row.getObject(leftJoinColumn.getName()), row.getObject(rightJoinColumn.getName()));
		}
	}

	@Test
	public void testLimitAndOffset() throws TableException
	{
		final int limit = 10;
		final int offset = 4;
		table.setLimit(limit);
		table.setOffset(offset);
		assertEquals(table.getRows().size(), limit);
		assertEquals("AFG", table.getRows().get(0).getString("Country.Code"));
		assertEquals("Mazar-e-Sharif", table.getRows().get(0).getString("City.Name"));
	}

	@Test
	public void testFilter() throws DatabaseException, TableException
	{
		// create select
		// SELECT Country.Name, City.Name FROM Country, City
		// WHERE Country.code = City.countrycode AND Country.code = 'NLD' and
		// City.Population > 100000
		// ORDER BY City.population DESC LIMIT 10;
		final SQLTemplates dialect = new MySQLTemplates();

		final List<String> tableNames = Arrays.asList("Country", "City");
		final List<String> columnNames = Arrays.asList("Country.Name", "City.Name", "Country.Code", "City.Population");

		final List<Join> joins = Arrays.asList(new Join("Country", "Code", "City", "CountryCode"));

		final List<QueryRule> rules = Arrays.asList(new QueryRule("Country.Code", Operator.EQUALS, "NLD"),
				new QueryRule("City.Population", Operator.GREATER, 10000), new QueryRule(Operator.SORTDESC,
						"City.Population"));

		final List<String> hiddenFilterColumns = null;
		final JoinQueryCreator tableCreator = new JoinQueryCreator(db, tableNames, columnNames, hiddenFilterColumns,
				joins);
		table = new QueryTable(tableCreator, db.getConnection(), dialect);
		table.setFilters(rules);

		final List<Tuple> rows = table.getRows();
		assertEquals("Amsterdam", rows.get(0).getString("City.Name"));
		assertEquals("Rotterdam", rows.get(1).getString("City.Name"));
		assertEquals("Haag", rows.get(2).getString("City.Name"));
		assertEquals("Utrecht", rows.get(3).getString("City.Name"));
		assertEquals("Eindhoven", rows.get(4).getString("City.Name"));

		// reverse sorting
		rules.set(rules.size() - 1, new QueryRule(Operator.SORTASC, "City.Population"));
		table.setFilters(rules);
		final List<Tuple> rows2 = table.getRows();
		assertEquals("Alkmaar", rows2.get(0).getString("City.Name"));
		assertEquals("Heerlen", rows2.get(1).getString("City.Name"));
		assertEquals("Delft", rows2.get(2).getString("City.Name"));
		assertEquals("Ede", rows2.get(3).getString("City.Name"));
		assertEquals("Zwolle", rows2.get(4).getString("City.Name"));
	}

	// /// UTIL ////

	private Field findField(final String fieldName) throws TableException
	{
		return (Field) CollectionUtils.find(table.getColumns(), new Predicate()
		{
			@Override
			public boolean evaluate(Object arg0)
			{
				return ((Field) arg0).getName().equals(fieldName);
			}
		});
	}

}
