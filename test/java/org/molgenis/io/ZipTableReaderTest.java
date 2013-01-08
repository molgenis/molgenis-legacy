package org.molgenis.io;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

public class ZipTableReaderTest
{
	private ZipFile zipFile;

	@BeforeMethod
	public void setUp() throws URISyntaxException, ZipException, IOException
	{
		this.zipFile = new ZipFile(new File(this.getClass().getResource("/tables.zip").getFile()));
	}

	@SuppressWarnings("resource")
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void ZipTableReader() throws IOException
	{
		new ZipTableReader(null);
	}

	@Test
	public void getTableNames() throws IOException
	{
		ZipTableReader zipTableReader = new ZipTableReader(zipFile);
		try
		{
			List<String> tableNames = Lists.newArrayList(zipTableReader.getTableNames());
			assertEquals(3, tableNames.size());
			assertTrue(tableNames.contains("0"));
			assertTrue(tableNames.contains("1"));
			assertTrue(tableNames.contains("2"));
		}
		finally
		{
			zipTableReader.close();
		}
	}

	@Test
	public void getTupleReader() throws IOException
	{
		ZipTableReader zipTableReader = new ZipTableReader(zipFile);
		try
		{
			TupleReader tupleReader0 = zipTableReader.getTupleReader("0");
			try
			{
				Collection<String> colNames = Lists.newArrayList(tupleReader0.colNamesIterator());
				assertEquals(2, colNames.size());
				assertTrue(colNames.contains("col1"));
				assertTrue(colNames.contains("col2"));
			}
			finally
			{
				tupleReader0.close();
			}

			TupleReader tupleReader1 = zipTableReader.getTupleReader("1");
			try
			{
				Collection<String> colNames = Lists.newArrayList(tupleReader1.colNamesIterator());
				assertEquals(2, colNames.size());
				assertTrue(colNames.contains("col3"));
				assertTrue(colNames.contains("col4"));
			}
			finally
			{
				tupleReader1.close();
			}

			TupleReader tupleReader2 = zipTableReader.getTupleReader("2");
			try
			{
				Collection<String> colNames = Lists.newArrayList(tupleReader2.colNamesIterator());
				assertEquals(2, colNames.size());
				assertTrue(colNames.contains("col5"));
				assertTrue(colNames.contains("col6"));
			}
			finally
			{
				tupleReader2.close();
			}
		}
		finally
		{
			zipTableReader.close();
		}
	}

	@Test
	public void iterator() throws IOException
	{
		ZipTableReader zipTableReader = new ZipTableReader(zipFile);
		try
		{
			Iterator<TupleReader> it = zipTableReader.iterator();
			assertTrue(it.hasNext());
			TupleReader tupleReader0 = it.next();
			assertNotNull(tupleReader0);
			tupleReader0.close();

			assertTrue(it.hasNext());
			TupleReader tupleReader1 = it.next();
			assertNotNull(tupleReader1);
			tupleReader1.close();

			assertTrue(it.hasNext());
			TupleReader tupleReader2 = it.next();
			assertNotNull(tupleReader2);
			tupleReader2.close();

			assertFalse(it.hasNext());
		}
		finally
		{
			zipTableReader.close();
		}
	}
}
