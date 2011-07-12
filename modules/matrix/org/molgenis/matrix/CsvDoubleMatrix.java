package org.molgenis.matrix;

import java.io.File;
import java.io.FileNotFoundException;

import org.molgenis.matrix.convertors.DoubleConvertor;
import org.molgenis.matrix.convertors.StringConvertor;

public class CsvDoubleMatrix extends CsvMemoryMatrix<String, String, Double>
{
	public CsvDoubleMatrix(Matrix<String,String,Double> matrix) throws MatrixException
	{
		super(new StringConvertor(),
				new StringConvertor(),
				new DoubleConvertor(), matrix);
	}

	public CsvDoubleMatrix(File f) throws FileNotFoundException,
			MatrixException
	{
		super(new StringConvertor(), new StringConvertor(),
				new DoubleConvertor(), f);
	}

}
