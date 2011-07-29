package org.molgenis.matrix.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.molgenis.Molgenis;
import org.molgenis.framework.db.Database;
import org.molgenis.matrix.PhenoMemoryMatrix;
import org.molgenis.matrix.StringMemoryMatrix;
import org.molgenis.matrix.TargetFeatureMemoryMatrix;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.cmdline.CmdLineException;

import app.DatabaseFactory;

//import app.JDBCDatabase;

public class TestPhenoMatrix
{
	Logger logger = Logger.getLogger(TestPhenoMatrix.class);
	static final String PROPERTIES_FILE = "org/molgenis/sandbox/sandbox.properties";

	@BeforeClass
	public static void setUp() throws FileNotFoundException, SQLException, IOException, CmdLineException, Exception
	{
		// clean database
		new Molgenis(PROPERTIES_FILE).updateDb(true);
	}

	@Test
	public void testPhenoMatrixFromDatabase() throws SQLException,
			CmdLineException, Exception
	{
		Database db = DatabaseFactory.create(PROPERTIES_FILE);

		for (Investigation i : db.find(Investigation.class))
			System.out.println(i);

		int nRows = 10;
		int nCols = 10;

		// create list of features
		List<ObservableFeature> fList = new ArrayList<ObservableFeature>();
		for (int i = 0; i < nRows; i++)
		{
			ObservableFeature f = new ObservableFeature();
			f.setName("feature" + i);
			fList.add(f);
		}

		// create list of targets
		List<ObservationTarget> tList = new ArrayList<ObservationTarget>();
		for (int i = 0; i < nCols; i++)
		{
			ObservationTarget t = new ObservationTarget();
			t.setName("target" + i);
			tList.add(t);
		}

		// add to database to get ids
		db.add(fList);
		db.add(tList);

		// create list of values
		int rowIndex = 0;
		int colIndex = 0;
		List<ObservedValue> vList = new ArrayList<ObservedValue>();
		for (ObservationTarget t : tList)
		{
			rowIndex++;
			colIndex = 0;
			for (ObservableFeature f : fList)
			{
				colIndex++;

				ObservedValue v = new ObservedValue();
				v.setFeature(f.getId());
				v.setTarget(t.getId());
				v.setValue("value" + rowIndex + "." + colIndex);

				vList.add(v);
			}
		}

		// put in database
		db.add(vList);

		logger
				.info("DATA LOADED, now starting rendering and printing of matrix");

		// create the matrix
		PhenoMemoryMatrix<ObservationTarget, ObservableFeature> m = new PhenoMemoryMatrix<ObservationTarget, ObservableFeature>(
				tList, fList, db);
		System.out.println(m.toString());

		logger.info("DONE, now removing all temporary records");

		// remove from database
		db.remove(vList);
		db.remove(fList);
		db.remove(tList);
	}

	@Test
	public void testPhenoMatrixFromStringMatrix() throws SQLException,
			CmdLineException, Exception
	{
		Database db = DatabaseFactory.create(PROPERTIES_FILE);

		// create a StringMemoryMatrix
		List<String> features = Arrays.asList(new String[]
		{ "f1", "f2", "f3", "f4", "f5" });
		List<String> targets = Arrays.asList(new String[]
		{ "t1", "t2", "t3" });
		StringMemoryMatrix m = new StringMemoryMatrix(targets, features);
		for (String t : targets)
		{
			for (String f : features)
			{
				m.setValue(t, f, t + f);
			}
		}

		// create a PhenoMatrix from it
		TargetFeatureMemoryMatrix m2 = new TargetFeatureMemoryMatrix(m);

		// save PhenoMatrix to database (assuming empty database)
		m2.store(db, Database.DatabaseAction.ADD_IGNORE_EXISTING);

		// retrieve as new
		TargetFeatureMemoryMatrix m3 = new TargetFeatureMemoryMatrix(db);
		System.out.println(m3.toString());

		// check
		// Assert.assertEquals("f1", m3.getRowNames().get(0).getName());

		// destroy the pheno matrix
		// this only works if now other observedvalue have been linked to these
		// targets (so in practice not very usefull??)
		m2.store(db, Database.DatabaseAction.REMOVE_IGNORE_MISSING);

		Assert.assertEquals(0, db.count(ObservationTarget.class));

	}
}
