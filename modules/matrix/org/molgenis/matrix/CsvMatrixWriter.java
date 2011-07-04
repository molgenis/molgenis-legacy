package org.molgenis.matrix;

import java.io.File;
import java.io.IOException;

import org.molgenis.util.CsvFileWriter;
import org.molgenis.util.CsvWriter;

public class CsvMatrixWriter implements MatrixWriter
{
	private CsvWriter writer;
	
	public CsvMatrixWriter(CsvWriter writer)
	{
		this.writer = writer;
	}

	public CsvMatrixWriter(File f) throws IOException
	{
		this(new CsvFileWriter(f));
	}
	
	@Override
	public void write(Matrix<?> matrix) throws MatrixException
	{	
		//NB this only works if names are unique!!!
		//set headers
		writer.setHeaders(matrix.getColNames());
		writer.writeHeader();
		for(String rowName: matrix.getRowNames())
		{
			writer.writeValue(rowName);
			for(Object o: matrix.getRowByName(rowName))
			{
				writer.writeSeparator();
				writer.writeValue(o);
			}
			writer.writeEndOfLine();
		}
		
		writer.close();
			
	}

}
