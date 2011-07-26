package matrix.test.implementations;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;
import matrix.test.implementations.general.Helper;
import matrix.test.implementations.general.Params;
import matrix.test.implementations.general.TestMatrix;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.molgenis.framework.db.Database;

import app.servlet.MolgenisServlet;

/**
 * Test data matrix import and export across all backends, all retrieval functions,
 * data types, and most dimensions, transpositions, sparsities, and text length variation.
 * 
 * !! WARNING !!
 * Running the test might DELETE records and files from the current database.
 *
 * To run the test:
 * - Generate test XGAP (apps/org/molgenis/xgap/XgapTestGenerate)
 * - Update test database (apps/org/molgenis/xgap/XgapUpdateTestDatabase)
 * - Set storage directory:
 * -- Either in the GUI (has validation):
 *		Menu 'Admin and settings', tab 'Settings' and follow instructions.
 * -- Or by inserting SQL (no validation, you have to be sure):
 *		NOTE! Replace 'YOURDIRECTORY' with your storage dir, e.g. 'data/test' for unix like or 'C:\data\test' for windows like.
		use test_xgap_1_5;
		create table systemsettings_090527PBDB00QCGEXP4G (filedirpath VARCHAR(255), verified BOOL DEFAULT 0);
		insert into systemsettings_090527PBDB00QCGEXP4G (filedirpath, verified) values ('YOURDIRECTORY', 1);
 *
 * @author joerivandervelde
 *
 */
@RunWith(value = Parameterized.class)
public class RunTestMatrix extends TestCase {

	private TestMatrix tm;
	private Database db;
	
	public RunTestMatrix(Params params) throws Exception {
		db = new MolgenisServlet().getDatabase();
		tm = new TestMatrix(db, params);
	}

	@Parameters
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] {
				//			 DIM DIM TEXT	FIX.T?  SPARSE? R.TEST? P.TEST? SKIPEL.?
				{ new Params(1,	 1,  0,		false,	false,	true,	false,	false) },
				{ new Params(1,	 1,  0,		true,	true,	true,	false,	false) },
				{ new Params(1,	 1,  1,		false,	true,	true,	false,	false) },
				{ new Params(1,	 1,  1,		true,	false,	true,	false,	false) },
				{ new Params(20, 10, 50,	false,	true,	true,	false,	false) },
				{ new Params(10, 20, 50,	true,	false,	true,	false,	false) },
				{ new Params(20, 10, 50,	false,	false,	true,	false,	false) },
				{ new Params(10, 20, 50,	true,	true,	true,	false,	false) },
				{ new Params(75, 95, 2,		false,	true,	true,	false,	true) },
				{ new Params(95, 75, 2,		true,	false,	true,	false,	true) },
				{ new Params(100, 5, 127,	false,	false,	true,	false,	true) },
				{ new Params(5, 100, 127,	true,	true,	true,	false,	true) }
				};
		return Arrays.asList(data);
	}
	
	@BeforeClass
	public static void print(){
		Helper.printEmptyRLists();
	}

	@Before
	public void assertAppDirAvailable() throws Exception {
		assertTrue(Helper.storageDirsAreAvailable(db));
	}

	@After
	public void printPerformance() {
		Helper.printForR(tm);
	}

	@Test
	public void binary() throws Exception{
		tm.runBinary();
	}

	@Test
	public void database() throws Exception {
		tm.runDatabase();
	}

	@Test
	public void file() throws Exception {
		tm.runFile();
	}
	
	@Test
	public void memory() throws Exception {
		tm.runMemory();
	}
	
}