package org.molgenis.util.tuple;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class KeyValueTupleTest
{
	private KeyValueTuple tuple;

	@BeforeMethod
	public void setUp()
	{
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("col1", "val1");
		map.put("col2", "val2");
		map.put("col3", "val3");
		tuple = new KeyValueTuple(map);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void KeyValueTuple()
	{
		new KeyValueTuple(null);
	}

	@Test
	public void getString()
	{
		assertEquals(tuple.get("col1"), "val1");
		assertEquals(tuple.get("col2"), "val2");
		assertEquals(tuple.get("col3"), "val3");
	}

	@Test
	public void getint()
	{
		assertEquals(tuple.get(0), "val1");
		assertEquals(tuple.get(1), "val2");
		assertEquals(tuple.get(2), "val3");
	}

	@Test
	public void getColNames()
	{
		Iterator<String> it = tuple.getColNames();
		assertTrue(it.hasNext());
		assertEquals(it.next(), "col1");
		assertTrue(it.hasNext());
		assertEquals(it.next(), "col2");
		assertTrue(it.hasNext());
		assertEquals(it.next(), "col3");
		assertFalse(it.hasNext());
	}
}
