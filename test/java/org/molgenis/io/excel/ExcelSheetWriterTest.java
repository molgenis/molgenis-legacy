package org.molgenis.io.excel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.molgenis.io.processor.CellProcessor;
import org.molgenis.util.tuple.HeaderTuple;
import org.molgenis.util.tuple.KeyValueTuple;
import org.molgenis.util.tuple.Tuple;
import org.molgenis.util.tuple.ValueTuple;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ExcelSheetWriterTest
{
	private ExcelWriter excelWriter;
	private ByteArrayOutputStream bos;
	private ExcelSheetWriter excelSheetWriter;

	@BeforeMethod
	public void setUp() throws IOException
	{
		bos = new ByteArrayOutputStream();
		excelWriter = new ExcelWriter(bos);
		excelSheetWriter = excelWriter.createSheet("sheet");
	}

	@AfterMethod
	public void tearDown() throws IOException
	{
		excelWriter.close();
	}

	@Test
	public void addCellProcessor() throws IOException
	{
		CellProcessor processor = when(mock(CellProcessor.class).processHeader()).thenReturn(true).getMock();
		Tuple headerTuple = mock(Tuple.class);
		when(headerTuple.getNrCols()).thenReturn(2);
		when(headerTuple.getColNames()).thenReturn(Arrays.asList("col1", "col2").iterator());

		Tuple dataTuple = mock(Tuple.class);
		when(dataTuple.getNrCols()).thenReturn(2);
		when(dataTuple.get("col1")).thenReturn("val1");
		when(dataTuple.get("col2")).thenReturn("val2");

		excelSheetWriter.addCellProcessor(processor);
		excelSheetWriter.writeColNames(headerTuple);
		excelSheetWriter.write(dataTuple);

		verify(processor).process("col1");
		verify(processor).process("col2");
	}

	@Test
	public void write() throws IOException
	{
		excelSheetWriter.write(new ValueTuple(Arrays.asList("val1", "val2", "val3")));
		excelSheetWriter.write(new ValueTuple(Arrays.asList("val4", "val5")));
		excelWriter.close();

		ExcelReader excelReader = new ExcelReader(new ByteArrayInputStream(bos.toByteArray()), false);
		try
		{
			Iterator<Tuple> it = excelReader.getSheet("sheet").iterator();
			assertTrue(it.hasNext());
			Tuple tuple0 = it.next();
			assertEquals(tuple0.getString(0), "val1");
			assertEquals(tuple0.getString(1), "val2");
			assertEquals(tuple0.getString(2), "val3");
			assertTrue(it.hasNext());
			Tuple tuple1 = it.next();
			assertEquals(tuple1.getString(0), "val4");
			assertEquals(tuple1.getString(1), "val5");
			assertFalse(it.hasNext());
		}
		finally
		{
			excelReader.close();
		}
	}

	@Test
	public void writeColNames() throws IOException
	{
		Map<String, String> row1 = new HashMap<String, String>();
		row1.put("col1", "val1");
		row1.put("col2", "val2");
		Map<String, String> row2 = new HashMap<String, String>();
		row2.put("col1", "val3");
		row2.put("col2", "val4");

		excelSheetWriter.writeColNames(new HeaderTuple(Arrays.asList("col1", "col2")));
		excelSheetWriter.write(new KeyValueTuple(row1));
		excelSheetWriter.write(new KeyValueTuple(row2));
		excelWriter.close();

		ExcelReader excelReader = new ExcelReader(new ByteArrayInputStream(bos.toByteArray()), true);
		try
		{
			Iterator<Tuple> it = excelReader.getSheet("sheet").iterator();
			assertTrue(it.hasNext());
			Tuple tuple0 = it.next();
			assertEquals(tuple0.getString("col1"), "val1");
			assertEquals(tuple0.getString("col2"), "val2");
			assertTrue(it.hasNext());
			Tuple tuple1 = it.next();
			assertEquals(tuple1.getString("col1"), "val3");
			assertEquals(tuple1.getString("col2"), "val4");
			assertFalse(it.hasNext());
		}
		finally
		{
			excelReader.close();
		}
	}
}
