package org.molgenis.matrix.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.matrix.CsvMatrix;
import org.molgenis.matrix.CsvMatrixWriter;
import org.molgenis.matrix.Matrix;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.MemoryMatrix;
import org.molgenis.matrix.SimpleObservedValueMatrix;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvStringReader;
import org.molgenis.util.CsvWriter;

import app.JDBCDatabase;

public class TestMatrix1
{
	Logger logger = Logger.getLogger(TestMatrix1.class);

	public static void main(String[] args) throws MatrixException,
			FileNotFoundException, IOException, DatabaseException
	{
		TestMatrix1 tm = new TestMatrix1();
		tm.testSimpleMatrix();
		tm.testErrors();
		tm.testCsvMatrix();
		tm.testDatabaseMatrix();
	}

	public void testCsvMatrix() throws MatrixException
	{
		System.out.println("Test CSV reader from string");
		String csv = "\tcol1\tcol2\nrow1\t11\t12\nrow2\t21\t22";

		CsvReader reader = new CsvStringReader(csv);

		Matrix<String> m = new CsvMatrix<String>(String.class, reader);

		logger.info(toString(m));

		StringWriter string = new StringWriter();
		CsvWriter writer = new CsvWriter(new PrintWriter(string));
		CsvMatrixWriter csvWriter = new CsvMatrixWriter(writer);
		csvWriter.write(m);

		logger.info("Written csv matrix to string using CsvMatrixWriter:\n"
				+ string);

	}

	public void testDatabaseMatrix() throws MatrixException
	{
		Database db = null;
		try
		{
			db = new JDBCDatabase(
					"handwritten/apps/org/molgenis/compute/compute.properties");
			logger.info("database created");

			db.beginTx();
			// List<ComputeFeature> features = db.find(ComputeFeature.class);
			// List<Panel> targets = db.find(Panel.class);
			
			ObservableFeature tmpFeature;
			List<ObservableFeature> features = new ArrayList<ObservableFeature>();
			for (int i = 0; i < 100; i++)
			{
				tmpFeature = new ObservableFeature();
				tmpFeature.setName("feat" + i);
				features.add(tmpFeature);
			}
			
			Individual tmpIndividual;
			List<Individual> targets = new ArrayList<Individual>();
			for (int i = 0; i < 100; i++)
			{
				tmpIndividual = new Individual();
				tmpIndividual.setName("indiv" + i);
				targets.add(tmpIndividual);
			}
			db.add(features);
			db.add(targets);

			List<ObservedValue> values = new ArrayList<ObservedValue>();

			for (ObservableFeature f : features)
			{
				for (Individual t : targets)
				{
					ObservedValue v = new ObservedValue();
					v.setFeature(f.getId());
					v.setTarget(t.getId());
					v
							.setValue("value:" + f.getId() + ","
									+ t.getId());

					values.add(v);
				}
			}
			db.add(values);

			logger.info("example data loaded");

			Matrix m = new SimpleObservedValueMatrix(db, targets, features);

			logger.info("matrix loaded from database");

			logger.info(toString(m));

			db.rollbackTx();
		}
		catch (Exception e)
		{
			try
			{
				db.rollbackTx();
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
	}

	public void testSimpleMatrix() throws MatrixException
	{
		List<String> colNames = Arrays.asList(new String[]
		{ "row1", "row2" });
		List<String> rowNames = Arrays.asList(new String[]
		{ "col1", "col2", "col3" });

		logger.info("Testing String matrix:\n");
		String[][] values = new String[][]
		{
		{ "row1,col1", "row1,col2", "row1,col3" },
		{ "row2,col1", "row2,col2", "row2,col3" } };
		Matrix<String> m = new MemoryMatrix<String>(colNames, rowNames, values);
		logger.info(toString(m));

		logger.info("\nTesting transposed matrix:\n");
		m.transpose();
		logger.info(toString(m));

		logger.info("Testing double matrix:\n");
		Double[][] doubleValues = new Double[][]
		{
		{ 1.1, 1.2, 1.3 },
		{ 2.1, 2.2, 2.3 } };
		Matrix<Double> md = new MemoryMatrix<Double>(colNames, rowNames,
				doubleValues);
		logger.info(toString(md));

		logger.info("\nTesting transposed double matrix:\n");
		md.transpose();
		logger.info(toString(md));

	}

	public void testErrors()
	{
		logger.info("\nTesting error handling:\n");

		String[][] values = new String[][]
		{
		{ "row1,col1", "row1,col2" },
		{ "row2,col1", "row2,col2" } };

		List<String> colNames = Arrays.asList(new String[]
		{ "row1", "row2" });
		List<String> rowNames = Arrays.asList(new String[]
		{ "col1", "col2" });

		String[][] valuesError = new String[][]
		{
		{ "row1,col1", "row1,col2" },
		{ "row2,col1", "row2,col2", "row1,col3" } };

		List<String> colNamesError = Arrays.asList(new String[]
		{ "row1", "row2", "row3" });
		List<String> rowNamesError = Arrays.asList(new String[]
		{ "col1", "col2", "col3" });

		Matrix<String> m;

		try
		{
			m = new MemoryMatrix<String>(colNamesError, rowNames, values);
			logger.info("ERROR catching colnames error");
		}
		catch (MatrixException e)
		{
			logger.info("Succesfully caught error: " + e.getMessage());
		}

		try
		{
			m = new MemoryMatrix<String>(colNames, rowNamesError, values);
			logger.info("ERROR catching rownames error");
		}
		catch (MatrixException e)
		{
			logger.info("Succesfully caught error: " + e.getMessage());
		}

		try
		{
			m = new MemoryMatrix<String>(colNames, rowNames, valuesError);
			logger.info("ERROR catching values error");
		}
		catch (MatrixException e)
		{
			logger.info("Succesfully caught error: " + e.getMessage());
		}
	}

	private String toString(Matrix<? extends Object> m) throws MatrixException
	{
		// print headers
		String result = "\nMatrix: \n";
		List<String> colnames = m.getColNames();
		for (String colName : colnames)
		{
			result += "\t" + colName;
		}

		// print rows
		List<String> rownames = m.getRowNames();
		for (int i = 0; i < rownames.size(); i++)
		{
			result += "\n" + rownames.get(i);
			for (int j = 0; j < colnames.size(); j++)
			{
				if (m.getValue(i, j) instanceof ObservedValue)
				{
					result += "\tvalue:"
							+ ((ObservedValue) m.getValue(i, j)).getValue();
				}
				else
				{
					result += "\t" + m.getValue(i, j);
				}
			}
		}

		return result + "\n";
	}
}