package org.molgenis.util;

import static org.testng.AssertJUnit.assertEquals;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

public class CsvFileReaderTest
{
	@Test
	public void testSimple() throws Exception
	{
		File file0 = File.createTempFile("CsvFileReaderTest_file0", null);
		try
		{
			FileUtils.write(file0, "col1,col2\nval1,val2\nval3,val4", Charset.forName("UTF-8"));

			CsvFileReader reader = new CsvFileReader(file0);
			try
			{
				assertEquals(reader.colnames(), Arrays.asList(new String[]
				{ "col1", "col2" }));

				final List<Tuple> result = new ArrayList<Tuple>();
				for (Tuple tuple : reader)
				{
					result.add(tuple);
				}

				assertEquals(result.get(0).getString("col1"), "val1");
				assertEquals(result.get(0).getString("col2"), "val2");

				assertEquals(result.get(1).getString("col1"), "val3");
				assertEquals(result.get(1).getString("col2"), "val4");
			}
			finally
			{
				IOUtils.closeQuietly(reader);
			}
		}
		finally
		{
			file0.delete();
		}
	}

	@Test
	public void testIterator() throws Exception
	{
		File file0 = File.createTempFile("CsvFileReaderTest_file0", null);
		try
		{
			FileUtils.write(file0, "col1,col2\nval1,val2\nval3,val4", Charset.forName("UTF-8"));

			CsvFileReader reader = new CsvFileReader(file0);
			try
			{
				List<Tuple> result = new ArrayList<Tuple>();
				for (Tuple t : reader)
				{
					result.add(t);
				}

				assertEquals(result.get(0).getString("col1"), "val1");
				assertEquals(result.get(0).getString("col2"), "val2");

				assertEquals(result.get(1).getString("col1"), "val3");
				assertEquals(result.get(1).getString("col2"), "val4");
			}
			finally
			{
				IOUtils.closeQuietly(reader);
			}
		}
		finally
		{
			file0.delete();
		}
	}

	@Test
	public void testQuotes() throws Exception
	{
		File file0 = File.createTempFile("CsvFileReaderTest_file0", null);
		try
		{
			FileUtils.write(file0, "t2col1,t2col2\nval1,\"val2 quoted \"\"test\"\" this\"\nval3,val4",
					Charset.forName("UTF-8"));

			CsvFileReader reader = new CsvFileReader(file0);
			try
			{
				assertEquals(reader.colnames(), Arrays.asList(new String[]
				{ "t2col1", "t2col2" }));

				final List<Tuple> result = new ArrayList<Tuple>();
				for (Tuple t : reader)
					result.add(t);

				assertEquals(result.get(0).getString("t2col1"), "val1");
				assertEquals(result.get(0).getString("t2col2"), "val2 quoted \"test\" this");

				assertEquals(result.get(1).getString("t2col1"), "val3");
				assertEquals(result.get(1).getString("t2col2"), "val4");
			}
			finally
			{
				IOUtils.closeQuietly(reader);
			}
		}
		finally
		{
			file0.delete();
		}
	}

	@Test
	public void testMultilineQuotesEscapingAndEmptyLines() throws Exception
	{
		File file0 = File.createTempFile("CsvFileReaderTest_file0", null);
		try
		{
			FileUtils.write(file0, "t2col1,t2col2\n" + "val1,\"val2 \n" + "newline and \"\"quotes\"\"\n\n work\"\n"
					+ "val3,val4", Charset.forName("UTF-8"));

			CsvFileReader reader = new CsvFileReader(file0);
			try
			{
				// test rownames
				List<String> rowNames = reader.rownames();
				assertEquals(2, rowNames.size());
				assertEquals("val1", rowNames.get(0));
				assertEquals("val3", rowNames.get(1));

				final List<Tuple> rows = new ArrayList<Tuple>();
				for (Tuple t : reader)
				{
					rows.add(t);
				}

				assertEquals(rows.size(), 2);

				assertEquals(rows.get(0).getString("t2col1"), "val1");
				assertEquals(rows.get(0).getString("t2col2"), "val2 \nnewline and \"quotes\"\n\n work");

				assertEquals(rows.get(1).getString("t2col1"), "val3");
				assertEquals(rows.get(1).getString("t2col2"), "val4");
			}
			finally
			{
				IOUtils.closeQuietly(reader);
			}
		}
		finally
		{
			file0.delete();
		}
	}

	@Test
	public void testBlockStart() throws Exception
	{
		File file0 = File.createTempFile("CsvFileReaderTest_file0", null);
		try
		{
			FileUtils.write(file0,
					"##bla1\n##bla2\n##bla3\nHeader1\tHeader2\nRow1Col1\tRow1Col2\nRow2Col1\tRow2Col2\n",
					Charset.forName("UTF-8"));

			CsvFileReader reader = new CsvFileReader(file0, "##bla3");

			try
			{
				// test columnnames
				List<String> columnNames = reader.columnnames;
				assertEquals(2, columnNames.size());
				assertEquals("Header1", columnNames.get(0));
				assertEquals("Header2", columnNames.get(1));
			}
			finally
			{
				IOUtils.closeQuietly(reader);
			}
		}
		finally
		{
			file0.delete();
		}
	}
}