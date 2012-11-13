package org.molgenis.util;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.molgenis.util.test.AbstractResourceTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ExcelReaderTest extends AbstractResourceTest
{
	private ExcelReader reader;

	@BeforeMethod
	public void setUp() throws IOException
	{
		File csvFile = getTestResource("/test.xls");
		reader = new ExcelReader(csvFile, "test");
		reader.setMissingValues("XXX");
	}

	@Test
	public void testIterator() throws Exception
	{
		List<Tuple> result = new ArrayList<Tuple>();
		try
		{
			for (Tuple t : reader)
			{
				result.add(t);
			}
		}
		finally
		{
			IOUtils.closeQuietly(reader);
		}
		assertEquals(result.get(0).getString("col1"), "val1");
		assertEquals(result.get(0).getString("col2"), "val2");

		assertEquals(result.get(1).getString("col1"), "val3");
		assertEquals(result.get(1).getString("col2"), "val4");

		assertNull(result.get(2).getString("col1"));
	}

	@Test
	public void testColnames()
	{
		List<String> colNames = reader.colnames();
		assertEquals(colNames.size(), 2);
		assertEquals(colNames.get(0), "col1");
		assertEquals(colNames.get(1), "col2");
	}
}
