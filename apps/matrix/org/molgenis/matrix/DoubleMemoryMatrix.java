package org.molgenis.matrix;

import java.util.List;

public class DoubleMemoryMatrix extends MemoryMatrix<String, String, Double>
		implements DoubleMatrix
{
	public DoubleMemoryMatrix(List<String> rowNames, List<String> colNames)
			throws MatrixException
	{
		super(rowNames, colNames, Double.class);
	}

	public DoubleMemoryMatrix(List<String> rowNames, List<String> colNames,
			Double[][] values) throws MatrixException
	{
		super(rowNames, colNames, values);
	}
}
