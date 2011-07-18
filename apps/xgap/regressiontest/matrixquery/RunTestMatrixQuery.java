package regressiontest.matrixquery;

import java.io.File;

import junit.framework.TestCase;
import matrix.AbstractDataMatrixInstance;
import matrix.general.DataMatrixHandler;
import matrix.test.implementations.general.Helper;

import org.junit.Test;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;

import regressiontest.matrixquery.help.DB;
import app.JDBCDatabase;

/**
 * Use test database to add example data and try out different query/sorting functions.
 * 
 * To add a storage directory to this test database without using the GUI, do:
 * use test_xgap_1_4_distro;
 * create table systemsettings_090527PBDB00QCGEXP4G (filedirpath VARCHAR(255), verified BOOL DEFAULT 0);
 * insert into systemsettings_090527PBDB00QCGEXP4G (filedirpath, verified) values ('C:\data', 1);
 * 
 * @author joerivandervelde
 *
 */
public class RunTestMatrixQuery extends TestCase {
	
	Database db = null;
	AbstractDataMatrixInstance<Object> baseMatrix = null;
	
	public RunTestMatrixQuery() throws Exception{
		db = new JDBCDatabase("handwritten/apps/org/molgenis/xgap/xgap.test.properties");
		assertTrue(Helper.storageDirsAreAvailable(db));
		assertTrue(DB.removeData(db));
		assertTrue(new DB().importExampleData(db));
		Data data = db.find(Data.class).get(0);
		DataMatrixHandler dmh = new DataMatrixHandler(db);
		baseMatrix = dmh.createInstance(data);
	}
	
	@Test
	public void testSubMatrixFilterByRowEntityValues() throws Exception{
		QueryRule q1 = new QueryRule("name", Operator.EQUALS, "Butenyl");
		QueryRule or = new QueryRule(Operator.OR);
		QueryRule q2 = new QueryRule("name", Operator.EQUALS, "Methylthiopentyl");
		AbstractDataMatrixInstance<Object> testMatrix = baseMatrix.getSubMatrixFilterByRowEntityValues(db, q1, or, q2);
		
		File testAgainstFile = new File(this.getClass().getResource("help/expectedoutput/filterbyrowentityvalues").getFile().replace("%20", " "));
		String testAgainst = Helper.readFileToString(testAgainstFile);
		
		assertEquals(testAgainst, testMatrix.toString());
	}
	
	@Test
	public void testSubMatrixFilterByColEntityValues() throws Exception{
		QueryRule q1 = new QueryRule("name", Operator.EQUALS, "X4");
		QueryRule or = new QueryRule(Operator.OR);
		QueryRule q2 = new QueryRule("name", Operator.EQUALS, "X193");
		AbstractDataMatrixInstance<Object> testMatrix = baseMatrix.getSubMatrixFilterByColEntityValues(db, q1, or, q2);
	
		File testAgainstFile = new File(this.getClass().getResource("help/expectedoutput/filterbycolentityvalues").getFile().replace("%20", " "));
		String testAgainst = Helper.readFileToString(testAgainstFile);
		
		assertEquals(testAgainst, testMatrix.toString());
	}
	
	@Test
	public void testSubMatrixFilterByRowMatrixValues() throws Exception{
		QueryRule q1 = new QueryRule("Butenyl", Operator.GREATER, "10000");
		AbstractDataMatrixInstance testMatrix = baseMatrix.getSubMatrixFilterByRowMatrixValues(q1);
		System.out.println(testMatrix.toString());

		File testAgainstFile = new File(this.getClass().getResource("help/expectedoutput/filterbyrowmatrixvalues").getFile().replace("%20", " "));
		String testAgainst = Helper.readFileToString(testAgainstFile);
		
		assertEquals(testAgainst, testMatrix.toString());
	}
	
	@Test
	public void testSubMatrixFilterByColMatrixValues() throws Exception{
		QueryRule q1 = new QueryRule("X10", Operator.GREATER, "1000");
		AbstractDataMatrixInstance testMatrix = baseMatrix.getSubMatrixFilterByColMatrixValues(q1);

		File testAgainstFile = new File(this.getClass().getResource("help/expectedoutput/filterbycolmatrixvalues").getFile().replace("%20", " "));
		String testAgainst = Helper.readFileToString(testAgainstFile);
		
		assertEquals(testAgainst, testMatrix.toString());
	}
	

}