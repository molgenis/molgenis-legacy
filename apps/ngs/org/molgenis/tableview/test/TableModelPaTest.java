package org.molgenis.tableview.test;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.observ.ObservableFeature;
import org.molgenis.observ.ObservedValue;
import org.molgenis.observ.Protocol;
import org.molgenis.observ.ProtocolApplication;
import org.molgenis.tableview.TableModelProtocolApp;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import app.DatabaseFactory;

public class TableModelPaTest
{
	Database db;
	String name = "prot19";

	@BeforeClass
	public void setUp() throws DatabaseException
	{
		db = DatabaseFactory.create();

		List<ObservableFeature> features = new ArrayList<ObservableFeature>();
		for (int i = 1; i < 50; i++)
		{
			ObservableFeature f = new ObservableFeature();
			f.setIdentifier(name+"_col" + i);
			//f.setObservedCharacteristic_Id(1);
			features.add(f);
		}
		db.add(features);

		List<Integer> ids = new ArrayList<Integer>();
		for (ObservableFeature f : features)
			ids.add(f.getId());

		Protocol p = new Protocol();
		p.setIdentifier(name);
		p.setParameters_Id(ids);
		p.setTargetType("blaat");
		db.add(p);

		// simulate pa
		List<ProtocolApplication> paList = new ArrayList<ProtocolApplication>();
		for (int i = 1; i < 10000; i++)
		{
			ProtocolApplication pa = new ProtocolApplication();
			pa.setProtocolUsed_Id(p.getId());
			pa.setTarget_Id(1);
			paList.add(pa);
		}
		db.add(paList);
		
		// value
		List<ObservedValue> values = new ArrayList<ObservedValue>();
		int count = 0;
		for (ProtocolApplication pa : paList)
		{
			count++;
			for (ObservableFeature f : features)
			{
				ObservedValue v = new ObservedValue();
				v.setFeature_Id(f.getId());
				v.setProtocolApplication(pa.getId());
				v.setValue(f.getIdentifier()+"_value_"+count);
				v.setCharacteristic_Id(1);
				values.add(v);
			}
		}
		db.add(values);
	}

	@Test
	public void testSimple() throws DatabaseException
	{
		TableModelProtocolApp m = new TableModelProtocolApp(name);

		m.refresh2(db);

	}
}
