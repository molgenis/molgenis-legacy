package org.molgenis.datatable.test;

import static org.testng.AssertJUnit.assertEquals;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.molgenis.datatable.model.QueryCreator;
import org.molgenis.datatable.model.QueryTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.fieldtypes.DecimalField;
import org.molgenis.fieldtypes.StringField;
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
import com.mysema.query.sql.RelationalPath;
import com.mysema.query.sql.SQLQueryImpl;
import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.types.expr.NumberExpression;
import com.mysema.query.types.expr.SimpleExpression;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.PathBuilder;
import com.mysema.query.types.path.StringPath;

public class TestQueryTable
{

	private static final double EPSILON = 0.0001;
	private QueryTable table;
	private Database db;
	private SQLTemplates dialect = new MySQLTemplates();

	class MyQuery implements QueryCreator
	{
		private NumberExpression<Double> cityPopulationRatio;
		private PathBuilder<RelationalPath> city;
		private PathBuilder<RelationalPath> country;

		@Override
		public SQLQueryImpl createQuery(Connection connection, SQLTemplates dialect)
		{
			SQLQueryImpl query = new SQLQueryImpl(connection, dialect);

			// create select
			// SELECT Country.Name, City.Name, City.Population /
			// Country.Population AS ratio
			// FROM Country, City where Country.code = City.countrycode
			// ORDER BY ratio DESC LIMIT 10;
			country = new PathBuilder<RelationalPath>(RelationalPath.class, "Country");
			city = new PathBuilder<RelationalPath>(RelationalPath.class, "City");
			query.from(country, city).where(country.get("code").eq(city.get("countrycode")));

			final NumberPath<Integer> countryPopulation = country.get(new NumberPath<Integer>(Integer.class,
					"Population"));
			final NumberPath<Integer> cityPopulation = city.get(new NumberPath<Integer>(Integer.class, "Population"));
			cityPopulationRatio = cityPopulation.divide(countryPopulation);
			query.limit(10);
			query.orderBy(cityPopulationRatio.desc());
			return query;
		}

		@Override
		public LinkedHashMap<String, SimpleExpression<? extends Object>> getAttributeExpressions()
		{
			LinkedHashMap<String, SimpleExpression<? extends Object>> selectMap = new LinkedHashMap<String, SimpleExpression<? extends Object>>();
			selectMap.put("Country.Name", country.get(new StringPath("name")));
			selectMap.put("City.Name", city.get(new StringPath("name")));
			selectMap.put("ratio", cityPopulationRatio);
			return selectMap;
		}

		@Override
		public List<Field> getFields()
		{
			final Field countryName = new Field("Country.Name");
			countryName.setType(new StringField());
			final Field cityName = new Field("City.Name");
			cityName.setType(new StringField());
			final Field ratio = new Field("ratio");
			ratio.setType(new DecimalField());

			return Arrays.asList(countryName, cityName, ratio);
		}

		@Override
		public List<String> getHiddenFieldNames()
		{
			// TODO Auto-generated method stub
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	@BeforeMethod
	public void setUp() throws DatabaseException
	{
		db = DatabaseFactory.create();
		table = new QueryTable(new MyQuery(), db.getConnection(), dialect);
	}

	@Test
	public void testGetRowsBasic() throws TableException, DatabaseException
	{
		assertEquals(table.getCount(), 4079);
		// test top 10 ratios (query is limit 10)
		//
		// Singapore Singapore 1.1264
		// Gibraltar Gibraltar 1.081
		// Macao Macao 0.9249
		// Pitcairn Adamstown 0.84
		// Cocos (Keeling) Islands Bantam 0.8383
		// Saint Pierre and Miquelon Saint-Pierre 0.8297
		// Falkland Islands Stanley 0.818
		// Palau Koror 0.6316
		// Djibouti Djibouti 0.6003
		// Cook Islands Avarua 0.595
		final List<Tuple> rows = table.getRows();
		assertEquals(rows.get(0).getDecimal("ratio"), 1.1264, EPSILON);
		assertEquals(rows.get(1).getDecimal("ratio"), 1.081, EPSILON);
		assertEquals(rows.get(2).getDecimal("ratio"), 0.9249, EPSILON);
		assertEquals(rows.get(3).getDecimal("ratio"), 0.84, EPSILON);
		assertEquals(rows.get(4).getDecimal("ratio"), 0.8383, EPSILON);
		assertEquals(rows.get(5).getDecimal("ratio"), 0.8297, EPSILON);
		assertEquals(rows.get(6).getDecimal("ratio"), 0.818, EPSILON);
		assertEquals(rows.get(7).getDecimal("ratio"), 0.6316, EPSILON);
		assertEquals(rows.get(8).getDecimal("ratio"), 0.6003, EPSILON);
		assertEquals(rows.get(9).getDecimal("ratio"), 0.595, EPSILON);
		assertEquals(rows.size(), 10);

		// add extra where
		// query.where(cityPopulationRatio.gt(1));
		table.getFilters().add(new QueryRule("ratio", Operator.GREATER, 1));
		final List<Tuple> rows2 = table.getRows();
		assertEquals(rows2.size(), 2);

		table.close();
	}

	@Test
	public void testIterate() throws TableException
	{
		// test top 3 ratios (query still has extra where clause))
		//
		// Singapore Singapore 1.1264
		// Gibraltar Gibraltar 1.081
		// Macao Macao 0.9249

		final List<Tuple> rows = new ArrayList<Tuple>();
		for (final Tuple t : table)
		{
			rows.add(t);
		}
		assertEquals(rows.get(0).getDecimal("ratio"), 1.1264, EPSILON);
		assertEquals(rows.get(1).getDecimal("ratio"), 1.081, EPSILON);
		assertEquals(rows.get(2).getDecimal("ratio"), 0.9249, EPSILON);
		table.close();
	}

	@Test
	public void testTupleSetLimit() throws TableException
	{
		table.setLimit(5);
		assertEquals(table.getRows().size(), 5);
	}

	@Test
	public void testTupleSetOffset() throws TableException
	{
		// First 3 should be skipped
		// Singapore Singapore 1.1264
		// Gibraltar Gibraltar 1.081
		// Macao Macao 0.9249
		// Pitcairn Adamstown 0.84
		// Cocos (Keeling) Islands Bantam 0.8383
		// Saint Pierre and Miquelon Saint-Pierre 0.8297

		table.setOffset(3);
		final List<Tuple> rows = table.getRows();

		// limit is still 10
		assertEquals(rows.size(), 10);
		assertEquals(rows.get(0).getDecimal("ratio"), 0.84, EPSILON);
		assertEquals(rows.get(1).getDecimal("ratio"), 0.8383, EPSILON);
		assertEquals(rows.get(2).getDecimal("ratio"), 0.8297, EPSILON);
	}

}
