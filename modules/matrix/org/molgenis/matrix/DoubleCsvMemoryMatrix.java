package org.molgenis.matrix;

import java.io.File;
import java.io.FileNotFoundException;

import org.molgenis.matrix.convertors.DoubleConvertor;
import org.molgenis.matrix.convertors.StringConvertor;

public class DoubleCsvMemoryMatrix extends CsvMemoryMatrix<String, String, Double>
{
	public DoubleCsvMemoryMatrix(Matrix<String,String,Double> matrix) throws MatrixException
	{
		super(new StringConvertor(),
				new StringConvertor(),
				new DoubleConvertor(), matrix, null);
	}
	
	public DoubleCsvMemoryMatrix(Matrix<String,String,Double> matrix, File file) throws MatrixException
	{
		super(new StringConvertor(),
				new StringConvertor(),
				new DoubleConvertor(), matrix, file);
	}

	public DoubleCsvMemoryMatrix(File f) throws FileNotFoundException,
			MatrixException
	{
		super(new StringConvertor(), new StringConvertor(),
				new DoubleConvertor(), f);
	}

}
