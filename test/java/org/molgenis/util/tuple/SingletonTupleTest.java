package org.molgenis.util.tuple;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Iterator;

import org.testng.annotations.Test;

public class SingletonTupleTest
{
	@Test
	public void getString()
	{
		assertEquals(new SingletonTuple<Integer>(1, "col1").get("col1"), 1);
		assertNull(new SingletonTuple<Integer>(1).get("col_unknown"));
	}

	@Test
	public void getint()
	{
		assertEquals(new SingletonTuple<Integer>(1).get(0), 1);
		assertNull(new SingletonTuple<Integer>(1).get(1));
	}

	@Test
	public void getColNames()
	{
		Iterator<String> it = new SingletonTuple<Integer>(1, "col1").getColNames();
		assertTrue(it.hasNext());
		assertEquals(it.next(), "col1");
		assertFalse(it.hasNext());
	}

	@Test
	public void getNrCols()
	{
		assertEquals(new SingletonTuple<Integer>(1, "col1").getNrCols(), 1);
		assertEquals(new SingletonTuple<Integer>(1).getNrCols(), 1);
	}
}
