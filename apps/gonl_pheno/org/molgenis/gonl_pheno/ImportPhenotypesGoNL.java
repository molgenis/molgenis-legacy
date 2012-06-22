package org.molgenis.gonl_pheno;

import java.io.File;
import java.io.IOException;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.organization.Investigation;
import org.molgenis.util.ExcelReader;
import org.molgenis.util.Tuple;
import org.molgenis.util.TupleReader;

import app.DatabaseFactory;

public class ImportPhenotypesGoNL
{

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		File input = new File("/Users/mswertz/Dropbox/BBRMI-NL/pheno/phenotypes merged.xls");

		// parse sheet 0:
		TupleReader s = new ExcelReader(input, "samples");

		Investigation gonl = new Investigation();
		gonl.setName("gonl");

		DataSet d = new DataSet();

		for (Tuple i : s)
		{
			String id = i.getString("Sample").toUpperCase();
			// System.out.println(i);
			d.addPanel(i.getString("Biobank"));
			d.addIndividual(id);

			for (String meas : i.getFields())
			{
				d.addValue(d.individuals.get(id), meas, i.getString(meas));
			}
		}

		// parse sheet 1:
		TupleReader g = new ExcelReader(input, "G");

		for (Tuple i : g)
		{
			String id = i.getString("GoNLnr").toUpperCase();

			// verify individual
			if (d.individuals.get(id) == null)
			{
				System.err.println("Id " + id + " unknown");
			}

			// load the values, except GoNLnr
			for (String meas : i.getFields())
			{
				d.addValue(d.individuals.get(id), meas, i.getString(meas));
			}
		}

		// parse sheet R
		TupleReader r = new ExcelReader(input, "R");

		for (Tuple i : r)
		{
			String id = i.getString("GoNLnr").toUpperCase();

			// verify individual
			if (d.individuals.get(id) == null)
			{
				System.err.println("Id " + id + " unknown");
			}
			else
			{
				// load the values, except GoNLnr
				for (String meas : i.getFields())
				{
					d.addValue(d.individuals.get(id), meas, i.getString(meas));
				}
			}
		}

		// parse sheet a
		TupleReader a = new ExcelReader(input, "A");

		for (Tuple i : a)
		{
			String id = i.getString("GoNLnr").toUpperCase();

			// verify individual
			if (d.individuals.get(id) == null)
			{
				System.err.println("Id " + id + " unknown");
			}

			// load the values, except GoNLnr
			for (String meas : i.getFields())
			{
				d.addValue(d.individuals.get(id), meas, i.getString(meas));
			}
		}
		
		// parse sheet a
		TupleReader l = new ExcelReader(input, "L");

		for (Tuple i : l)
		{
			String id = i.getString("GoNLnr").toUpperCase();

			// verify individual
			if (d.individuals.get(id) == null)
			{
				System.err.println("Id " + id + " unknown");
			}

			// load the values, except GoNLnr
			for (String meas : i.getFields())
			{
				d.addValue(d.individuals.get(id), meas, i.getString(meas));
			}
		}

		// load
		Database db = DatabaseFactory.create();
		//
		db.loadExampleData(d);

	}
}
