package org.molgenis.datatable.test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.molgenis.datatable.model.QueryTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.fieldtypes.DecimalField;
import org.molgenis.fieldtypes.StringField;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Tuple;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
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

	@BeforeClass
	public void setUp() throws DatabaseException {
		db = DatabaseFactory.create();
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testGetRowsBasic() throws TableException, DatabaseException {
		final SQLTemplates dialect = new MySQLTemplates();
		SQLQueryImpl query = new SQLQueryImpl(db.getConnection(), dialect);
	
		// create select
		// SELECT Country.Name, City.Name, City.Population / Country.Population AS ratio 
		// FROM Country, City where Country.code = City.countrycode ORDER BY ratio DESC LIMIT 10;
		final PathBuilder<RelationalPath> country = new PathBuilder<RelationalPath>(RelationalPath.class,
				"Country");
		final PathBuilder<RelationalPath> city = new PathBuilder<RelationalPath>(RelationalPath.class, "City");
		query.from(country, city)
			.where(country.get("code").eq(city.get("countrycode")));

		final NumberPath<Integer> countryPopulation = country.get(new NumberPath<Integer>(Integer.class,
				"Population"));
		final NumberPath<Integer> cityPopulation = city.get(new NumberPath<Integer>(Integer.class,
				"Population"));
		final NumberExpression<Double> cityPopulationRatio = cityPopulation.divide(countryPopulation);
		query.limit(10);
		query.orderBy(cityPopulationRatio.desc());


		Field countryName = new Field("Country.Name");
		countryName.setType(new StringField());
		Field cityName = new Field("City.Name");
		cityName.setType(new StringField());
		Field ratio = new Field("ratio");
		ratio.setType(new DecimalField());
		
		LinkedHashMap<String, SimpleExpression<? extends Object>> selectMap = new LinkedHashMap<String, SimpleExpression<? extends Object>>();
		selectMap.put("Country.Name", country.get(new StringPath("name")));
		selectMap.put("City.Name", city.get(new StringPath("name")));
		selectMap.put("ratio", cityPopulationRatio);
		List<Field> columns = Arrays.asList(countryName, cityName, ratio);
		table = new QueryTable(query, selectMap, columns);		
		
		Assert.assertEquals(table.getCount(), 4079);
		// test top 10 ratios (query is limit 10)
		// 
		// 	Singapore					Singapore		1.1264
		//	Gibraltar					Gibraltar		1.081
		//	Macao						Macao			0.9249
		//	Pitcairn					Adamstown		0.84
		//	Cocos (Keeling) Islands		Bantam			0.8383
		//	Saint Pierre and Miquelon	Saint-Pierre	0.8297
		//	Falkland Islands			Stanley			0.818
		//	Palau						Koror			0.6316
		//	Djibouti					Djibouti		0.6003
		//	Cook Islands				Avarua			0.595
		final List<Tuple> rows = table.getRows();
		Assert.assertEquals(rows.get(0).getDecimal("ratio"), 1.1264, EPSILON);
		Assert.assertEquals(rows.get(1).getDecimal("ratio"), 1.081, EPSILON);
		Assert.assertEquals(rows.get(2).getDecimal("ratio"), 0.9249, EPSILON);
		Assert.assertEquals(rows.get(3).getDecimal("ratio"), 0.84, EPSILON);
		Assert.assertEquals(rows.get(4).getDecimal("ratio"), 0.8383, EPSILON);
		Assert.assertEquals(rows.get(5).getDecimal("ratio"), 0.8297, EPSILON);
		Assert.assertEquals(rows.get(6).getDecimal("ratio"), 0.818, EPSILON);
		Assert.assertEquals(rows.get(7).getDecimal("ratio"), 0.6316, EPSILON);
		Assert.assertEquals(rows.get(8).getDecimal("ratio"), 0.6003, EPSILON);
		Assert.assertEquals(rows.get(9).getDecimal("ratio"), 0.595, EPSILON);
		Assert.assertEquals(rows.size(), 10);
	}
	
	
}
