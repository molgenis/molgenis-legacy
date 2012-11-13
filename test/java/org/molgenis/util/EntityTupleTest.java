package org.molgenis.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.Vector;

import org.testng.annotations.Test;

public class EntityTupleTest
{
	@Test
	public void testGetFieldNames()
	{
		Vector<String> fields = new Vector<String>();
		fields.add("field1");
		fields.add("field2");

		Entity entity = when(mock(Entity.class).getFields()).thenReturn(fields).getMock();

		EntityTuple entityTuple = new EntityTuple(entity);
		assertEquals(entityTuple.getFieldNames(), fields);
	}

	@Test
	public void testGetColName()
	{
		Vector<String> fields = new Vector<String>();
		fields.add("field1");
		fields.add("field2");

		Entity entity = when(mock(Entity.class).getFields()).thenReturn(fields).getMock();

		EntityTuple entityTuple = new EntityTuple(entity);
		assertEquals(entityTuple.getColName(0), "field1");
		assertEquals(entityTuple.getColName(1), "field2");
		assertNull(entityTuple.getColName(-1));
		assertNull(entityTuple.getColName(2));
	}

	@Test
	public void testGetObjectByColumnName()
	{
		String colName = "name";
		String value = "value";

		Entity entity = when(mock(Entity.class).get(colName)).thenReturn(value).getMock();
		EntityTuple entityTuple = new EntityTuple(entity);

		assertEquals(entityTuple.getObject(colName), value);
	}

	@Test
	public void testGetObjectByColumnIndex()
	{
		Vector<String> fields = new Vector<String>();
		fields.add("field1");
		fields.add("field2");
		String value = "value";

		Entity entity = mock(Entity.class);
		when(entity.getFields()).thenReturn(fields).getMock();
		when(entity.get("field1")).thenReturn(value);

		EntityTuple entityTuple = new EntityTuple(entity);

		assertEquals(entityTuple.getObject(0), "value");
	}

	@Test
	public void testGetNrColumns()
	{
		Vector<String> fields = new Vector<String>();
		fields.add("field1");
		fields.add("field2");

		Entity entity = when(mock(Entity.class).getFields()).thenReturn(fields).getMock();
		EntityTuple entityTuple = new EntityTuple(entity);

		assertEquals(entityTuple.getNrColumns(), fields.size());
	}
}
