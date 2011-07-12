package org.molgenis.matrix;

import java.io.File;
import java.io.FileNotFoundException;

import org.molgenis.matrix.convertors.StringConvertor;

public class CsvStringMatrix extends CsvMemoryMatrix<String, String, String>
{
	public CsvStringMatrix(Matrix<String,String,String> matrix) throws MatrixException
	{
		super(new StringConvertor(),
				new StringConvertor(),
				new StringConvertor(), matrix);
	}

	public CsvStringMatrix(File f) throws FileNotFoundException,
			MatrixException
	{
		super(new StringConvertor(), new StringConvertor(),
				new StringConvertor(), f);
	}

}
