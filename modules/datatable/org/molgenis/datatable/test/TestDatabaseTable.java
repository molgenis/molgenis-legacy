package org.molgenis.datatable.test;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.datatable.model.DatabaseTable;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.datatable.view.HtmlTableView;
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
	TupleTable table;
	
	@BeforeClass
	public void setup() throws DatabaseException
	{
		//assumes empty database!
		Database db = DatabaseFactory.create();
		
		//db.dropDatabase();
		//db.createDatabase();
		
		//load the persons
		db.remove(db.find(Individual.class));
		for(int i = 1; i <= 5; i++)
		{
			Individual p = new Individual();
			p.setName("individual"+i);
			individuals.add(p);
		}
		db.add(individuals);
		
		//load 
		table = new DatabaseTable(db, Individual.class);
	}
	
	@Test
	public void test1()
	{
		//check columns
		Assert.assertEquals("id", table.getColumns().get(0).getName());
		Assert.assertEquals("name", table.getColumns().get(1).getName());
		
		//check rows
		int i = 1;
		for(Tuple row: table)
		{
			Assert.assertEquals(row.getObject("name"), "individual"+i);
			
			i = i + 1;
		}
	}
	
	@Test
	public void test2()
	{
		HtmlTableView view =  new HtmlTableView("test",table);
		
		System.out.println(view.render());
	}
}
