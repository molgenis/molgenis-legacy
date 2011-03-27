package matrix.test.implementations.general;
public class Params {
	public Params(int matrixDimension1, int matrixDimension2, int maxTextLength,
			boolean fixedTextLength, boolean sparse,
			boolean runRegressionTests, boolean runPerformanceTests,
			boolean skipPerElement) {
		this.matrixDimension1 = matrixDimension1;
		this.matrixDimension2 = matrixDimension2;
		this.maxTextLength = maxTextLength;
		this.fixedTextLength = fixedTextLength;
		this.sparse = sparse;
		this.runRegressionTests = runRegressionTests;
		this.runPerformanceTests = runPerformanceTests;
		this.skipPerElement = skipPerElement;
	}

	public int matrixDimension1;
	public int matrixDimension2;
	public int maxTextLength;
	public boolean fixedTextLength;
	public boolean sparse;
	public boolean runRegressionTests;
	public boolean runPerformanceTests;
	public boolean skipPerElement;
}