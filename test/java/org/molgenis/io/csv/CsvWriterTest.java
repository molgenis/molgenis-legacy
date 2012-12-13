package org.molgenis.io.csv;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import org.molgenis.io.processor.CellProcessor;
import org.molgenis.util.tuple.HeaderTuple;
import org.molgenis.util.tuple.Tuple;
import org.molgenis.util.tuple.ValueTuple;
import org.testng.annotations.Test;

public class CsvWriterTest
{
	@SuppressWarnings("resource")
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void CsvWriter()
	{
		new CsvWriter(null);
	}

	@Test
	public void addCellProcessor_header() throws IOException
	{
		CellProcessor processor = when(mock(CellProcessor.class).processHeader()).thenReturn(true).getMock();
		Tuple headerTuple = mock(Tuple.class);
		when(headerTuple.getNrCols()).thenReturn(2);
		when(headerTuple.getColNames()).thenReturn(Arrays.asList("col1", "col2").iterator());

		Tuple dataTuple = mock(Tuple.class);
		when(dataTuple.getNrCols()).thenReturn(2);
		when(dataTuple.get("col1")).thenReturn("val1");
		when(dataTuple.get("col2")).thenReturn("val2");

		CsvWriter csvWriter = new CsvWriter(new StringWriter());
		try
		{
			csvWriter.addCellProcessor(processor);
			csvWriter.writeColNames(headerTuple);
			csvWriter.write(dataTuple);
		}
		finally
		{
			csvWriter.close();
		}
		verify(processor).process("col1");
		verify(processor).process("col2");
	}

	@Test
	public void addCellProcessor_data() throws IOException
	{
		CellProcessor processor = when(mock(CellProcessor.class).processData()).thenReturn(true).getMock();

		Tuple dataTuple = mock(Tuple.class);
		when(dataTuple.getNrCols()).thenReturn(2);
		when(dataTuple.get(0)).thenReturn("val1");
		when(dataTuple.get(1)).thenReturn("val2");

		CsvWriter csvWriter = new CsvWriter(new StringWriter());
		try
		{
			csvWriter.addCellProcessor(processor);
			csvWriter.write(dataTuple);
		}
		finally
		{
			csvWriter.close();
		}
		verify(processor).process("val1");
		verify(processor).process("val2");
	}

	@Test
	public void write_Csv() throws IOException
	{
		StringWriter strWriter = new StringWriter();
		CsvWriter csvWriter = new CsvWriter(strWriter);
		try
		{
			csvWriter.writeColNames(new HeaderTuple(Arrays.asList("col1", "col2")));
			csvWriter.write(new ValueTuple(Arrays.asList("val1", "val2")));
			assertEquals(strWriter.toString(), "\"col1\",\"col2\"\n\"val1\",\"val2\"\n");
		}
		finally
		{
			csvWriter.close();
		}
	}

	@Test
	public void write_Tsv() throws IOException
	{
		StringWriter strWriter = new StringWriter();
		CsvWriter csvWriter = new CsvWriter(strWriter, '\t');
		try
		{
			csvWriter.writeColNames(new HeaderTuple(Arrays.asList("col1", "col2")));
			csvWriter.write(new ValueTuple(Arrays.asList("val1", "val2")));
			assertEquals(strWriter.toString(), "\"col1\"\t\"col2\"\n\"val1\"\t\"val2\"\n");
		}
		finally
		{
			csvWriter.close();
		}
	}

	@Test
	public void close() throws IOException
	{
		// FIXME enable when double closing bug in opencsv is fixed
		// Writer writer = mock(Writer.class);
		// CsvWriter csvWriter = new CsvWriter(writer);
		// csvWriter.close();
		// verify(writer).close();
	}
}
