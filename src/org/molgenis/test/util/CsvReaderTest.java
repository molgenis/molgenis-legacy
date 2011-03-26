package org.molgenis.test.util;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.molgenis.util.CsvBufferedReaderMultiline;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.CsvStringReader;
import org.molgenis.util.Tuple;

public class CsvReaderTest
{
	@Test
	public void testRowNames() throws Exception
	{
		String csv = "col1,col2\nval1,val2\nval3,val4";

		CsvReader reader = new CsvStringReader(csv);

		assertEquals(reader.rownames(), Arrays.asList(new String[]
		{ "val1", "val3" }));
	}
	
	@Test
	public void testRowNamesMatrix() throws Exception
	{
		//so here the headers have one column less than normal data rows
		String csv = "col1,col2\nrowname1,val1,val2\nrowname2,val3,val4";

		CsvReader reader = new CsvStringReader(csv);

		assertEquals(reader.rownames(), Arrays.asList(new String[]
		{ "rowname1", "rowname2" }));
	}

	@Test
	public void testSimple() throws Exception
	{
		String csv = "col1,col2\nval1,val2\nval3,val4";

		CsvReader reader = new CsvStringReader(csv);

		assertEquals(reader.colnames(), Arrays.asList(new String[]
		{ "col1", "col2" }));

		final List<Tuple> result = new ArrayList<Tuple>();

		reader.parse(new CsvReaderListener()
		{

			@Override
			public void handleLine(int lineNumber, Tuple tuple)
					throws Exception
			{
				result.add(tuple);
			}
		});

		assertEquals(result.get(0).getString("col1"), "val1");
		assertEquals(result.get(0).getString("col2"), "val2");

		assertEquals(result.get(1).getString("col1"), "val3");
		assertEquals(result.get(1).getString("col2"), "val4");

	}

	@Test
	public void testQuotes() throws Exception
	{
		String csv = "t2col1,t2col2\nval1,\"val2 quoted \"\"test\"\" this\"\nval3,val4";

		CsvReader reader = new CsvStringReader(csv);

		assertEquals(reader.colnames(), Arrays.asList(new String[]
		{ "t2col1", "t2col2" }));

		final List<Tuple> result = new ArrayList<Tuple>();

		reader.parse(new CsvReaderListener()
		{

			@Override
			public void handleLine(int lineNumber, Tuple tuple)
					throws Exception
			{
				result.add(tuple);
			}
		});

		assertEquals(result.get(0).getString("t2col1"), "val1");
		assertEquals(result.get(0).getString("t2col2"),
				"val2 quoted \"test\" this");

		assertEquals(result.get(1).getString("t2col1"), "val3");
		assertEquals(result.get(1).getString("t2col2"), "val4");
	}
	
	@Test
	public void testMultiline() throws Exception
	{
		String csv = 
				"t2col1,t2col2\n" +
				"val1,\"val2 \n" +
				"newline and \"\"quotes\"\" work\"\n" +
				"val3,val4";

		CsvReader reader = new CsvStringReader(csv);
		final List<Tuple> rows = new ArrayList<Tuple>();
		reader.parse(new CsvReaderListener()
		{
			@Override
			public void handleLine(int lineNumber, Tuple tuple)
					throws Exception
			{
				rows.add(tuple);
			}
		});

		assertEquals(rows.get(0).getString("t2col1"), "val1");
		assertEquals(rows.get(0).getString("t2col2"),
				"val2 \nnewline and \"quotes\" work");

		assertEquals(rows.get(1).getString("t2col1"), "val3");
		assertEquals(rows.get(1).getString("t2col2"), "val4");
	}
}
