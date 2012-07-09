package org.molgenis.omicsconnect.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.observ.DataSet;
import org.molgenis.observ.ObservableFeature;
import org.molgenis.observ.ObservationSet;
import org.molgenis.observ.ObservedValue;
import org.molgenis.observ.Protocol;
import org.molgenis.observ.target.Individual;

import app.DatabaseFactory;

public class GenerateExampleData
{
	public static void main(String[] args) throws DatabaseException
	{
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.DEBUG);
		
		int noColumns = 1000;
		
		int noRows = 1000;
		
		Database db = DatabaseFactory.create();
		try
		{
			db.beginTx();

			// create protocol
			Protocol p = new Protocol();
			p.setIdentifier("p2test");
			p.setName("dit is een test");
			p.setTargetType("Individual");

			db.add(p);

			// create features
			List<ObservableFeature> fList = new ArrayList<ObservableFeature>();
			for (int i = 0; i < noColumns; i++)
			{
				ObservableFeature f = new ObservableFeature();
				f.setIdentifier("f" + i + "test");
				f.setName("test feature "+i);
				fList.add(f);
			}
			db.add(fList);

			// add features to protocol
			List<Integer> fIds = new ArrayList<Integer>();
			for (ObservableFeature f : fList)
			{
				fIds.add(f.getId());
			}
			p.setFeatures_Id(fIds);
			db.update(p);

			// create targets
			List<Individual> iList = new ArrayList<Individual>();
			for (int i = 0; i < noRows; i++)
			{
				Individual f = new Individual();
				f.setIdentifier("i" + i + "test");
				f.setName("feature "+i);
				iList.add(f);
			}
			db.add(iList);

			// create a data set
			DataSet ds = new DataSet();
			ds.setIdentifier("ds1");
			ds.setName("test data set");
			ds.setProtocolUsed(p.getId());
			db.add(ds);

			// get values
			List<ObservedValue> vList = new ArrayList<ObservedValue>();
			for (Individual i : iList)
			{
				ObservationSet os = new ObservationSet();
				os.setTarget_Id(i.getId());
				os.setPartOfDataSet(ds.getId());

				db.add(os);

				for (ObservableFeature of : fList)
				{
					ObservedValue v = new ObservedValue();
					v.setFeature(of);
					v.setObservationSet(os.getId());
					v.setValue(Double.toString(Math.random()));
					vList.add(v);
				}
			}
			db.add(vList);

			db.commitTx();
		}
		catch (Exception e)
		{
			db.rollbackTx();
		}

	}
}
