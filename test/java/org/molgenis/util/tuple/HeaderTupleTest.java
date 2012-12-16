package org.molgenis.util.tuple;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class HeaderTupleTest
{
	private HeaderTuple tuple;

	@BeforeMethod
	public void setUp()
	{
		tuple = new HeaderTuple(Arrays.asList("1", "2", "3"));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void HeaderListTuple()
	{
		new HeaderTuple(null);
	}

	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void getString()
	{
		tuple.get("1");
	}

	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void getint()
	{
		tuple.get(0);
	}

	@Test
	public void getColNames()
	{
		Iterator<String> it = tuple.getColNames();
		assertTrue(it.hasNext());
		assertEquals(it.next(), "1");
		assertTrue(it.hasNext());
		assertEquals(it.next(), "2");
		assertTrue(it.hasNext());
		assertEquals(it.next(), "3");
		assertFalse(it.hasNext());
	}

	@Test
	public void getNrCols()
	{
		assertEquals(3, tuple.getNrCols());
	}
}
