package regressiontest.excel;

import java.io.File;

import app.ExcelExport;
import app.JDBCDatabase;


public class TestExcelExport {

	public TestExcelExport() throws Exception{
		
		JDBCDatabase db = new JDBCDatabase("xgap.test.properties");
		//new emptyDatabase(db);
		//db.remove(db.find(Species.class));
		//db.remove(db.find(Strain.class));
		//db.remove(db.find(Investigation.class));
		
		//File file = new File(new File("").getAbsolutePath() + "/handwritten/java/regressiontest/excel/tomato.xls");
		
		//ExcelImport.importAll(file, db, null);
		
		File file = new File(System.getProperty("java.io.tmpdir") + File.separator + "file.xls");

		new ExcelExport().exportAll(file, db, true);
		
		System.out.println("result: " + file.getAbsolutePath());
		
		
	}
	public static void main(String[] args) throws Exception {
		new TestExcelExport();
	}

}
