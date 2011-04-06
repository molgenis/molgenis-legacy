package regressiontest.misc;

import junit.framework.TestCase;

import org.junit.Test;
import org.molgenis.framework.db.DatabaseException;

import decorators.NameConvention;


public class TestNameConvention extends TestCase {

	@Test
	public void testEscapeEntityNames() throws Exception {
		assertEquals("name", NameConvention.escapeEntityNameStrict("123name"));
		assertEquals("x123nAmE", NameConvention.escapeEntityNameStrict("x123nAmE"));
		assertEquals("x7", NameConvention.escapeEntityNameStrict("123456x7"));
		assertEquals("x2x3x", NameConvention.escapeEntityNameStrict("1x2x3x"));
		assertEquals("x1x2x3x", NameConvention.escapeEntityNameStrict("x1x2x3x"));
		assertEquals("a", NameConvention.escapeEntityNameStrict("@#23a"));
		assertEquals("x", NameConvention.escapeEntityNameStrict("@#234567x"));
		assertEquals("x234567", NameConvention.escapeEntityNameStrict("x@#234567"));
		assertEquals("x1234", NameConvention.escapeEntityNameStrict("@#234567x1234"));
		assertEquals("x", NameConvention.escapeEntityNameStrict("1x"));
		assertEquals("x1", NameConvention.escapeEntityNameStrict("x1"));
		assertEquals("xx", NameConvention.escapeEntityNameStrict("11xx"));
		assertEquals("xx11", NameConvention.escapeEntityNameStrict("xx11"));
		assertEquals("xx", NameConvention.escapeEntityNameStrict("x!@#$x"));
		assertEquals("xx1", NameConvention.escapeEntityNameStrict("5#%x!@#$x$#1"));
	}

	@Test
	public void testEscapeFileNames() throws Exception {
		assertEquals("123name", NameConvention.escapeFileName("123name"));
		assertEquals("123name", NameConvention.escapeFileName("123nAmE"));
		assertEquals("name", NameConvention.escapeFileName("n%a@m*e"));
		assertEquals("_name_", NameConvention.escapeFileName("_N%A@M*E_"));
		assertEquals("123x1x1x1", NameConvention.escapeFileName("$$^123!x1#X1x%&1"));
	}

	@Test
	public void testValidateEntityNames() throws DatabaseException{
		
		NameConvention.validateEntityNameStrict("name_1234567890");
		NameConvention.validateEntityNameStrict("myName");
		NameConvention.validateEntityNameStrict("_345_myName");
		NameConvention.validateEntityNameStrict("_345_XxxxXX_myName_");
		
		Throwable e = null;
		try {
			NameConvention.validateEntityNameStrict("123name");
		} catch (Throwable ex) {
			e = ex;
		}
		assertTrue(e instanceof DatabaseException);
		
		try {
			NameConvention.validateEntityNameStrict("name@#$");
		} catch (Throwable ex) {
			e = ex;
		}
		assertTrue(e instanceof DatabaseException);
		
		try {
			NameConvention.validateEntityNameStrict("@#23");
		} catch (Throwable ex) {
			e = ex;
		}
		assertTrue(e instanceof DatabaseException);
		
		try {
			NameConvention.validateEntityNameStrict("123");
		} catch (Throwable ex) {
			e = ex;
		}
		assertTrue(e instanceof DatabaseException);
		
		try {
			NameConvention.validateEntityNameStrict("@##%#%");
		} catch (Throwable ex) {
			e = ex;
		}
		assertTrue(e instanceof DatabaseException);

	}
	
	@Test
	public void testValidateFileNames() throws DatabaseException{
		
		NameConvention.validateFileName("name");
		NameConvention.validateFileName("xxxxxx");
		NameConvention.validateFileName("123_name");
		NameConvention.validateFileName("_____");
		NameConvention.validateFileName("456472");
		NameConvention.validateFileName("n_a_m_e_5_3");
		
		Throwable e = null;
		try {
			NameConvention.validateFileName("nam%e123@#");
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
		
	}

}