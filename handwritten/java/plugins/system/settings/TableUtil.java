package plugins.system.settings;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.util.Tuple;

import app.JDBCDatabase;

public class TableUtil {

	public static boolean removeTable(Database db, String tableName)
			throws Exception {
		JDBCDatabase db_ = (JDBCDatabase) db;
		return removeTable(db_, tableName);
	}

	public static boolean removeTable(JDBCDatabase db, String tableName) {
		boolean success = false;
		Statement stmt = null;
		Connection conn = null;
		try {
			conn = db.getConnection();
			stmt = conn.createStatement();
			stmt.execute("DROP TABLE " + tableName + ";");
			success = true;
			JDBCDatabase.closeStatement(stmt);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return success;
	}

	public static boolean addSystemSettingsTable(Database db, String tableName, String fieldName)
			throws Exception {
		JDBCDatabase db_ = (JDBCDatabase) db;
		return addSystemSettingsTable(db_, tableName, fieldName);
	}

	public static boolean addSystemSettingsTable(JDBCDatabase db, String tableName, String fieldName) {
		boolean success = false;
		Statement stmt = null;
		Connection conn = null;
		try {
			conn = db.getConnection();
			stmt = conn.createStatement();
			stmt.execute("CREATE TABLE " + tableName
					+ " ("+fieldName+" VARCHAR(255), verified BOOL DEFAULT 0);");
			success = true;
			JDBCDatabase.closeStatement(stmt);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return success;
	}
	
	public static boolean updateInTable(Database db, String tableName, String fieldName, String value, String where){
		JDBCDatabase db_ = (JDBCDatabase) db;
		return updateInTable(db_, tableName, fieldName, value, where);
	}
		
	public static boolean updateInTable(JDBCDatabase db, String tableName, String fieldName, String value, String where){
		boolean success = false;
		Statement stmt = null;
		Connection conn = null;
		try {
			conn = db.getConnection();
			stmt = conn.createStatement();
			System.out.println("UPDATE " + tableName + " SET " + fieldName + "=" + value + " WHERE "+where.replace("\\", "\\\\")+";");
			stmt.execute("UPDATE " + tableName + " SET " + fieldName + "=" + value + " WHERE "+where.replace("\\", "\\\\")+";");
			success = true;
			JDBCDatabase.closeStatement(stmt);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return success;
	}
	
	public static boolean insertInTable(Database db, String tableName, String fieldName, String value){
		JDBCDatabase db_ = (JDBCDatabase) db;
		return insertInTable(db_, tableName, fieldName, value);
	}

	public static boolean insertInTable(JDBCDatabase db, String tableName, String fieldName, String value){
		boolean success = false;
		Statement stmt = null;
		Connection conn = null;
		try {
			conn = db.getConnection();
			stmt = conn.createStatement();
			stmt.execute("INSERT INTO " + tableName + " (" + fieldName + ") values ('" + value.replace("\\", "\\\\") + "');");
			success = true;
			JDBCDatabase.closeStatement(stmt);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return success;
	}
	
	public static Object getFromTable(Database db, String tableName, String fieldName) {
		JDBCDatabase db_ = (JDBCDatabase) db;
		return getFromTable(db_, tableName, fieldName);
	}
	public static Object getFromTable(JDBCDatabase db, String tableName, String fieldName) {
		Object res = null;
		Statement stmt = null;
		Connection conn = null;
		try {
			conn = db.getConnection();
			stmt = conn.createStatement();
			stmt.execute("SELECT " + fieldName + " FROM " + tableName + ";");
			ResultSet rs = stmt.getResultSet();
			boolean pass = false;
			while(rs.next()){
				if(pass != false){
					throw new Exception("More than one result");
				}
				res = rs.getObject(1);
				pass = true;
			}
			JDBCDatabase.closeStatement(stmt);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return res;
	}

	public static String hasTable(Database db, String tableName) {
		JDBCDatabase db_ = (JDBCDatabase) db;
		return hasTable(db_, tableName);
	}

	public static String hasTable(JDBCDatabase db, String tableName) {
		try {
			List<Tuple> res = db.sql("show tables");
			List<String> tableNames = new ArrayList<String>(res.size());
			for (Tuple t : res) {
				tableNames.add(t.getString(1).toLowerCase());
			}
			if (tableNames.contains(tableName.toLowerCase())) {
				return "true";
			} else {
				return "false";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}
}
