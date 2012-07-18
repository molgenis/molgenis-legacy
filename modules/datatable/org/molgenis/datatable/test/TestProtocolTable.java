package org.molgenis.datatable.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.molgenis.datatable.model.ProtocolTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Tuple;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import app.DatabaseFactory;

public class TestProtocolTable
{
	TupleTable table;
	
	@BeforeClass
	public void setup() throws DatabaseException
	{
		BasicConfigurator.configure();
		
		Database db = DatabaseFactory.create();
		
		//clean protocols, protocolApplications, measurements, values
//		db.remove(db.find(ObservedValue.class));
//		db.remove(db.find(Measurement.class));
//		db.remove(db.find(ProtocolApplication.class));
//		db.remove(db.find(Protocol.class));
//		db.remove(db.find(Individual.class));
		
		//generate good protocol
		Protocol p = new Protocol();
		p.setName("TestProtocol");
		
		List<Measurement> mList = new ArrayList<Measurement>();
		for(int i = 1; i <= 10; i++)
		{
			Measurement m = new Measurement();
			m.setName("meas"+i);
			
			db.add(m);		
			p.getFeatures_Id().add(m.getId());
			
			mList.add(m);
		}
		
		db.add(p);
		
		//generate some protocol applications
		List<ObservedValue> values = new ArrayList<ObservedValue>();
		
		for(int row = 1; row <= 51; row++)
		{
			Individual ind = new Individual();
			ind.setName("patient"+row);
			db.add(ind);
			
			ProtocolApplication pa = new ProtocolApplication();
			pa.setName("pa"+row);
			pa.setProtocol(p.getId());
			db.add(pa);
			
			for(Measurement m: mList)
			{
				ObservedValue v = new ObservedValue();
				v.setTarget_Id(ind.getId());
				v.setProtocolApplication(pa);
				v.setFeature_Id(m.getId());
				v.setValue(m.getName()+":val"+row);
				
				values.add(v);
			}
		}
		
		db.add(values);
		
		table = new ProtocolTable(db, p);
	}
	
	@Test
	public void test1() throws TableException
	{
		//check columns
		Assert.assertEquals(table.getColumns().get(0).getName(), "target");
		Assert.assertEquals(table.getColumns().get(1).getName(), "meas1");
		
		//check rows
		Assert.assertEquals(51, table.getRows().size());
		
		//check iterator
		int i = 1;
		for(Tuple row: table)
		{
//			Assert.assertEquals(2, row.getFieldNames().size());
//			
//			Assert.assertEquals(true, row.getFieldNames().contains("firstName"));
//			Assert.assertEquals(true, row.getFieldNames().contains("lastName"));
//			
//			Assert.assertEquals(row.getObject("firstName"),"first"+i);
//			Assert.assertEquals(row.getObject("lastName"),"last"+i);
//			
//			i=i+1;
		}
	}
	
	@Test
	public void testLimitOffset() throws TableException
	{
		table.setLimitOffset(2, 3);
		
		//limit == 2
		Assert.assertEquals(table.getRows().size(), 2);
		
		//offset = 3, so we skip first1-first3 and expect first4
		Assert.assertEquals(table.getRows().get(0).getString("meas1"), "meas1:val3");
		
		//remove filters again
		table.setLimitOffset(0, 0);
	}
}
