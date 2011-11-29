package regressiontest.excel;

import java.io.File;

import org.molgenis.framework.db.Database;

import app.DatabaseFactory;
import app.ExcelExport;

public class TestExcelExport {

	public TestExcelExport() throws Exception{
		
		Database db = DatabaseFactory.create("xgap.test.properties");
		//new emptyDatabase(db);
		//db.remove(db.find(Species.class));
		//db.remove(db.find(Strain.class));
		//db.remove(db.find(Investigation.class));
		
		//File file = new File(new File("").getAbsolutePath() + "/handwritten/java/regressiontest/excel/tomato.xls");
		
		//ExcelImport.importAll(file, db, null);
		
		File file = new File(System.getProperty("java.io.tmpdir") + File.separator + "file.xls");

		new ExcelExport().exportAll(file, db, true);
		
	}
	
	public static void main(String[] args) throws Exception {
		new TestExcelExport();
	}

}
