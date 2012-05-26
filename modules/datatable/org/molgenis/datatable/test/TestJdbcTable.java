package org.molgenis.datatable.test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.molgenis.datatable.model.JdbcTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.util.Tuple;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import app.DatabaseFactory;

public class TestJdbcTable
{
	List<Individual> individuals = new ArrayList<Individual>();
	Database db = null;

	/**
	 * Before: create database and load a predictable list of Individual
	 * 
	 * @throws DatabaseException
	 */
	@BeforeClass
	public void setup() throws DatabaseException
	{
		// assumes empty database!
		db = DatabaseFactory.create();
	}

	@Test
	public void testJDBCTable() throws SQLException, DatabaseException, TableException
	{	
		final TupleTable countryTable = new JdbcTable(db, "SELECT Name, Continent FROM Country", 
				Arrays.asList(new QueryRule("Code", Operator.EQUALS, "NLD")));
		
		// check columns
		Assert.assertEquals("Name", countryTable.getColumns().get(0).getName());
		Assert.assertEquals("Continent", countryTable.getColumns().get(1).getName());

		// check rows
		int i = 1;
		for (Tuple row : countryTable)
		{
			Assert.assertEquals("Netherlands", row.getString("Name"));

			i = i + 1;
		}
		countryTable.close();
		
		QueryRule sortDesc = new QueryRule();
		sortDesc.setOperator(Operator.SORTDESC);
		sortDesc.setValue("SurfaceArea");
		
		final TupleTable countryLikeCondition = new JdbcTable(db, "SELECT Name, Continent FROM Country", 
				Arrays.asList(new QueryRule("Name", Operator.LIKE, "NETH"), sortDesc));
		List<Tuple> rows = countryLikeCondition.getRows();
		Assert.assertEquals("Netherlands", rows.get(0).getString("Name"));
		Assert.assertEquals("Netherlands Antilles", rows.get(1).getString("Name"));

		final TupleTable countryLikeQuery = new JdbcTable(db, "SELECT Name, Continent FROM country WHERE Name LIKE '%NETH%' ORDER BY SurfaceArea DESC");
		int rowIdx = 0;
		for(Tuple row : countryLikeQuery) {
			Assert.assertEquals(row.getObject("Name"), rows.get(rowIdx).getObject("Name"));
			Assert.assertEquals(row.getObject("Continent"), rows.get(rowIdx).getObject("Continent"));
			++rowIdx;
		}

		
		
	}
}
