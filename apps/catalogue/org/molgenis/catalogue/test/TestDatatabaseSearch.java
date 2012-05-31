package org.molgenis.catalogue.test;

import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.pheno.Measurement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import app.DatabaseFactory;

public class TestDatatabaseSearch {

	private Database db;

	@BeforeClass
	public void setUp() throws DatabaseException {
		BasicConfigurator.configure();
		db = DatabaseFactory.create();
	}
	
	@Test
	public void testSearch() throws DatabaseException
	{
		//ensure something is there
		if(0 == db.query(Measurement.class).eq(Measurement.NAME, "testmonkey").count())
		{
			Measurement m = new Measurement();
			m.setName("testmonkey");
			db.add(m);
		}
		
		List<Measurement> mList = db.query(Measurement.class).limit(100).search("monkey").find();
		
		boolean found = false;
		for(Measurement m: mList)
		{
			//if it runs, and finds something, it is okay.
			if("testmonkey".equals(m.getName())) found = true;
		}
		Assert.assertEquals(true,found);
		
		
	}
	
	
}
