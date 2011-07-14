package org.molgenis.matrix;

import org.molgenis.matrix.convertors.DoubleConvertor;
import org.molgenis.matrix.convertors.StringConvertor;
import org.molgenis.util.CsvReader;

public class DoubleCsvMemoryMatrix extends CsvMemoryMatrix<String, String, Double>
{
	public DoubleCsvMemoryMatrix(Matrix<String,String,Double> matrix) throws MatrixException
	{
		super(new StringConvertor(),
				new StringConvertor(),
				new DoubleConvertor(), null);
	}
	
	public DoubleCsvMemoryMatrix(Matrix<String,String,Double> matrix, CsvReader reader) throws MatrixException
	{
		super(new StringConvertor(),
				new StringConvertor(),
				new DoubleConvertor(), matrix, reader);
	}
}
