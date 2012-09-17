//package org.molgenis.datatable.test;
//
//import java.io.File;
//import java.util.List;
//
//import org.molgenis.datatable.model.BinaryTupleTable;
//import org.molgenis.datatable.model.TupleTable;
//import org.molgenis.util.Tuple;
//import org.testng.Assert;
//import org.testng.annotations.Test;
//
//public class BinaryTupleTableTest
//{
//
//	@Test
//	public void test1() throws Exception
//	{
//		TupleTable t = new BinaryTupleTable(new File("/Users/mswertz/test.bin"));
//		
//		List<Tuple> rows = t.getRows();
//		Assert.assertEquals(rows.size(), 10);
//		
//		Assert.assertEquals(rows.get(0).getObject("pkP1050"), 0.008762753);
//		Assert.assertEquals(rows.get(9).getObject("pkP6171"), 85.99224806);
//		
//		for(Tuple row: t.getRows())
//		{
//			Assert.assertEquals(row.size(), 121);
//			System.out.println(row);
//		}
//	}
//	
//	@Test
//	public void testLimitOffset() throws Exception
//	{
//		TupleTable t = new BinaryTupleTable(new File("/Users/mswertz/test.bin"));
//		t.setLimit(1);
//		t.setOffset(5);
//		
//		List<Tuple> rows = t.getRows();
//		Assert.assertEquals(rows.size(), 1);
//		
//		for(Tuple row: t.getRows())
//		{
//			System.out.println(row);
//			Assert.assertEquals(row.size(), 121);
//		}
//		
//		Assert.assertEquals(rows.get(0).getObject("pkP1050"),0.027330887);
//	}
//	
//	@Test
//	public void testColLimitOffset() throws Exception
//	{
//		TupleTable t = new BinaryTupleTable(new File("/Users/mswertz/test.bin"));
//		t.setColLimit(10);
//		t.setColOffset(10);
//		
//		List<Tuple> rows = t.getRows();
//		Assert.assertEquals(rows.size(), 10);
//		
//		for(Tuple row: t.getRows())
//		{
//			System.out.println(row);
//			Assert.assertEquals(rows.size(), 10);
//		}
//		
//		Assert.assertEquals(rows.get(0).getObject("pkP1059"), 0.043075);
//		Assert.assertEquals(rows.get(9).getObject("pkP1072"), 142.16);
//	}
// }
