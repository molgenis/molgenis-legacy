package regressiontest.misc;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;

import matrix.test.implementations.general.Helper;
import matrix.test.implementations.general.Params;
import matrix.test.implementations.general.TestMatrix;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.molgenis.framework.db.DatabaseException;

import decorators.NameConvention;


public class TestNameConvention extends TestCase {

	@Test
	public void testEscapeEntityNames() throws Exception {
		assertEquals("name", NameConvention.escapeEntityName("123name"));
		assertEquals("x123nAmE", NameConvention.escapeEntityName("x123nAmE"));
		assertEquals("", NameConvention.escapeEntityName("@#23"));
		assertEquals("", NameConvention.escapeEntityName("123456"));
		assertEquals("x7", NameConvention.escapeEntityName("123456x7"));
		assertEquals("x2x3x", NameConvention.escapeEntityName("1x2x3x"));
		assertEquals("x1x2x3x", NameConvention.escapeEntityName("x1x2x3x"));
		assertEquals("a", NameConvention.escapeEntityName("@#23a"));
		assertEquals("", NameConvention.escapeEntityName("@#234567"));
		assertEquals("x", NameConvention.escapeEntityName("@#234567x"));
		assertEquals("x234567", NameConvention.escapeEntityName("x@#234567"));
		assertEquals("x1234", NameConvention.escapeEntityName("@#234567x1234"));
		assertEquals("", NameConvention.escapeEntityName("1"));
		assertEquals("x", NameConvention.escapeEntityName("1x"));
		assertEquals("x1", NameConvention.escapeEntityName("x1"));
		assertEquals("xx", NameConvention.escapeEntityName("11xx"));
		assertEquals("xx11", NameConvention.escapeEntityName("xx11"));
		assertEquals("xx", NameConvention.escapeEntityName("x!@#$x"));
		assertEquals("xx1", NameConvention.escapeEntityName("5#%x!@#$x$#1"));
	}

	@Test
	public void testEscapeFileNames() throws Exception {
		assertEquals("name", NameConvention.escapeFileName("123name"));
		assertEquals("name", NameConvention.escapeFileName("123nAmE"));
		assertEquals("", NameConvention.escapeFileName("123"));
		assertEquals("xxx", NameConvention.escapeFileName("$$^123!x1X1x1"));
	}

	@Test
	public void testValidateEntityNames() throws DatabaseException{
		Throwable e = null;
		try {
			NameConvention.validateEntityName("123name");
		} catch (Throwable ex) {
			e = ex;
		}
		assertTrue(e instanceof DatabaseException);
		
		try {
			NameConvention.validateEntityName("name@#$");
		} catch (Throwable ex) {
			e = ex;
		}
		assertTrue(e instanceof DatabaseException);

		NameConvention.validateEntityName("name_1234567890");
		NameConvention.validateEntityName("myName");

	}
	
	@Test
	public void testValidateFileNames() throws DatabaseException{
		Throwable e = null;
		try {
			NameConvention.validateFileName("name123");
		} catch (Throwable ex) {
			e = ex;
		}
		assertTrue(e instanceof DatabaseException);
		
		try {
			NameConvention.validateFileName("nAme");
		} catch (Throwable ex) {
			e = ex;
		}
		assertTrue(e instanceof DatabaseException);
		
		NameConvention.validateFileName("name");
		NameConvention.validateFileName("xxxxxx");
	}

}