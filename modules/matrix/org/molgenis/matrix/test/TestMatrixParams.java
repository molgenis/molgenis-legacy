package org.molgenis.matrix.test;

/**
 * This class holds the parameters to generate matrix tests with.
 * 
 * @author mswertz, jvelde
 */
public class TestMatrixParams {
	
	public TestMatrixParams(int matrixDimension1, int matrixDimension2, int maxTextLength,
			boolean fixedTextLength, boolean sparse,
			boolean runRegressionTests, boolean runPerformanceTests,
			boolean skipPerElement) {
		this.source = "Memory";
		this.rowCount = matrixDimension1;
		this.colCount = matrixDimension2;
		this.maxTextLength = maxTextLength;
		this.fixedTextLength = fixedTextLength;
		this.sparse = sparse;
		this.runRegressionTests = runRegressionTests;
		this.runPerformanceTests = runPerformanceTests;
		this.skipPerElement = skipPerElement;
	}

	//type of matrix
	public String source = "Memory";
	//working directory for tests
	public String dir = System.getProperty("java.io.tmpdir");
	//dimensions of matrix
	public Class valueType = Double.class;
	public int rowCount = 10;
	public int colCount = 10;
	public int maxTextLength = 10;
	public boolean fixedTextLength = false;
	public boolean sparse = false;
	public boolean runRegressionTests = false;
	public boolean runPerformanceTests = true;
	public boolean skipPerElement = false;
	public boolean fileWrite = false;
	public boolean simpleOutput = true;
	
	public String toString()
	{
		return "###############################\n"+
		"TestMatrix starting with settings:\n"+
		"* source              = " + this.source+"\n"+
		"* valutype            = " + this.valueType+"\n"+
		"* rowCount            = " + this.rowCount+"\n"+
		"* colCount            = " + this.colCount+"\n"+
		"* maxTextLength       = " + this.maxTextLength+"\n"+
		"* fixedTextLength     = " + this.fixedTextLength+"\n"+
		"* sparse              = " + this.sparse+"\n"+
		"* runRegressionTests  = " + this.runRegressionTests+"\n"+
		"* runPerformanceTests = " + this.runPerformanceTests+"\n"+
		"* skipPerElement      = " + this.skipPerElement+"\n"+
		"* fileWrite           = " + this.fileWrite+"\n"+
		"###############################"+"\n";
	}
}