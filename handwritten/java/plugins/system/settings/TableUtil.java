package plugins.system.settings;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.util.Tuple;

import app.JDBCDatabase;

/**
 * Low-level helper functions for simple database actions, outside the regular generated API.
 * Used for special tables which should not be touched normally.
 * @author joerivandervelde
 *
 */
public class TableUtil {

	/**
	 * Drop a table with this name.
	 * @param db
	 * @param tableName
	 * @return
	 * @throws DatabaseException
	 */
	public static boolean removeTable(Database db, String tableName) throws DatabaseException {
		JDBCDatabase db_ = (JDBCDatabase)db;
		return db_.executeSql("DROP TABLE " + tableName + ";");
	}

	/**
	 * Specific function. Add the system settings table with this name, plus a bool field for a setting.
	 * @param db
	 * @param tableName
	 * @param fieldName
	 * @return
	 */
	public static boolean addSystemSettingsTable(Database db, String tableName, String fieldName) {
		JDBCDatabase db_ = (JDBCDatabase)db;
		return db_.executeSql("CREATE TABLE " + tableName + " ("+fieldName+" VARCHAR(255), verified BOOLEAN DEFAULT 0);");
	}

	/**
	 * Update a field with a new value in a table. Also uses a WHERE clause which is specially escaped to accomodate file paths.
	 * @param db
	 * @param tableName
	 * @param fieldName
	 * @param value
	 * @param where
	 * @return
	 */
	public static boolean updateInTable(Database db, String tableName, String fieldName, String value, String where){
		JDBCDatabase db_ = (JDBCDatabase)db;
		return db_.executeSql("UPDATE " + tableName + " SET " + fieldName + "=" + value + " WHERE "+where.replace("\\", "\\\\")+";");
	}


	/**
	 * Insert a value for a field in a table.
	 * @param db
	 * @param tableName
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public static boolean insertInTable(Database db, String tableName, String fieldName, String value){
		JDBCDatabase db_ = (JDBCDatabase)db;
		return db_.executeSql("INSERT INTO " + tableName + " (" + fieldName + ") values ('" + value.replace("\\", "\\\\") + "');");
	}
	
	/**
	 * Get a setting (value) from a field in a table. Only retrieves if resultset is 1.
	 * @param db
	 * @param tableName
	 * @param fieldName
	 * @return
	 * @throws DatabaseException
	 */
	public static Object getFromTable(Database db, String tableName, String fieldName) throws DatabaseException {
		System.out.println("** getFromTable CALLED!: tablname = "+ tableName + " field = " + fieldName);
		JDBCDatabase db_ = (JDBCDatabase)db;
		List<Tuple> res = db_.sql("SELECT " + fieldName + " FROM " + tableName + ";");
		System.out.println("** RES: " + res.toString());
		if(res.size() > 1){
			throw new DatabaseException("More than one result");
		}
		System.out.println("** returning: " + res.get(0).getObject(0));
		return res.get(0).getObject(0);
	}
	
	/**
	 * Find out if the database has a table with this name.
	 * @param db
	 * @param tableName
	 * @return
	 */
	public static String hasTable(Database db, String tableName) {
		try {
			JDBCDatabase db_ = (JDBCDatabase)db;
			List<Tuple> res = null;
			if(db_.getSource().getDriverClassName().contains("hsql")){
				System.out.println("db.getSource().getDriverClassName() " + db_.getSource().getDriverClassName());
				res = db_.sql("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.SYSTEM_COLUMNS WHERE TABLE_NAME NOT LIKE 'SYSTEM_%';");
			}else{
				System.out.println("db.getSource().getDriverClassName() " + db_.getSource().getDriverClassName());
				res = db_.sql("SHOW tables;");
			}
			List<String> tableNames = new ArrayList<String>(res.size());
			for (Tuple t : res) {
				if(db_.getSource().getDriverClassName().contains("hsql")){
					tableNames.add(t.getString(0).toLowerCase());
				}else{
					tableNames.add(t.getString(1).toLowerCase());
				}
			}
			if (tableNames.contains(tableName.toLowerCase())) {
				System.out.println("** TRUE");
				return "true";
			} else {
				System.out.println("** VALS");
				return "false";
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("** ERROR");
			return "error";
		}
	}
}
