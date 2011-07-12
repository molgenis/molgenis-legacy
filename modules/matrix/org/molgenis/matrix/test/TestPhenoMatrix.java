package org.molgenis.matrix.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.ObservedValueMemoryMatrix;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;

import app.JDBCDatabase;

public class TestPhenoMatrix
{
	Logger logger = Logger.getLogger(TestPhenoMatrix.class);
	@Test
	public void test1() throws FileNotFoundException, IOException,
			DatabaseException, MatrixException, ParseException
	{
		JDBCDatabase db = new JDBCDatabase(
				"org/molgenis/sandbox/sandbox.properties");

		for (Investigation i : db.find(Investigation.class))
			System.out.println(i);

		int nRows = 100;
		int nCols = 100;

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
		
		//add to database to get ids
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
		
		logger.info("DATA LOADED, now starting rendering and printing of matrix");

		// create the matrix
		ObservedValueMemoryMatrix m = new ObservedValueMemoryMatrix<ObservationTarget,ObservableFeature>(db,tList,fList);
		System.out.println(m.toString());
		
		logger.info("DONE, now removing all temporary records");

		// remove from database
		db.remove(vList);
		db.remove(fList);
		db.remove(tList);

	}
}
