package org.molgenis.matrix.component.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import app.JDBCDatabase;

public class TestSliceableObservationMatrix
{
	//SETTINGS!
	String properties = "apps/animaldb/org/molgenis/animaldb/animaldb.properties";
	
	
	//data and metadata
	private List<Measurement> features = new ArrayList<Measurement>();
	private List<Individual> targets = new ArrayList<Individual>();
	private List<ObservedValue> values = new ArrayList<ObservedValue>();
	private Investigation investigation;

	//the matrix
	private SliceablePhenoMatrix<Individual, Measurement> matrix;
	
	//the logger
	Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	@BeforeClass
	public void setUp() throws DatabaseException, FileNotFoundException, IOException
	{
		BasicConfigurator.configure();
		
		JDBCDatabase db = new JDBCDatabase(properties);

		this.generateData(db);

		matrix = new SliceablePhenoMatrix<Individual, Measurement>(
				db, Individual.class, Measurement.class);
		//different row and col limit to make sure we test different things
		matrix.setColLimit(5);
		
		logger.debug("++++++++++SETUP COMPLETE++++++++++++");
	}

	@Test
	public void testRowHeaders() throws Exception
	{
		matrix.reset();
		logger.debug("testRowHeaders");
		
		List<Individual> rowHeaders = matrix.getRowHeaders();
		
		Assert.assertEquals(rowHeaders.get(0).getIdValue(), targets.get(0).getIdValue());
		Assert.assertEquals(rowHeaders.size(), matrix.getRowLimit());
		
		//more the row offset
		//matrix.setRowOffset(10);
		//Assert.assertEquals(rowHeaders.get(0), targets.get(10));
	}

	@Test
	public void testColHeaders() throws Exception
	{
		matrix.reset();
		logger.debug("testColHeaders");
		//we expect feature index in reverse order
		List<Measurement> colHeaders = matrix.getColHeaders();
		
		Assert.assertEquals(colHeaders.size(), matrix.getColLimit());
		
		Assert.assertEquals(colHeaders.get(0), features.get(0));

	}

	@Test
	public void testRowIndices() throws Exception
	{
		matrix.reset();
		logger.debug("testRowIndics");
		
		//we expect feature index in reverse order
		List<Integer> rowIndices = matrix.getRowIndices();
		
		Assert.assertEquals(rowIndices.size(), matrix.getRowLimit());
		Assert.assertEquals(rowIndices.get(0), targets.get(0).getIdValue());

	}

	@Test
	public void testColIndices() throws Exception
	{
		matrix.reset();
		logger.debug("testColIndices");
		
		//we expect feature index in reverse order
		List<Integer> colIndices = matrix.getColIndices();
		
		Assert.assertEquals(colIndices.get(0), features.get(0).getIdValue());
		Assert.assertEquals(colIndices.size(), matrix.getColLimit());
	}

	@Test
	public void testValues() throws Exception
	{
		matrix.reset();
		logger.debug("testValues: load data");
		
		List<ObservedValue>[][] valueMatrix = matrix.getValues();
		logger.debug("testValues: verifying...");
		
		for(Integer row = 0; row < matrix.getRowLimit(); row++ )
		{
			for(Integer col = 0; col < matrix.getColLimit(); col++)
			{
				List<ObservedValue> e = valueMatrix[row][col];
				Assert.assertEquals(e.get(0).getValue(), this.values.get(row*matrix.getColCount() + col).getValue());
				//TODO 
				//Assert.assertEquals(e.get(0), col);
				//Assert.assertEquals(e.getValue(), "val"+row+","+col);
			}
		}
	}
	
	@Test
	public void testRowHeaderFilters() throws Exception
	{
		matrix.reset();
		logger.debug("testRowHeaderFilters");		
		
		//check for first one greater then 
		matrix.sliceByRowProperty(Individual.ID, Operator.GREATER, targets.get(5).getId());
		Assert.assertEquals(matrix.getRowHeaders().get(0).getIdValue(), targets.get(6).getId());
		
		//repeat for sliceByRowIndex (wich should work identical to above)
		matrix.reset();
		matrix.sliceByRowIndex(Operator.GREATER, targets.get(5).getId());
		Assert.assertEquals(matrix.getRowHeaders().get(0).getIdValue(), targets.get(6).getId());

		
		//reverse sort, now we should get last ID first
		matrix.sliceByRowProperty(Individual.ID, Operator.SORTDESC, null);
		Assert.assertEquals(matrix.getRowHeaders().get(0).getIdValue(), targets.get(targets.size() - 1).getIdValue());
		
	}
	
	@Test
	public void testColValuePropertyFilters() throws Exception
	{
		matrix.reset();
		logger.debug("testColValuePropertyFilters");		
		
		//filter on 5th column (values 'val4,4' and higher), we expect first result target to be row5
		matrix.sliceByColValues(features.get(5), Operator.GREATER, "val44");
		matrix.sortCol(features.get(5).getId(), ObservedValue.VALUE, Operator.SORTASC);
		Assert.assertEquals(matrix.getRowHeaders().get(0), targets.get(5));
		
		//repeate but now explicity property chosing
		matrix.reset();
		matrix.sliceByColValueProperty(features.get(5), ObservedValue.VALUE, Operator.GREATER, "val44");
		matrix.sortCol(features.get(5).getId(), ObservedValue.VALUE, Operator.SORTASC);
		Assert.assertEquals(matrix.getRowHeaders().get(0).getIdValue(), targets.get(5).getIdValue());
	}

	@Test
	public void testColCount() throws Exception
	{
		matrix.reset();
		logger.debug("testColCount");
		Assert.assertEquals(matrix.getColCount(), (Integer)features.size());
	}

	@Test
	public void testRowCount() throws Exception
	{
		matrix.reset();
		logger.debug("testRowCount");
		Assert.assertEquals(matrix.getRowCount(), (Integer)targets.size());
	}
	
	@Test
	public void testRowOffset() throws Exception
	{
		matrix.reset();
		logger.debug("testRowOffset");
		matrix.setRowOffset(10);
		Assert.assertEquals(matrix.getRowIndices().get(0), targets.get(10).getIdValue());	
		Assert.assertEquals(matrix.getRowHeaders().get(0), targets.get(10));	
	}
	
	@AfterClass
	public void tearDown() throws DatabaseException, FileNotFoundException, IOException
	{
		logger.debug("++++++++++TEST COMPLETE, REMOVING DATA ++++++++++++");
		JDBCDatabase db = new JDBCDatabase(properties);
		
		try
		{
			db.beginTx();
			
			db.remove(values);
			db.remove(targets);
			db.remove(features);
			db.remove(investigation);
			
			db.commitTx();
		}
		catch(DatabaseException e)
		{
			db.rollbackTx();
		}
		logger.debug("done");
	}

	private void generateData(Database db) throws DatabaseException
	{
		logger.debug("generating test data");
		int rowCount = 100;
		int colCount = 20;
		try
		{
			db.beginTx();

			// generate data
			investigation = new Investigation();
			investigation.setName("Test" + System.currentTimeMillis());
			db.add(investigation);

			// measurements
			for (int i = 0; i < colCount; i++)
			{
				Measurement meas = new Measurement();
				meas.setInvestigation(investigation.getId());
				meas.setName("meas" + i);
				meas.setOwns_Id(2);
				
				features.add(meas);
			}
			db.add(features);
			features = db.find(Measurement.class);

			// individuals
			for (int i = 0; i < rowCount; i++)
			{
				Individual inv = new Individual();
				inv.setInvestigation(investigation.getId());
				inv.setName("inv" + i);
				inv.setOwns_Id(2);
				targets.add(inv);
			}

			db.add(this.targets);
			targets = db.find(Individual.class);

			// the matrix, for testing purpose in reverse order
			int rowIndex = 0;
			for (int row = 0; row < targets.size(); row++)
			{
				int colIndex = 0;
				for (int col = 0; col < features.size(); col++)
				{
					ObservedValue td = new ObservedValue();
					td.setFeature(features.get(col));
					td.setTarget(targets.get(row));
					td.setValue("val"+row+","+col);
					
					values.add(td);
				}
				rowIndex++;
			}
			db.add(values);

			db.commitTx();
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
			db.rollbackTx();
			throw new RuntimeException("database problem");
		}
	}
}
