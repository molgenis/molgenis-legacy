package org.molgenis.hemodb;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Locale;

import javax.sql.DataSource;

import jxl.Workbook;
import jxl.WorkbookSettings;

import org.apache.commons.dbcp.BasicDataSource;
import org.hamcrest.generator.qdox.parser.structs.LocatedDef;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.hemodb.excel.HemoSampleExcelReader;

import app.DatabaseFactory;


public class ImportSampleTestje
{

	public static void main(String[] args) throws IndexOutOfBoundsException, DatabaseException, IOException, Exception{
		File excelFile = new File("C:/data/hemodb/imports/hemodbAnnotations.xls");
		
		WorkbookSettings ws = new WorkbookSettings();
		System.out.println(Locale.getDefault());
		Locale saveTheDefault = Locale.getDefault();
		
		Locale.setDefault(Locale.US);
		System.out.println(Locale.getDefault());
		
		
		Workbook workbook = Workbook.getWorkbook(excelFile, ws);
		ArrayList<String> sheetNames = new ArrayList<String>();
		for(String sheetName : workbook.getSheetNames()){
			sheetNames.add(sheetName.toLowerCase());
		}
		
		BasicDataSource data_src = new BasicDataSource();
		data_src.setDriverClassName("org.hsqldb.jdbcDriver");
		data_src.setUsername("sa");
		data_src.setPassword("");
		data_src.setUrl("jdbc:hsqldb:file:hsqldb/molgenisdb;shutdown=true");
		//data_src.setMaxIdle(10);
		//data_src.setMaxWait(1000);
		data_src.setInitialSize(10);
		data_src.setTestOnBorrow(true);
		DataSource dataSource = (DataSource)data_src;
		Connection conn = dataSource.getConnection();
		//Database db = new app.JDBCDatabase(conn);
		Database db = DatabaseFactory.create(conn);	
		
		new HemoSampleExcelReader().importSheet(db, workbook.getSheet(sheetNames.indexOf("hemosample")), null, DatabaseAction.ADD, "");
	
		Locale.setDefault(saveTheDefault);
		System.out.println(Locale.getDefault());
	}
	
}
