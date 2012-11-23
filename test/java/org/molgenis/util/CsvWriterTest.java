package org.molgenis.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import org.testng.annotations.Test;

public class CsvWriterTest
{
	@Test
	public void writeHeader() throws IOException
	{
		StringWriter strWriter = new StringWriter();
		CsvWriter csvWriter = new CsvWriter(strWriter, Arrays.asList("h1", "h2", "h3"));
		try
		{
			csvWriter.writeHeader();
		}
		finally
		{
			csvWriter.close();
		}
		assertEquals("h1\th2\th3\n", strWriter.toString());
	}

	@Test
	public void writeMatrix() throws IOException
	{
		StringWriter strWriter = new StringWriter();
		CsvWriter csvWriter = new CsvWriter(strWriter, Arrays.asList("h1", "h2", "h3"));
		try
		{
			String[][] values = new String[3][3];
			values[0][0] = "v1";
			values[0][1] = "v2";
			values[0][2] = "v3";
			values[1][0] = "v4";
			values[1][1] = "v5";
			values[1][2] = "v6";
			values[2][0] = "v7";
			values[2][1] = "v8";
			values[2][2] = "v9";

			csvWriter.writeMatrix(Arrays.asList("r1", "r2", "r3"), Arrays.asList("c1", "c2", "c3"), values);
		}
		finally
		{
			csvWriter.close();
		}
		assertEquals("\tc1\tc2\tc3\nr1\tv1\tv2\tv3\nr2\tv4\tv5\tv6\nr3\tv7\tv8\tv9\n", strWriter.toString());
	}

	@Test
	public void writeMatrix_missing() throws IOException
	{
		StringWriter strWriter = new StringWriter();
		CsvWriter csvWriter = new CsvWriter(strWriter, Arrays.asList("h1", "h2", "h3"));
		csvWriter.setMissingValue("x");
		try
		{
			String[][] values = new String[3][3];
			values[0][0] = "v1";
			values[0][1] = "v2";
			values[0][2] = null;
			values[1][0] = "v4";
			values[1][1] = "v5";
			values[1][2] = "v6";
			values[2][0] = "v7";
			values[2][1] = "v8";
			values[2][2] = "v9";

			csvWriter.writeMatrix(Arrays.asList("r1", "r2", "r3"), Arrays.asList("c1", "c2", "c3"), values);
		}
		finally
		{
			csvWriter.close();
		}
		assertEquals("\tc1\tc2\tc3\nr1\tv1\tv2\tx\nr2\tv4\tv5\tv6\nr3\tv7\tv8\tv9\n", strWriter.toString());
	}

	@Test
	public void writeRowEntity() throws IOException
	{
		StringWriter strWriter = new StringWriter();
		CsvWriter csvWriter = new CsvWriter(strWriter, Arrays.asList("h1", "h2", "h3"));
		try
		{
			Entity entity = mock(Entity.class);
			when(entity.get("h1")).thenReturn("v1");
			when(entity.get("h2")).thenReturn("v2");
			when(entity.get("h3")).thenReturn("v3");
			csvWriter.writeRow(entity);
		}
		finally
		{
			csvWriter.close();
		}
		assertEquals("v1\tv2\tv3\n", strWriter.toString());
	}

	@Test
	public void writeRowEntity_missing() throws IOException
	{
		StringWriter strWriter = new StringWriter();
		CsvWriter csvWriter = new CsvWriter(strWriter, Arrays.asList("h1", "h2", "h3", "h4"));
		csvWriter.setMissingValue("x");
		try
		{
			Entity entity = mock(Entity.class);
			when(entity.get("h1")).thenReturn("v1");
			when(entity.get("h2")).thenReturn("v2");
			when(entity.get("h4")).thenReturn("v4");
			csvWriter.writeRow(entity);
		}
		finally
		{
			csvWriter.close();
		}
		assertEquals("v1\tv2\tx\tv4\n", strWriter.toString());
	}

	@Test
	public void writeRowTuple() throws IOException
	{
		StringWriter strWriter = new StringWriter();
		CsvWriter csvWriter = new CsvWriter(strWriter, Arrays.asList("h1", "h2", "h3"));
		try
		{
			Tuple tuple = new SimpleTuple(Arrays.asList("h1", "h2", "h3", "h4"));
			tuple.set(0, "v1");
			tuple.set(1, "v2");
			tuple.set(2, "v3");
			tuple.set(3, "v4");
			csvWriter.writeRow(tuple);
		}
		finally
		{
			csvWriter.close();
		}
		assertEquals("v1\tv2\tv3\n", strWriter.toString());
	}

	@Test
	public void writeRowTuple_missing() throws IOException
	{
		StringWriter strWriter = new StringWriter();
		CsvWriter csvWriter = new CsvWriter(strWriter, Arrays.asList("h1", "h2", "h3", "h4"));
		csvWriter.setMissingValue("x");
		try
		{
			Tuple tuple = new SimpleTuple(Arrays.asList("h1", "h2", "h4"));
			tuple.set(0, "v1");
			tuple.set(1, "v2");
			tuple.set(2, "v4");
			csvWriter.writeRow(tuple);
		}
		finally
		{
			csvWriter.close();
		}
		assertEquals("v1\tv2\tx\tv4\n", strWriter.toString());
	}

	@Test
	public void writeSeparator() throws IOException
	{
		StringWriter strWriter = new StringWriter();
		CsvWriter csvWriter = new CsvWriter(strWriter, '~');
		try
		{
			csvWriter.writeSeparator();
		}
		finally
		{
			csvWriter.close();
		}
		assertEquals("~", strWriter.toString());
	}

	@Test
	public void writeValue_String() throws IOException
	{
		StringWriter strWriter = new StringWriter();
		CsvWriter csvWriter = new CsvWriter(strWriter);
		try
		{
			csvWriter.writeValue("s1");
		}
		finally
		{
			csvWriter.close();
		}
		assertEquals("s1", strWriter.toString());
	}

	@Test
	public void writeValue_String_escaping() throws IOException
	{
		StringWriter strWriter = new StringWriter();
		CsvWriter csvWriter = new CsvWriter(strWriter);
		csvWriter.setSeparator(',');
		try
		{
			csvWriter.writeValue("s1,s2");
		}
		finally
		{
			csvWriter.close();
		}
		assertEquals("\"s1,s2\"", strWriter.toString());
	}

	@Test
	public void writeValue_String_escaping2() throws IOException
	{
		StringWriter strWriter = new StringWriter();
		CsvWriter csvWriter = new CsvWriter(strWriter);
		csvWriter.setSeparator('\t');
		try
		{
			csvWriter.writeValue("s1,s2");
		}
		finally
		{
			csvWriter.close();
		}
		assertEquals("s1,s2", strWriter.toString());
	}

	@Test
	public void writeValue_List() throws IOException
	{
		StringWriter strWriter = new StringWriter();
		CsvWriter csvWriter = new CsvWriter(strWriter);
		try
		{
			csvWriter.writeValue(Arrays.asList("s1", "s2", "s3"));
		}
		finally
		{
			csvWriter.close();
		}
		assertEquals("s1,s2,s3", strWriter.toString());
	}

	@Test
	public void writeValue_List_separator() throws IOException
	{
		StringWriter strWriter = new StringWriter();
		CsvWriter csvWriter = new CsvWriter(strWriter);
		try
		{
			csvWriter.writeValue(Arrays.asList("s1", "s2", "s3"));
		}
		finally
		{
			csvWriter.close();
		}
		assertEquals("s1,s2,s3", strWriter.toString());
	}

	@Test
	public void writeValue_List_escaping() throws IOException
	{
		StringWriter strWriter = new StringWriter();
		CsvWriter csvWriter = new CsvWriter(strWriter);
		csvWriter.setSeparator('\t');
		try
		{
			csvWriter.writeValue(Arrays.asList("s1a\ts1b", "s2", "s3"));
		}
		finally
		{
			csvWriter.close();
		}
		assertEquals("\"s1a\ts1b,s2,s3\"", strWriter.toString());
	}
}
