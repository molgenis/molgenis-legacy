package org.molgenis.datatable.test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.molgenis.datatable.model.CollectionTableModel;
import org.molgenis.datatable.model.SimpleTableModel;
import org.molgenis.datatable.view.BeanValueBinding;
import org.molgenis.datatable.view.CsvExporter;
import org.molgenis.datatable.view.TupleValueBinding;
import org.molgenis.fieldtypes.StringField;
import org.molgenis.framework.db.Database;
import org.molgenis.matrix.MatrixException;
import org.molgenis.model.elements.Field;
import org.molgenis.organization.Investigation;
import org.molgenis.util.Tuple;
import org.testng.annotations.Test;

import app.DatabaseFactory;

public class TestCSVExporter
{


	public class Person
	{
		public String firstName;
		public String lastName;

		public Person(){}
		
		public Person(String firstName, String lastName)
		{
			this.firstName = firstName;
			this.lastName = lastName;
		}

		public String getFirstName()
		{
			return firstName;
		}

		public String getLastName()
		{
			return lastName;
		}
	}

	
	@Test
	public void testBeanExport() {

		final List<Person> persons = Arrays.asList(new Person("jay", "lo"), new Person("Daan", "Banaan"));
		List<Field> fields = Arrays.asList(new Field(null, "firstName", new StringField()), new Field(null, "lastName", new StringField()));
		
		SimpleTableModel<Person> tableModel = new CollectionTableModel<Person>(persons, new BeanValueBinding<Person>(), fields);

		
		OutputStream export = export(tableModel);
		Assert.assertEquals("firstName,lastName\njay,lo\nDaan,Banaan\n", export.toString());
	}

	private <T> OutputStream export(SimpleTableModel<T> tableModel)
	{
		try
		{
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			CsvExporter<T> exporter = new CsvExporter<T>(tableModel, outstream);
			exporter.export();
			return outstream;
		}
		catch (MatrixException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void testEntityExport() {
		Investigation inv1 = new Investigation();
		inv1.setId(1);
		inv1.setName("inv1");
		Investigation inv2 = new Investigation();
		inv2.setId(2);
		inv2.setName("inv2");
		List<Investigation> investigations = Arrays.asList(inv1, inv2);
		List<Field> fields = Arrays.asList(
				new Field(null, Investigation.ID, new StringField()), 
				new Field(null, Investigation.NAME, new StringField()));
		
		SimpleTableModel<Investigation> tableModel = 
				new CollectionTableModel<Investigation>(investigations, new BeanValueBinding<Investigation>(), fields);
		OutputStream export = export(tableModel);
		Assert.assertEquals("id,name\n1,inv1\n2,inv2\n", export.toString());
	}
	
	@Test
	public void testTupleExport() {
		Investigation inv1 = new Investigation();
		inv1.setId(1);
		inv1.setName("inv1");
		Tuple tuple1 = inv1.getValues();
		Investigation inv2 = new Investigation();
		inv2.setId(2);
		inv2.setName("inv2");
		Tuple tuple2 = inv2.getValues();
		Collection<Tuple> tuples = Arrays.asList(tuple1, tuple2);
		
		List<Field> fields = Arrays.asList(
				new Field(null, Investigation.ID, new StringField()), 
				new Field(null, Investigation.NAME, new StringField()));
		
		SimpleTableModel<Tuple> tableModel = 
				new CollectionTableModel<Tuple>(tuples , new TupleValueBinding<Tuple>(), fields);
		OutputStream export = export(tableModel);
		Assert.assertEquals("id,name\n1,inv1\n2,inv2\n", export.toString());
	}
	
	@Test
	public void testDatabaseTuple() throws Exception {
		Database db = DatabaseFactory.create();
		EntityManager em = db.getEntityManager();
		List resultList = em.createNativeQuery("SELECT id, name FROM Investigation").getResultList();
		
		
//		Investigation inv1 = new Investigation();
//		inv1.setId(1);
//		inv1.setName("inv1");
//		Tuple tuple1 = inv1.getValues();
//		Investigation inv2 = new Investigation();
//		inv2.setId(2);
//		inv2.setName("inv2");
//		Tuple tuple2 = inv2.getValues();
//		Collection<Tuple> tuples = Arrays.asList(tuple1, tuple2);
		
		List<Field> fields = Arrays.asList(
				new Field(null, Investigation.ID, new StringField()), 
				new Field(null, Investigation.NAME, new StringField()));
		
		SimpleTableModel<Tuple> tableModel = 
				new CollectionTableModel<Tuple>(resultList , new TupleValueBinding<Tuple>(), fields);
		OutputStream export = export(tableModel);
		Assert.assertEquals("id,name\n1,inv1\n2,inv2\n", export.toString());
	}
}
