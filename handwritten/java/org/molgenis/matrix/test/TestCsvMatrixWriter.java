package org.molgenis.matrix.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.molgenis.matrix.CsvMatrix;
import org.molgenis.matrix.CsvMatrixWriter;
import org.molgenis.matrix.Matrix;
import org.molgenis.matrix.MatrixException;

/**
 * Write a csv matrix and compare the results agains an in-memory matrix.
 */
public class TestCsvMatrixWriter extends TestMatrixPerformance
{
	List<String> uniqueNames = new ArrayList<String>();
	Logger logger = Logger.getLogger(getClass().getSimpleName());

	/**
	 * WARNING: running this test will empty the contents of your selected
	 * database and possibly overwrite matrix backend data! Consider carefully
	 * before running!
	 * 
	 * @param matrixDimension1
	 * @param matrixDimension2
	 * @param maxTextLength
	 * @param fixedTextLength
	 * @param sparse
	 * @param runRegressionTests
	 * @param runPerformanceTests
	 * @throws Exception
	 */
	public TestCsvMatrixWriter(TestMatrixParams params)
			throws Exception
	{
		super(params);
	}
	
	@Test
	public void testCompareReadAndWrite() throws Exception
	{
		//get file handle
		File file = new File( (params.dir != null ? params.dir + File.separatorChar : "")+"testfilematrix"+UUID.randomUUID()+".csv");
		if(file.exists()) file.delete();
		
		//first write the memory matrix to the file
		CsvMatrixWriter writer = new CsvMatrixWriter(file);
		
		logger.info("Generating CSV file: "+file);
		long storeTimerStart = System.currentTimeMillis();
		long start = System.currentTimeMillis();

		long storeTimerStop = System.currentTimeMillis();
		long storeTime = storeTimerStop - storeTimerStart;
		int elems = ((params.rowCount * params.colCount) * 2);
		int elemsec = (int) ((elems) / (storeTime / 1000.0));
		
		writer.write(this.matrix);
		
		//this.performanceResults.put("Write", elemsec);
		
		logger.info(" -> Wrote " + elems + " elements in " + (storeTime / 1000.0) + " seconds (" + elemsec
				+ " elem/sec)");
		
		//then read and compare
		CsvMatrix csvMatrix = new CsvMatrix(params.valueType, file);
		String error = checkForErrors(matrix,csvMatrix);
		if(error != null)
		{
			throw new Exception(error);
		}
	
		
		logger.info("matrices are equal");
		
		//copy the csvMatrix to matrix to test other tests
		this.matrix = csvMatrix;
	}

	private String checkForErrors(Matrix matrix, CsvMatrix csvMatrix) throws MatrixException
	{
		String error = null;
		//colcount
		if(matrix.getColCount() != csvMatrix.getColCount())
		{
			error = "compare: different colCount: "+matrix.getColCount()+" vs. "+csvMatrix.getColCount();
		}
		//rowcount
		if(matrix.getRowCount() != csvMatrix.getRowCount())
		{
			error = "compare: different rowCount: "+matrix.getRowCount()+" vs. "+csvMatrix.getRowCount();
		}
		if(matrix.getColCount() == 0)
		{
			logger.warn("matrix doesn't contain values");
		}
		//compare type
		if(!matrix.getValueType().equals(csvMatrix.getValueType()))
		{
			error = "compare: different valuetype: "+matrix.getValueType()+" vs. "+csvMatrix.getValueType();
		}
		//conames
		if(!matrix.getRowNames().equals(csvMatrix.getRowNames()))
		{
			error = "compare: different rowNames";
		}
		//colnames
		if(!matrix.getColNames().equals(csvMatrix.getColNames()))
		{
			error = "compare: different colNames";
		}
		//values
		for(int row = 0; row < matrix.getRowCount(); row ++)
		{
			for(int col = 0; col < matrix.getColCount(); col ++)
			{
				Object value = matrix.getValue(row, col);
				Object otherValue = csvMatrix.getValue(row, col);
				if( (value == null && otherValue != null) || (otherValue == null && value != null) ) 
				{
					error = "compare: different getValue("+row+","+col+") -> ("+value+","+otherValue+")";
					return error;
				}
				if(value != null && !value.equals(otherValue) )
				{
					error = "compare: different getValue("+row+","+col+") -> ("+value+","+otherValue+")";
					return error;
				}
			}
		}
		
		return error;
	}		
	
}
