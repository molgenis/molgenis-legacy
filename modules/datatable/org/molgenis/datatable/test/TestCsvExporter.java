//package org.molgenis.datatable.test;
//
//import java.io.ByteArrayOutputStream;
//import java.util.List;
//
//import junit.framework.Assert;
//
//import org.molgenis.datatable.model.CsvTable;
//import org.molgenis.datatable.model.TupleTable;
//import org.molgenis.datatable.view.CsvExporter;
//import org.molgenis.model.elements.Field;
//import org.molgenis.util.Tuple;
//import org.testng.annotations.Test;
//
//public class TestCsvExporter
//{
//	/**
//	 * Test by first using CsvExport to export table1, and the CsvTable to read it back as table2.
//	 * Obviously table1.equals(table2) == true.
//	 * 
//	 * @throws Exception
//	 */
//	@Test
//	public void testCsvRoundTrip() throws Exception
//	{
//		//create in memory TupleTable
//		TupleTable table1 = MemoryTableFactory.create();
//		
//		//export to stream
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		new CsvExporter(table1).export(bos);
//		
//		//convert to string
//		String csv = bos.toString();
//		System.out.println("CSV exported =\n"+csv);
//		
//		//load via CsvTable
//		TupleTable table2 = new CsvTable(csv);
//		
//		//compare
//		Assert.assertEquals(table1.getColumns(), table2.getColumns());
//		List<Tuple> rows1 = table1.getRows();
//		List<Tuple> rows2 = table2.getRows();
//		Assert.assertEquals(rows1.size(), rows2.size());
//		for(int i = 0; i < rows1.size(); i ++)
//		{
//			for(Field f: table1.getColumns())
//			{
//				//nb: this doesn't do type safety as roundtrip will return strings
//				Assert.assertEquals(rows1.get(i).getString(f.getName()), rows2.get(i).getString(f.getName()));
//			}
//		}
//		
//	}
//}
