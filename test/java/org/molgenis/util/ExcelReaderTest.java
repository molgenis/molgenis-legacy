package org.molgenis.util;

import static org.testng.AssertJUnit.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.util.test.AbstractResourceTest;
import org.testng.annotations.Test;

public class ExcelReaderTest extends AbstractResourceTest
{
	@Test
	public void testIterator() throws Exception
	{
		File csvFile = getTestResource("/test.xls");

		ExcelReader reader = new ExcelReader(csvFile, "test");

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
}
