package regressiontest.excel;

import java.io.File;

import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Panel;
import org.molgenis.pheno.Species;

import app.ExcelImport;
import app.JDBCDatabase;

public class TestExcelImport {

	public TestExcelImport() throws Exception{
		
		JDBCDatabase db = new JDBCDatabase("xgap.test.properties");
		//new emptyDatabase(db);
		db.remove(db.find(Panel.class));
		db.remove(db.find(Species.class));
		db.remove(db.find(Investigation.class));
		
		File file = new File(new File("").getAbsolutePath() + "/handwritten/java/regressiontest/excel/tomato.xls");
		
		ExcelImport.importAll(file, db, null);
		
	}
	public static void main(String[] args) throws Exception {
		new TestExcelImport();
	}

}
