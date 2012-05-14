package org.molgenis.datatable.test;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.datatable.model.DatabaseTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.pheno.Individual;
import org.molgenis.util.Tuple;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import app.DatabaseFactory;

public class TestDatabaseTable
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

		// db.dropDatabase();
		// db.createDatabase();

		// load the persons
		db.remove(db.find(Individual.class));
		for (int i = 1; i <= 5; i++)
		{
			Individual p = new Individual();
			p.setName("individual" + i);
			individuals.add(p);
		}
		db.add(individuals);

		// load
	}

	@Test
	public void test1() throws TableException
	{
		TupleTable table = new DatabaseTable(db, Individual.class);
		
		// check columns
		Assert.assertEquals("id", table.getColumns().get(0).getName());
		Assert.assertEquals("name", table.getColumns().get(1).getName());

		// check rows
		int i = 1;
		for (Tuple row : table)
		{
			Assert.assertEquals(row.getObject("name"), "individual" + i);

			i = i + 1;
		}
	}
}
