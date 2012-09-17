package org.molgenis.matrix;

import java.util.List;

public class StringMemoryMatrix extends MemoryMatrix<String, String, String>
		implements StringMatrix
{
	public StringMemoryMatrix(List<String> rowNames, List<String> colNames)
			throws MatrixException
	{
		super(rowNames, colNames, String.class);
	}

	public StringMemoryMatrix(List<String> rowNames, List<String> colNames, String[][] values)
			throws MatrixException
	{
		super(rowNames,colNames,values);
	}
}
