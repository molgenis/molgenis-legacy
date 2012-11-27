package org.molgenis.util;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

public class CsvFileReaderTest
{
	/**
	 * Test based on au.com.bytecode.opencsv.CSVReaderTest
	 * 
	 * @throws Exception
	 */
	@Test
	public void testOpenCSV() throws Exception
	{
		StringBuilder sb = new StringBuilder();
		sb.append("a,b,c").append("\n"); // standard case
		sb.append("a,\"b,b,b\",c").append("\n"); // quoted elements
		sb.append(",,").append("\n"); // empty elements
		sb.append("a,\"PO Box 123,\nKippax,ACT. 2615.\nAustralia\",d.\n");
		sb.append("\"Glen \"\"The Man\"\" Smith\",Athlete,Developer\n"); // Test
																			// quoted
																			// quote
																			// chars
		sb.append("\"\"\"\"\"\",\"test\"\n"); // """""","test" representing: "",
												// test
		sb.append("\"a\nb\",b,\"\nd\",e\n");

		File file0 = File.createTempFile("CsvFileReaderTest_file0", null);
		try
		{
			FileUtils.write(file0, sb.toString(), Charset.forName("UTF-8"));
			CsvFileReader reader = new CsvFileReader(file0);
			try
			{
				// test normal case
				List<String> nextLine = reader.getRow();
				assertEquals("a", nextLine.get(0));
				assertEquals("b", nextLine.get(1));
				assertEquals("c", nextLine.get(2));

				// test quoted commas
				nextLine = reader.getRow();
				assertEquals("a", nextLine.get(0));
				assertEquals("b,b,b", nextLine.get(1));
				assertEquals("c", nextLine.get(2));

				// test empty elements
				nextLine = reader.getRow();
				assertEquals(3, nextLine.size());

				// test multiline quoted
				nextLine = reader.getRow();
				assertEquals(3, nextLine.size());

				// test quoted quote chars
				nextLine = reader.getRow();
				assertEquals("Glen \"The Man\" Smith", nextLine.get(0));

				nextLine = reader.getRow();
				assertEquals("\"\"", nextLine.get(0)); // check the tricky
														// situation
				assertEquals("test", nextLine.get(1)); // make sure we didn't
														// ruin the next field..

				nextLine = reader.getRow();
				assertEquals(4, nextLine.size());

				// test end of stream
				assertNull(reader.getRow());
			}
			finally
			{
				reader.close();
			}
		}
		finally
		{
			file0.delete();
		}
	}

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

	@Test
	public void test() throws IOException
	{
		File file0 = File.createTempFile("CsvFileReaderTest_file0", null);
		try
		{

			String csv = "a|b,c\td";

			FileUtils.write(file0, csv, Charset.forName("UTF-8"));

			CsvFileReader reader = new CsvFileReader(file0, false);

			try
			{
				for (Iterator<Tuple> it = reader.iterator(); it.hasNext();)
					System.out.println(it.next());
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