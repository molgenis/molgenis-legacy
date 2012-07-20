package org.molgenis.datatable.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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

public class CreateProtocolData
{
	TupleTable table;
	Protocol protocol;
	Database db;

	@BeforeClass
	public void setup() throws DatabaseException
	{
		BasicConfigurator.configure();

		Logger.getLogger("org.hibernate").setLevel(Level.INFO);
		Logger.getLogger("org.hibernate.type").setLevel(Level.INFO);

		db = DatabaseFactory.create();

		final int numRows = 100;

		// clean protocols, protocolApplications, measurements, values
		db.remove(db.find(ObservedValue.class));
		db.remove(db.find(ProtocolApplication.class));
		db.remove(db.find(Protocol.class));
		db.remove(db.find(Measurement.class));
		db.remove(db.find(Individual.class));

		// generate good protocol
		protocol = new Protocol();
		protocol.setName("TestProtocol");

		final List<Measurement> mList = new ArrayList<Measurement>();
		for (int i = 1; i <= 10; i++)
		{
			final Measurement m = new Measurement();
			m.setName("meas" + i);

			db.add(m);

			protocol.getFeatures_Id().add(m.getId());

			mList.add(m);
		}

		db.add(protocol);

		// generate some protocol applications

		final List<Individual> iList = new ArrayList<Individual>();
		for (int row = 1; row <= numRows; row++)
		{
			final Individual ind = new Individual();
			ind.setName("patient" + row);
			iList.add(ind);
		}
		db.add(iList);

		final List<ProtocolApplication> paList = new ArrayList<ProtocolApplication>();
		for (int row = 0; row < iList.size(); row++)
		{
			final ProtocolApplication pa = new ProtocolApplication();
			pa.setName("pa" + row);
			pa.setProtocol(protocol.getId());
			paList.add(pa);
		}

		db.add(paList);

		List<ObservedValue> values = new ArrayList<ObservedValue>();
		for (int row = 0; row < iList.size(); row++)
		{

			for (final Measurement m : mList)
			{
				final ObservedValue v = new ObservedValue();
				v.setTarget_Id(iList.get(row).getId());
				v.setProtocolApplication(paList.get(row).getId());
				v.setFeature_Id(m.getId());
				v.setValue(m.getName() + ":val" + row);

				values.add(v);
			}

			// empty cache
			if (values.size() > 1000)
			{
				db.add(values);
				values = new ArrayList<ObservedValue>();
			}

		}

		db.add(values);

		table = new ProtocolTable(db, protocol);
	}

	@Test
	public void test1() throws TableException
	{
		// check columns
		Assert.assertEquals(table.getColumns().get(0).getName(), "target");
		Assert.assertEquals(table.getColumns().get(1).getName(), "meas1");

		// check rows
		table.getRows().size();

		for (final Tuple row : table)
		{
			// Assert.assertEquals(2, row.getFieldNames().size());
			//
			// Assert.assertEquals(true,
			// row.getFieldNames().contains("firstName"));
			// Assert.assertEquals(true,
			// row.getFieldNames().contains("lastName"));
			//
			// Assert.assertEquals(row.getObject("firstName"),"first"+i);
			// Assert.assertEquals(row.getObject("lastName"),"last"+i);
			//
			// i=i+1;
		}
	}

	@Test
	public void testLimitOffset() throws TableException
	{
		table.setLimitOffset(2, 3);

		// limit == 2
		Assert.assertEquals(table.getRows().size(), 2);

		// offset = 3, so we skip first1-first3 and expect first4
		Assert.assertEquals(table.getRows().get(0).getString("meas1"), "meas1:val3");

		// remove filters again
		table.setLimitOffset(0, 0);
	}
}
