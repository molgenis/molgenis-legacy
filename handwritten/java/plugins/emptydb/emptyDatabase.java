package plugins.emptydb;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.util.cmdline.CmdLineException;

import app.JDBCDatabase;

public class emptyDatabase {
	
	/**
	 * Empties the supplied database and then runs the generated create_tables.sql
	 * and, if desired, insert_metadata.sql.
	 * 
	 * @param db
	 * @param insertMetadata Run generated insert_metadata.sql or not
	 * @throws Exception
	 */
	public emptyDatabase(JDBCDatabase db, boolean insertMetadata) throws Exception {
		empty(db, "../../create_tables.sql");
		if (insertMetadata == true) {
			empty(db, "../../insert_metadata.sql");
		}
	}


	private void empty(JDBCDatabase db, String sqlScriptName) throws SQLException, DatabaseException, FileNotFoundException, IOException, CmdLineException {
		Connection conn = null;
		try {
			InputStream fis = this.getClass().getResourceAsStream(sqlScriptName);
			conn = db.getConnection();
			String create_tables_sql = "";
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(fis));
				String line;
				while ((line = in.readLine()) != null) {
					create_tables_sql += line + "\n";
				}
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Statement stmt = conn.createStatement();
			int i = 0;
			for (String command : create_tables_sql.split(";")) {
				if (command.trim().length() > 0) {
					try {
						stmt.executeUpdate(command + ";");
						if (i++ % 10 == 0){
							//System.out.print(".");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}
}
