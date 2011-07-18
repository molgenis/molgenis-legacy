package matrix.test.implementations.general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import matrix.test.implementations.binary.TestBinMatrix;
import matrix.test.implementations.csv.TestFileMatrix;
import matrix.test.implementations.database.TestDatabaseMatrix;
import matrix.test.implementations.memory.TestMemoryMatrix;

import org.molgenis.framework.db.Database;


public class TestMatrix {
	
	private List<HashMap<String, Integer>> binaryPerformanceResults = new ArrayList<HashMap<String, Integer>>();
	private List<HashMap<String, Integer>> databasePerformanceResults = new ArrayList<HashMap<String, Integer>>();
	private List<HashMap<String, Integer>> filePerformanceResults = new ArrayList<HashMap<String, Integer>>();
	private List<HashMap<String, Integer>> memoryPerformanceResults = new ArrayList<HashMap<String, Integer>>();

	private int matrixDimension1;
	private int matrixDimension2;
	private int maxTextLength;
	private boolean fixedTextLength;
	private boolean sparse;
	private boolean runRegressionTests;
	private boolean runPerformanceTests;
	private boolean skipPerElement;
	
	private Database db;

	public TestMatrix(Database db, Params params){
		this.db = db;
		this.matrixDimension1 = params.matrixDimension1;
		this.matrixDimension2 = params.matrixDimension2;
		this.maxTextLength = params.maxTextLength;
		this.fixedTextLength = params.fixedTextLength;
		this.sparse = params.sparse;
		this.runRegressionTests = params.runRegressionTests;
		this.runPerformanceTests =params.runPerformanceTests;
		this.skipPerElement = params.skipPerElement;
	}
	
	public TestMatrix(Database db, int matrixDimension1, int matrixDimension2,
			int maxTextLength, boolean fixedTextLength, boolean sparse,
			boolean runRegressionTests, boolean runPerformanceTests,
			boolean skipPerElement) {
		this.db = db;
		this.matrixDimension1 = matrixDimension1;
		this.matrixDimension2 = matrixDimension2;
		this.maxTextLength = maxTextLength;
		this.fixedTextLength = fixedTextLength;
		this.sparse = sparse;
		this.runRegressionTests = runRegressionTests;
		this.runPerformanceTests = runPerformanceTests;
		this.skipPerElement = skipPerElement;
	}

	public TestMatrix(Database db, boolean regularFullTest) {
		if(!regularFullTest){
			this.db = db;
		// default high-performance-only test settings, dont edit
			this.matrixDimension1 = 500; // 500
			this.matrixDimension2 = 500; // 500
			this.maxTextLength = 2; // 2
			this.fixedTextLength = true; // true
			this.sparse = false; // false
			this.runRegressionTests = false; // false
			this.runPerformanceTests = true; // true
			this.skipPerElement = true; // true
		}else{
			// default settings for a general regression test, dont edit
			this.matrixDimension1 = 10; // 10
			this.matrixDimension2 = 20; // 20
			this.maxTextLength = 5; // 5
			this.fixedTextLength = false; // false
			this.sparse = true; // true
			this.runRegressionTests = true; // true
			this.runPerformanceTests = false; // false
			this.skipPerElement = false; // false
		}
	}
	
	public void runBinary(int times) throws Exception {
		for(int i = 0; i < times; i++){
			this.runBinary();
		}
	}
	
	public void runDatabase(int times) throws Exception {
		for(int i = 0; i < times; i++){
			this.runDatabase();
		}
	}
	
	public void runFile(int times) throws Exception {
		for(int i = 0; i < times; i++){
			this.runFile();
		}
	}
	
	public void runMemory(int times) throws Exception {
		for(int i = 0; i < times; i++){
			this.runMemory();
		}
	}
	
	public void runBinary() throws Exception {
		HashMap<String, Integer> res = new TestBinMatrix(db, matrixDimension1, matrixDimension2, maxTextLength,
				fixedTextLength, sparse, runRegressionTests,
				runPerformanceTests, skipPerElement).getPerformanceResults();
		binaryPerformanceResults.add(res);
	}

	public void runDatabase() throws Exception {
		HashMap<String, Integer> res = new TestDatabaseMatrix(db, matrixDimension1, matrixDimension2,
				maxTextLength, fixedTextLength, sparse, runRegressionTests,
				runPerformanceTests, skipPerElement).getPerformanceResults();
		databasePerformanceResults.add(res);
	}
	
	public void runFile() throws Exception {
		HashMap<String, Integer> res = new TestFileMatrix(db, matrixDimension1, matrixDimension2, maxTextLength,
				fixedTextLength, sparse, runRegressionTests,
				runPerformanceTests, skipPerElement).getPerformanceResults();
		filePerformanceResults.add(res);
	}
	
	public void runMemory() throws Exception {
		HashMap<String, Integer> res = new TestMemoryMatrix(db, matrixDimension1, matrixDimension2, maxTextLength,
				fixedTextLength, sparse, runRegressionTests,
				runPerformanceTests, skipPerElement).getPerformanceResults();
		memoryPerformanceResults.add(res);
	}

	public List<HashMap<String, Integer>> getBinaryPerformanceResults()
	{
		return binaryPerformanceResults;
	}

	public List<HashMap<String, Integer>> getDatabasePerformanceResults()
	{
		return databasePerformanceResults;
	}

	public List<HashMap<String, Integer>> getFilePerformanceResults()
	{
		return filePerformanceResults;
	}
	
	public List<HashMap<String, Integer>> getMemoryPerformanceResults()
	{
		return memoryPerformanceResults;
	}

	public int getMatrixDimension1()
	{
		return matrixDimension1;
	}

	public int getMatrixDimension2()
	{
		return matrixDimension2;
	}

	public int getMaxTextLength()
	{
		return maxTextLength;
	}

	public boolean isFixedTextLength()
	{
		return fixedTextLength;
	}

	public boolean isSparse()
	{
		return sparse;
	}

	public boolean isRunRegressionTests()
	{
		return runRegressionTests;
	}

	public boolean isRunPerformanceTests()
	{
		return runPerformanceTests;
	}

	public boolean isSkipPerElement()
	{
		return skipPerElement;
	}
	
	
}
