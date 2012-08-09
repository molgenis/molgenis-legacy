//package matrix.test;
//
//import app.DatabaseFactory;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import matrix.component.ObservationMatrix;
//
//import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Logger;
//import org.molgenis.data.Data;
//import org.molgenis.data.TextDataElement;
//import org.molgenis.framework.db.Database;
//import org.molgenis.framework.db.DatabaseException;
//import org.molgenis.organization.Investigation;
//import org.molgenis.pheno.Individual;
//import org.molgenis.pheno.Measurement;
//import org.testng.Assert;
//import org.testng.annotations.AfterClass;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Test;
//
//public class TestObservationMatrix
//{
//	//data and metadata
//	private List<Measurement> features = new ArrayList<Measurement>();
//	private List<Individual> targets = new ArrayList<Individual>();
//	private List<TextDataElement> values = new ArrayList<TextDataElement>();
//	private Data data;
//	private Investigation investigation;
//
//	//the matrix
//	protected ObservationMatrix<Individual, Measurement, TextDataElement> matrix;
//	
//	//the logger
//	Logger logger = Logger.getLogger(this.getClass());
//
//	@BeforeClass
//	public void setUp() throws DatabaseException, FileNotFoundException, IOException
//	{
//		BasicConfigurator.configure();
//
//                Database db = DatabaseFactory.create("apps/xgap/org/molgenis/xgap/xqtlworkbench/xqtl.properties");
//		this.generateData(db);
//
//		matrix = new ObservationMatrix<Individual, Measurement, TextDataElement>(
//				db, data);
//	}
//
//	@Test
//	public void testRowHeaders() throws Exception
//	{
//		logger.debug("testRowHeaders");
//		//we expect feature index in reverse order
//		List<Individual> rowHeaders = matrix.getRowHeaders();
//		
//		Assert.assertEquals(rowHeaders.get(rowHeaders.size() - 1), targets.get(0));
//		Assert.assertEquals(rowHeaders.size(), targets.size());
//	}
//
//	@Test
//	public void testColHeaders() throws Exception
//	{
//		logger.debug("testColHeaders");
//		//we expect feature index in reverse order
//		List<Measurement> colHeaders = matrix.getColHeaders();
//		
//		Assert.assertEquals(colHeaders.size(), features.size());
//		Assert.assertEquals(colHeaders.get(colHeaders.size() - 1), features.get(0));
//
//	}
//
//	@Test
//	public void testRowIndices() throws Exception
//	{
//		logger.debug("testRowIndics");
//		//we expect feature index in reverse order
//		List<Integer> rowIndices = matrix.getRowIndices();
//		
//		Assert.assertEquals(rowIndices.size(), targets.size());
//		Assert.assertEquals(rowIndices.get(rowIndices.size() - 1), targets.get(0).getIdValue());
//
//	}
//
//	@Test
//	public void testColIndices() throws Exception
//	{
//		logger.debug("testColIndices");
//		
//		//we expect feature index in reverse order
//		List<Integer> colIndices = matrix.getColIndices();
//		
//		Assert.assertEquals(colIndices.get(colIndices.size() - 1), features.get(0).getIdValue());
//		Assert.assertEquals(colIndices.size(), features.size());
//	}
//
//	@Test
//	public void testValues() throws Exception
//	{
//		logger.debug("testValues: load data");
//		TextDataElement[][] valueMatrix = matrix.getValues();
//		logger.debug("testValues: verifying...");
//		for(Integer row = 0; row < matrix.getRowCount(); row++ )
//		{
//			for(Integer col = 0; col < matrix.getColCount(); col++)
//			{
//				TextDataElement e = valueMatrix[row][col];
//				Assert.assertEquals(e.getTargetIndex(), row);
//				Assert.assertEquals(e.getFeatureIndex(), col);
//				//Assert.assertEquals(e.getValue(), "val"+row+","+col);
//			}
//		}
//	}
//
//	@Test
//	public void testColCount() throws Exception
//	{
//		logger.debug("testColCount");
//		Assert.assertEquals(matrix.getColCount(), (Integer)features.size());
//	}
//
//	@Test
//	public void testRowCount() throws Exception
//	{
//		logger.debug("testRowCount");
//		Assert.assertEquals(matrix.getRowCount(), (Integer)targets.size());
//	}
//	
//	@AfterClass
//	public void tearDown() throws DatabaseException, FileNotFoundException, IOException
//	{
//		logger.debug("tearDown");
//		Database db = DatabaseFactory.create("apps/xgap/org/molgenis/xgap/xqtlworkbench/xqtl.properties");
//		
//		try
//		{
//			db.beginTx();
//			
//			db.remove(values);
//			db.remove(data);
//			db.remove(targets);
//			db.remove(features);
//			db.remove(investigation);
//			
//			db.commitTx();
//		}
//		catch(DatabaseException e)
//		{
//			db.rollbackTx();
//		}
//		logger.debug("done");
//	}
//
//	private void generateData(Database db) throws DatabaseException
//	{
//		logger.debug("generating test data");
//		int rowCount = 5000;
//		int colCount = 100;
//		try
//		{
//			db.beginTx();
//
//			// generate data
//			investigation = new Investigation();
//			investigation.setName("Test" + System.currentTimeMillis());
//			db.add(investigation);
//
//			// measurements
//			for (int i = 0; i < colCount; i++)
//			{
//				Measurement meas = new Measurement();
//				meas.setInvestigation(investigation.getId());
//				meas.setName("meas" + i);
//				features.add(meas);
//			}
//			db.add(features);
//
//			// individuals
//			for (int i = 0; i < rowCount; i++)
//			{
//				Individual inv = new Individual();
//				inv.setInvestigation(investigation.getId());
//				inv.setName("inv" + i);
//				targets.add(inv);
//			}
//
//			db.add(this.targets);
//
//			// data set
//			data = new Data();
//			data.setName("data" + System.currentTimeMillis());
//			data.setFeatureType(Measurement.class.getSimpleName());
//			data.setTargetType(Individual.class.getSimpleName());
//			data.setValueType("Text");
//			db.add(data);
//
//			// the matrix, for testing purpose in reverse order
//			int rowIndex = 0;
//			for (int row = targets.size() - 1; row >= 0; row--)
//			{
//				int colIndex = 0;
//				for (int col = features.size() - 1; col >= 0; col--)
//				{
//					TextDataElement td = new TextDataElement();
//					td.setData(data);
//					td.setFeature(features.get(col));
//					td.setTarget(targets.get(row));
//					td.setFeatureIndex(colIndex++);
//					td.setTargetIndex(rowIndex);
//					td.setValue("val"+row+","+col);
//					values.add(td);
//				}
//				rowIndex++;
//			}
//			db.add(values);
//
//			db.commitTx();
//		}
//		catch (DatabaseException e)
//		{
//			e.printStackTrace();
//			db.rollbackTx();
//			throw new RuntimeException("database problem");
//		}
//	}
//}
