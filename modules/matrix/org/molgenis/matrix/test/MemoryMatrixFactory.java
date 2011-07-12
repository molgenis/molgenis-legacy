package org.molgenis.matrix.test;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.matrix.Matrix;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.MemoryMatrix;

public class MemoryMatrixFactory
{
	public static Matrix<String, String, Double> create(int rows, int cols)
			throws MatrixException
	{
		List<String> rowNames = new ArrayList<String>();
		for (int i = 0; i < rows; i++)
			rowNames.add("row" + i);

		List<String> colNames = new ArrayList<String>();
		for (int i = 0; i < cols; i++)
			colNames.add("col" + i);

		MemoryMatrix<String, String, Double> matrix = new MemoryMatrix<String, String, Double>(
				rowNames, colNames);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				matrix.setValue(i, j, new Double(i + "." + j));

		return matrix;

	}
}
