package org.molgenis.util.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.util.ExcelReader;
import org.molgenis.util.Tuple;
import org.testng.annotations.Test;

public class ExcelReaderTest
{
	@Test
	public void testIterator() throws Exception
	{
		//put this in file
		String csv = "col1,col2\nval1,val2\nval3,val4";
		File csvFile = new File("/Users/mswertz/test2.xls");

		ExcelReader reader = new ExcelReader(csvFile,"test");

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
