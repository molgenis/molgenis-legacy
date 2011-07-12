package org.molgenis.matrix;

import java.io.File;
import java.io.FileNotFoundException;

import org.molgenis.matrix.convertors.StringConvertor;

public class StringCsvMemoryMatrix extends CsvMemoryMatrix<String, String, String> implements StringMatrix
{
	public StringCsvMemoryMatrix(Matrix<String, String, String> matrix, File f)
			throws MatrixException
	{
		super(new StringConvertor(), new StringConvertor(),
				new StringConvertor(), matrix, f);
	}

	public StringCsvMemoryMatrix(File f) throws FileNotFoundException,
			MatrixException
	{
		super(new StringConvertor(), new StringConvertor(),
				new StringConvertor(), f);
	}

}
