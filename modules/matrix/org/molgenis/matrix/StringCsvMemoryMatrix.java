package org.molgenis.matrix;

import java.io.FileNotFoundException;

import org.molgenis.matrix.convertors.StringConvertor;
import org.molgenis.util.CsvReader;

public class StringCsvMemoryMatrix extends CsvMemoryMatrix<String, String, String> implements StringMatrix
{
	public StringCsvMemoryMatrix(CsvReader reader) throws FileNotFoundException, MatrixException
	{
		super(new StringConvertor(), new StringConvertor(),
				new StringConvertor(), reader);
	}

}
