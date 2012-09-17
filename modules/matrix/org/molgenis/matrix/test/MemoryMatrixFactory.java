package org.molgenis.matrix.test;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.matrix.DoubleMemoryMatrix;
import org.molgenis.matrix.MatrixException;

public class MemoryMatrixFactory
{
	public static DoubleMemoryMatrix create(int rows, int cols)
			throws MatrixException
	{
		List<String> rowNames = new ArrayList<String>();
		for (int i = 0; i < rows; i++)
			rowNames.add("row" + i);

		List<String> colNames = new ArrayList<String>();
		for (int i = 0; i < cols; i++)
			colNames.add("col" + i);

		DoubleMemoryMatrix matrix = new DoubleMemoryMatrix(
				rowNames, colNames);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				matrix.setValue(i, j, new Double(i + "." + j));

		return matrix;

	}
}
