package org.molgenis.datatable.test;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.datatable.model.EntityTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.Individual;
import org.molgenis.util.Tuple;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import app.DatabaseFactory;

public class TestEntityTable {
	List<Individual> individuals = new ArrayList<Individual>();
	Database db = null;
	int size = 51;

	/**
	 * Before: create database and load a predictable list of Individual
	 * 
	 * @throws DatabaseException
	 */
	@BeforeClass
	public void setup() throws DatabaseException {
		// assumes empty database!
		db = DatabaseFactory.create();

		// load the persons
		db.remove(db.find(Individual.class));
		for (int i = 1; i <= size; i++) {
			Individual p = new Individual();
			p.setName("individual" + i);
			individuals.add(p);
		}
		db.add(individuals);
	}

	@Test
	public void testAll() throws TableException {
		TupleTable table = new EntityTable(db, Individual.class);

		// check columns
		Assert.assertEquals("id", table.getColumns().get(0).getName());
		Assert.assertEquals("name", table.getColumns().get(1).getName());

		// check count
		Assert.assertEquals(table.getCount(), size);

		// check rows
		int i = 1;
		for (Tuple row : table.getRows()) {
			Assert.assertEquals(row.getObject("name"), "individual" + i);

			i = i + 1;
		}
	}

	@Test
	public void testFiltered() throws TableException {
		TupleTable table = new EntityTable(db, Individual.class);

		((EntityTable) table).getFilters().add(
				new QueryRule("name", Operator.EQUALS, "individual2"));

		// check count
		Assert.assertEquals(table.getCount(), 1);

		// check rows
		Assert.assertEquals(table.getRows().get(0).getString("name"),
				"individual2");

	}

	@Test
	public void testLimitOffset() throws TableException {
		TupleTable table = new EntityTable(db, Individual.class);

		table.setLimit(3);
		table.setOffset(2);

		Assert.assertEquals(table.getCount(), 51);

		Assert.assertEquals(table.getRows().size(), 3);

		Assert.assertEquals(table.getRows().get(0).getString("name"),
				"individual3");
	}

	@Test
	public void testLimit() throws TableException {
		TupleTable table = new EntityTable(db, Individual.class);

		((EntityTable) table).getFilters()
				.add(new QueryRule(Operator.LIMIT, 2));

		// check count (this bypasses the limit rule)
		Assert.assertEquals(table.getCount(), size);

		// check limit (so shown in result size only)
		Assert.assertEquals(table.getRows().size(), 2);

		// check rows
		Assert.assertEquals(table.getRows().get(0).getString("name"),
				"individual1");
		Assert.assertEquals(table.getRows().get(1).getString("name"),
				"individual2");

	}
}
